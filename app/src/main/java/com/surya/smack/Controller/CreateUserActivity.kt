package com.surya.smack.Controller

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.surya.smack.R
import com.surya.smack.Services.AuthService
import com.surya.smack.Services.UserDataService
import com.surya.smack.Utilities.BROADCAST_USER_DATA_CHANGE
import kotlinx.android.synthetic.main.activity_create_user.*
import java.util.*

class CreateUserActivity : AppCompatActivity() {

    var userAvatar ="profileDefault"
    var avatarColor = "[0.5, 0.5, 0.5, 1]"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)

        createSpinner.visibility = View.INVISIBLE
    }

    fun generateUserAvatar(view : View){
        val random = Random()
        val color = random.nextInt()
        val avatar =random.nextInt(28)
        if(color % 2 == 0){
            userAvatar = "light$avatar"
        }
        else{
            userAvatar = "dark$avatar"
        }
        val resourceId = resources.getIdentifier(userAvatar,"drawable", packageName)
        createAvatarImageView.setImageResource(resourceId)
    }

    fun generateColorClicked(view : View){
        val random = Random()
        val r = random.nextInt(255)
        val g = random.nextInt(255)
        val b = random.nextInt(255)
        createAvatarImageView.setBackgroundColor(Color.rgb(r,g,b))
        //covert for sending to db
        val savedR = r.toDouble()/255
        val savedG = r.toDouble()/255
        val savedB = r.toDouble()/255
        avatarColor = "[$savedR, $savedG, $savedB, 1]"
        //println(avatarColor)
    }
    fun createUserClicked(view: View){
        enableSpinner(true)

        val userName = createUserNameText.text.toString()
        val email= createEmailText.text.toString()
        val password= creatPasswordText.text.toString()

        if(userName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()){
            AuthService.registerUser(this,email,password){registerSuccess ->
                if(registerSuccess){
                    AuthService.loginUser(this, email,password){loginSuccess ->
                        if(loginSuccess){
                            /*println(AuthService.authToken)
                            println(AuthService.userEmail)*/
                            AuthService.addUser(this,userName,email,userAvatar,avatarColor){createSuccess ->
                                if(createSuccess){
                                    val userDataChange = Intent(BROADCAST_USER_DATA_CHANGE)
                                    LocalBroadcastManager.getInstance(this).sendBroadcast(userDataChange)
                                    enableSpinner(false)
                                    //println("Avatar name: ${UserDataService.avatarName} and Avatar color: ${UserDataService.avatarColor}")
                                    finish()
                                }else{
                                    errorToast()
                                }
                            }
                        } else{
                            errorToast()
                        }
                    }
                }else{
                    errorToast()
                }
            }
        }else{
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
            enableSpinner(false)
        }

    }

    fun errorToast(){
        Toast.makeText(this, "Something went wrong please try again", Toast.LENGTH_SHORT).show()
        enableSpinner(false)
    }

    fun enableSpinner(enable : Boolean){
        if(enable){
            createSpinner.visibility = View.VISIBLE
        }else{
            createSpinner.visibility = View.INVISIBLE
        }
        createUserBtn.isEnabled = !enable
        createAvatarImageView.isEnabled = !enable
        backgroundColorButton.isEnabled = !enable
    }

}