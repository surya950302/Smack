package com.surya.smack

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
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
        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.toggle_open, R.string.toggle_close)
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