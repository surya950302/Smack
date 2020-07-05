package com.surya.smack.Services

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.surya.smack.Controller.App
import com.surya.smack.Utilities.*
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception

object AuthService {
    //var isLoggedIn = false
    //var userEmail = ""
    //var authToken = ""
    fun registerUser(email : String, password : String, complete : (Boolean)  -> Unit){

        val jsonBody = JSONObject()
        jsonBody.put("email", email)
        jsonBody.put("password",password)
        val requestBody = jsonBody.toString()

        val registerRequest = object : StringRequest(Method.POST, URL_REGISTER, Response.Listener {response ->
            println(response)
            complete(true)
        }, Response.ErrorListener {error ->
            Log.d("ERROR","could not register user : ${error.printStackTrace()}")
            complete(false)
        }){
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }
            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }
        }

        App.sp.requestQueue.add(registerRequest)

    }

    fun loginUser(email : String, password : String, complete : (Boolean)  -> Unit) {

        val jsonBody = JSONObject()
        jsonBody.put("email", email)
        jsonBody.put("password",password)
        val requestBody = jsonBody.toString()

        val loginRequest = object : JsonObjectRequest(Method.POST, URL_LOGIN, null, Response.Listener {response ->
            //println(response)
            try{
                App.sp.authToken = response.getString("token")
                App.sp.userEmail = response.getString("user")
                App.sp.isLoggedIn = true
                complete(true)
            }catch (e: JSONException){
                Log.d("JSON Exception","${e.printStackTrace()}")
                complete(false)
            }


        },Response.ErrorListener {error ->
            Log.d("ERROR","Could not login user : ${error.printStackTrace()}")
            complete(false)
        }){
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }
        }
        App.sp.requestQueue.add(loginRequest) //not good to keep creating new requwsts every time may lead to memory leaks
    }

    fun addUser(name : String, email: String, avatarName : String, avatarColor : String, complete: (Boolean) -> Unit){

        val jsonBody = JSONObject()
        jsonBody.put("name", name)
        jsonBody.put("email", email)
        jsonBody.put("avatarName", avatarName)
        jsonBody.put("avatarColor", avatarColor)
        val requestBody = jsonBody.toString()

        val creatUserRequest = object  : JsonObjectRequest(Method.POST, URL_CREATE_USER, null, Response.Listener {response ->
            try {
                UserDataService.name = response.getString("name")
                UserDataService.email = response.getString("email")
                UserDataService.avatarName = response.getString("avatarName")
                UserDataService.avatarColor = response.getString("avatarColor")
                UserDataService.id = response.getString("_id")
                complete(true)
            }catch(e: JSONException) {
                Log.d("JSON Exception","EXC : ${e.printStackTrace()}")
                complete(false)
            }

        }, Response.ErrorListener {error ->
            Log.d("ERROR","could not create user : ${error.printStackTrace()}")
            complete(false)
        }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                try{
                    Log.d("Auth Token","${App.sp.authToken}")
                    headers.put("Authorization", "Bearer ${App.sp.authToken}")
                }catch (e: Exception){
                    println("The token is not defined: ${e.printStackTrace()}")
                }
                return headers
            }
        }

        App.sp.requestQueue.add(creatUserRequest)
    }

    fun finfUserByEmail(context: Context, complete: (Boolean) -> Unit){

        val findUserRequest = object : JsonObjectRequest(Method.GET, "$URL_GET_USER${App.sp.userEmail}", null, Response.Listener { response ->
            try{
                UserDataService.name = response.getString("name")
                UserDataService.email = response.getString("email")
                UserDataService.avatarName = response.getString("avatarName")
                UserDataService.avatarColor = response.getString("avatarColor")
                UserDataService.id = response.getString("_id")

                val userDataChange = Intent(BROADCAST_USER_DATA_CHANGE)
                LocalBroadcastManager.getInstance(context).sendBroadcast(userDataChange)

                complete(true)

            }catch(e : JSONException){
                Log.d("JSON Exception","EXC : ${e.printStackTrace()}")
                complete(false)
            }

        },Response.ErrorListener {error ->
            Log.d("ERROR","could not get user : ${error.printStackTrace()}")
            complete(false)
        }){
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                try{
                    //Log.d("Auth Token","$authToken")
                    headers.put("Authorization", "Bearer ${App.sp.authToken}")
                }catch (e: Exception){
                    println("The token is not defined: ${e.printStackTrace()}")
                }
                return headers
            }
        }
        App.sp.requestQueue.add(findUserRequest)
    }

}