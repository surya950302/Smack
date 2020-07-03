package com.surya.smack.Controller

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import com.surya.smack.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    //val includeView : View = findViewById(R.id.nav_drawer_header_include)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        /*val includeView : View = findViewById(R.id.nav_drawer_header_include)
        val logBtn : Button= includeView.findViewById(R.id.loginBtnNavHeader)
        logBtn.setOnClickListener { view : View -> Unit
            Log.d("LoginNav","Login btn clicked")
            val loginIntent = Intent(this, LoginActivity :: class.java)
            startActivity(loginIntent)
        }*/
        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar,
            R.string.toggle_open,
            R.string.toggle_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()


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
        val loginIntent = Intent(this, LoginActivity::class.java)
        startActivity(loginIntent)
    }
    fun addChannelClicked(view : View){

    }
    fun sendMsgBtnClicked(view : View){

    }

}