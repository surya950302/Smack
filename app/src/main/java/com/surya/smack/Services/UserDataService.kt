package com.surya.smack.Services

import android.graphics.Color
import com.surya.smack.Controller.App
import java.util.*

object UserDataService {
    var id= ""
    var avatarColor = ""
    var avatarName = ""
    var email = ""
    var name = ""

    fun returnAvatarColor( components : String) : Int{
        //"[0.6745098039215687, 0.6745098039215687, 0.6745098039215687, 1]"
        val strippedColor = components
            .replace("[","")
            .replace("]","")
            .replace(",","")
        println(strippedColor)
        var r= 0
        var g = 0
        var b = 0
        //"0.6745098039215687 0.6745098039215687 0.6745098039215687 1"
        val scanner = Scanner(strippedColor)
        if(scanner.hasNext()){
            r = (scanner.nextDouble()*255).toInt()
            g = (scanner.nextDouble()*255).toInt()
            b = (scanner.nextDouble()*255).toInt()
        }
        println("regenerated : r- $r, g- $g, b- $b")
        return Color.rgb(r,g,b)

    }

    fun logout(){
        id= ""
        avatarColor = ""
        avatarName = ""
        email = ""
        name = ""
        App.sp.authToken = ""
        App.sp.userEmail = ""
        App.sp.isLoggedIn = false

    }
}