package com.surya.smack.Controller

import android.content.*
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.surya.smack.R
import com.surya.smack.Services.AuthService
import com.surya.smack.Services.MessageService
import com.surya.smack.Services.UserDataService
import com.surya.smack.Utilities.BROADCAST_USER_DATA_CHANGE
import com.surya.smack.Utilities.SOCKET_URL
import io.socket.client.IO
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header_main.*
import kotlinx.coroutines.channels.Channel

class MainActivity : AppCompatActivity() {

    val socket = IO.socket(SOCKET_URL)
    lateinit var channelAdapter : ArrayAdapter<com.surya.smack.Model.Channel>

    private fun setupAdapter(){
        channelAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, MessageService.channels)
        channel_list.adapter = channelAdapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        socket.connect()
        socket.on("channelCreated",onNewChannel) //onNewChannel is an object of Socket Emitter
        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar,
            R.string.toggle_open,
            R.string.toggle_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        setupAdapter()
    }

    override fun onResume() {

        LocalBroadcastManager.getInstance(this).registerReceiver(userDataChangeReceiver, IntentFilter(
            BROADCAST_USER_DATA_CHANGE
        ))
        super.onResume()
    }

    override fun onDestroy() {
        socket.disconnect()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(userDataChangeReceiver)
        super.onDestroy()
    }


    private val userDataChangeReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context, intent: Intent?) {
            if(AuthService.isLoggedIn){
                userNameNavHeader.text = UserDataService.name
                userEmailNavHeader.text = UserDataService.email

                val resourceId = resources.getIdentifier(UserDataService.avatarName,"drawable", packageName)
                userImageNaveHeader.setImageResource(resourceId)
                userImageNaveHeader.setBackgroundColor(UserDataService.returnAvatarColor(UserDataService.avatarColor))
                loginBtnNavHeader.text = "Logout"

                MessageService.getChannels(context){complete ->
                    if(complete){
                        channelAdapter.notifyDataSetChanged()
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
        if(AuthService.isLoggedIn){
            UserDataService.logout()
            userEmailNavHeader.text = ""
            userNameNavHeader.text = "Login"
            userImageNaveHeader.setImageResource(R.drawable.profiledefault)
            userImageNaveHeader.setBackgroundColor(Color.TRANSPARENT)
            loginBtnNavHeader.text = "Login"
        }else{
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }

    }
    fun addChannelClicked(view : View){
        Log.d("Add Channel","I am adding channel")
        if(AuthService.isLoggedIn){
            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.add_channel_dialog, null)

            builder.setView(dialogView)
                .setPositiveButton("Add"){ dialog: DialogInterface?, i: Int ->
                    //logic when clicked
                    //cant get the fields using the synthetics import as that works only for fields on activity_main.xml
                    val nameTextFiled = dialogView.findViewById<EditText>(R.id.addChannelNameText)
                    val channelDecsTextFiled = dialogView.findViewById<EditText>(R.id.addChannelDecsText)
                    val channelName = nameTextFiled.text.toString()
                    val channelDesc = channelDecsTextFiled.text.toString()

                    //Create Channel
                    socket.emit("newChannel", channelName,channelDesc)
                }
                .setNegativeButton("Cancel"){dialog: DialogInterface?, i: Int ->

                    //logic when clicked
                }
                .show()
        }
    }

    private val onNewChannel = Emitter.Listener { args ->
        //this run on a worker thread so we need to make it run on the main thread thats why the lambda expression
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
    fun sendMsgBtnClicked(view : View){

    }

}