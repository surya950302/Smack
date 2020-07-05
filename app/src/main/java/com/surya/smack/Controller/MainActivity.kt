package com.surya.smack.Controller

import android.content.*
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.surya.smack.Adapters.MessageAdapter
import com.surya.smack.Model.Message
import com.surya.smack.R
import com.surya.smack.Services.AuthService
import com.surya.smack.Services.MessageService
import com.surya.smack.Services.UserDataService
import com.surya.smack.Utilities.BROADCAST_USER_DATA_CHANGE
import com.surya.smack.Utilities.SOCKET_URL
import io.socket.client.IO
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.*
import kotlinx.coroutines.channels.Channel

class MainActivity : AppCompatActivity() {

    val socket = IO.socket(SOCKET_URL)
    lateinit var channelAdapter : ArrayAdapter<com.surya.smack.Model.Channel>
    var selectedChannel : com.surya.smack.Model.Channel? = null
    lateinit var  msgAdapter: MessageAdapter

    private fun setupAdapter(){
        channelAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, MessageService.channels)
        channel_list.adapter = channelAdapter

        msgAdapter = MessageAdapter(this, MessageService.messages)
        messageListView.adapter = msgAdapter
        val layoutManager = LinearLayoutManager(this)
        messageListView.layoutManager = layoutManager
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        socket.connect()
        socket.on("channelCreated",onNewChannel) //onNewChannel is an object of Socket Emitter
        socket.on("messageCreated", onNewMessage )
        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar,
            R.string.toggle_open,
            R.string.toggle_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        setupAdapter()

        LocalBroadcastManager.getInstance(this).registerReceiver(userDataChangeReceiver, IntentFilter(
            BROADCAST_USER_DATA_CHANGE
        ))

        channel_list.setOnItemClickListener { _, _, position, _ ->
            selectedChannel =  MessageService.channels[position]
            drawer_layout.closeDrawer(GravityCompat.START)
            updateWithChannel()
        }

        if(App.sp.isLoggedIn){
            AuthService.finfUserByEmail(this){}
        }
    }

    override fun onDestroy() {
        socket.disconnect()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(userDataChangeReceiver)
        super.onDestroy()
    }


    private val userDataChangeReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context, intent: Intent?) {
            if(App.sp.isLoggedIn){
                userNameNavHeader.text = UserDataService.name
                userEmailNavHeader.text = UserDataService.email

                val resourceId = resources.getIdentifier(UserDataService.avatarName,"drawable", packageName)
                userImageNaveHeader.setImageResource(resourceId)
                userImageNaveHeader.setBackgroundColor(UserDataService.returnAvatarColor(UserDataService.avatarColor))
                loginBtnNavHeader.text = "Logout"

                MessageService.getChannels(){complete ->
                    if(complete){
                        if(MessageService.channels.count() > 0){
                            selectedChannel = MessageService.channels[0]
                            channelAdapter.notifyDataSetChanged()
                            updateWithChannel()
                        }
                    }
                }
            }
        }
    }

    fun updateWithChannel(){
        mainChannelName.text = "#${selectedChannel?.name}"
        //download messages for channel
        if(selectedChannel != null){
            MessageService.getMessages(selectedChannel!!.id){ messageSuccess ->
                if(messageSuccess){
                    for (msg in MessageService.messages){
                         Log.v("messages","${msg.message}")
                        msgAdapter.notifyDataSetChanged()
                        if(msgAdapter.itemCount > 0){
                            messageListView.smoothScrollToPosition(msgAdapter.itemCount-1)
                        }
                    }
                }
            }
        }
    }
    override fun onBackPressed() {
        if(drawer_layout.isDrawerOpen(GravityCompat.START)){
            drawer_layout.closeDrawer(GravityCompat.START)
        }else{
            super.onBackPressed()
        }
    }


    fun loginBtnNavClicked(view : View){
        Log.d("LoginNav","Login btn clicked")
        if(App.sp.isLoggedIn){
            UserDataService.logout()
            channelAdapter.notifyDataSetChanged()
            msgAdapter.notifyDataSetChanged()
            userEmailNavHeader.text = ""
            userNameNavHeader.text = "Login"
            userImageNaveHeader.setImageResource(R.drawable.profiledefault)
            userImageNaveHeader.setBackgroundColor(Color.TRANSPARENT)
            loginBtnNavHeader.text = "Login"
            mainChannelName.text = "#Login Please"
        }else{
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }

    }
    fun addChannelClicked(view : View){
        Log.d("Add Channel","I am adding channel")
        if(App.sp.isLoggedIn){
            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.add_channel_dialog, null)

            builder.setView(dialogView)
                .setPositiveButton("Add"){ _, _ ->
                    //logic when clicked
                    //cant get the fields using the synthetics import as that works only for fields on activity_main.xml
                    val nameTextFiled = dialogView.findViewById<EditText>(R.id.addChannelNameText)
                    val channelDecsTextFiled = dialogView.findViewById<EditText>(R.id.addChannelDecsText)
                    val channelName = nameTextFiled.text.toString()
                    val channelDesc = channelDecsTextFiled.text.toString()

                    //Create Channel
                    socket.emit("newChannel", channelName,channelDesc)
                }
                .setNegativeButton("Cancel"){_, _ ->
                    //logic when clicked
                }
                .show()
        }
    }

    private val onNewChannel = Emitter.Listener { args ->
        //this run on a worker thread so we need to make it run on the main thread thats why the lambda expression
        if(App.sp.isLoggedIn){
            runOnUiThread {
                //println(args[0] as String)
                val channelName = args[0] as String
                val channelDecs = args[1] as String
                val channelId = args[2] as String

                val newChannel = com.surya.smack.Model.Channel(channelName,channelDecs,channelId)
                MessageService.channels.add(newChannel)
                println(MessageService.channels)
                channelAdapter.notifyDataSetChanged()
            }
        }

    }

    private val onNewMessage = Emitter.Listener { args ->
        if(App.sp.isLoggedIn){
            runOnUiThread {
                val channelId = args[2] as String
                if(channelId == selectedChannel?.id){
                    val msgBody = args[0] as String
                    val userName = args[3] as String
                    val avatarName = args[4] as String
                    val avatarColor = args[5] as String
                    val id = args[6] as String
                    val timeStamp = args[7] as String

                    val newMessage = Message(msgBody,userName,channelId, avatarName, avatarColor,id, timeStamp)
                    MessageService.messages.add(newMessage)
                    println(newMessage.message)
                    msgAdapter.notifyDataSetChanged()
                    messageListView.smoothScrollToPosition(msgAdapter.itemCount-1)
                }
            }
        }
    }

    fun sendMsgBtnClicked(view : View){
        if(App.sp.isLoggedIn && messageTextField.text.isNotEmpty() && selectedChannel != null ){
            val userId = UserDataService.id
            val channelId = selectedChannel!!.id
            socket.emit("newMessage",
                messageTextField.text.toString(),
                userId,
                channelId,
                UserDataService.name,
                UserDataService.avatarName,
                UserDataService.avatarColor
            )
            messageTextField.text.clear()
            hideKeyBoard()
        }
    }
    //Func to hide keyboard
    fun hideKeyBoard(){
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if(inputManager.isAcceptingText){
            inputManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }
    }

}