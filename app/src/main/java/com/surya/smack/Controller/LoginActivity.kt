package com.surya.smack.Controller

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.surya.smack.R
import com.surya.smack.Services.AuthService
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Log.d("LoginNav","Login activity is here")
    }

    fun loginLoginOnClicked(view : View){
        val email = loginEmailText.text.toString()
        val password = loginPasswordText.text.toString()

        AuthService.loginUser(this, email,password){loginSuccess ->
            if(loginSuccess){
                AuthService.finfUserByEmail(this){findSuccess ->
                    if(findSuccess){
                        finish()
                    }

                }
            }

        }
    }
    fun loginSignUpOnClicked(view : View){
        val signUpIntent = Intent(this, CreateUserActivity:: class.java)
        startActivity(signUpIntent)
        finish()
    }

}