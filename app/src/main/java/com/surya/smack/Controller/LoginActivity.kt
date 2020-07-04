package com.surya.smack.Controller

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.surya.smack.R

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Log.d("LoginNav","Login activity is here")
    }

    fun loginLoginOnClicked(view : View){

    }
    fun loginSignUpOnClicked(view : View){
        val signUpIntent = Intent(this, CreateUserActivity:: class.java)
        startActivity(signUpIntent)
        finish()
    }

}