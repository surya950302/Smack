package com.surya.smack.Controller

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.surya.smack.R
import com.surya.smack.Services.AuthService
import kotlinx.android.synthetic.main.activity_create_user.*
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Log.d("LoginNav","Login activity is here")
        loginSpinner.visibility = View.INVISIBLE
    }

    fun loginLoginOnClicked(view : View){
        enableSpinner(true)
        hideKeyBoard()
        val email = loginEmailText.text.toString()
        val password = loginPasswordText.text.toString()
        //hideKeyBoard()
        if(email.isNotEmpty() && password.isNotEmpty()){
            AuthService.loginUser(email,password){loginSuccess ->
                if(loginSuccess){
                    AuthService.finfUserByEmail(this){findSuccess ->
                        if(findSuccess){
                            enableSpinner(false)
                            finish()
                        }else{
                            errorToast()
                        }

                    }
                }else{
                    errorToast()
                }

            }
        }else{
            Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show()
            enableSpinner(false)
        }

    }
    fun loginSignUpOnClicked(view : View){
        val signUpIntent = Intent(this, CreateUserActivity:: class.java)
        startActivity(signUpIntent)
        finish()
    }

    fun errorToast(){
        Toast.makeText(this, "Something went wrong please try again", Toast.LENGTH_SHORT).show()
        enableSpinner(false)
    }

    fun enableSpinner(enable : Boolean){
        if(enable){
            loginSpinner.visibility = View.VISIBLE
        }else{
            loginSpinner.visibility = View.INVISIBLE
        }
        loginLoginBtn.isEnabled = !enable
        loginSignUpBtn.isEnabled = !enable
    }

    //Func to hide keyboard
    fun hideKeyBoard(){
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if(inputManager.isAcceptingText){
            inputManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }
    }

}