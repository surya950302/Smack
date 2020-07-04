package com.surya.smack.Controller

import android.content.*
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.surya.smack.R
import com.surya.smack.Services.AuthService
import com.surya.smack.Services.UserDataService
import com.surya.smack.Utilities.BROADCAST_USER_DATA_CHANGE
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar,
            R.string.toggle_open,
            R.string.toggle_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        LocalBroadcastManager.getInstance(this).registerReceiver(userDataChangeReceiver, IntentFilter(
            BROADCAST_USER_DATA_CHANGE
        ))

    }

    private val userDataChangeReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            if(AuthService.isLoggedIn){
                userNameNavHeader.text = UserDataService.name
                userEmailNavHeader.text = UserDataService.email

                val resourceId = resources.getIdentifier(UserDataService.avatarName,"drawable", packageName)
                userImageNaveHeader.setImageResource(resourceId)
                userImageNaveHeader.setBackgroundColor(UserDataService.returnAvatarColor(UserDataService.avatarColor))
                loginBtnNavHeader.text = "Logout"
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
                }
                .setNegativeButton("Cancel"){dialog: DialogInterface?, i: Int ->
                    //logic when clicked
                }
                .show()
        }
    }
    fun sendMsgBtnClicked(view : View){

    }

}