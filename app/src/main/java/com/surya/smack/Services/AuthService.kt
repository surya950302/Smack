package com.surya.smack.Services

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.surya.smack.Utilities.URL_CREATE_USER
import com.surya.smack.Utilities.URL_LOGIN
import com.surya.smack.Utilities.URL_REGISTER
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception

object AuthService {
    var isLoggedIn = false
    var userEmail = ""
    var authToken = ""
    fun registerUser(context: Context, email : String, password : String, complete : (Boolean)  -> Unit){

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

        Volley.newRequestQueue(context).add(registerRequest)

    }

    fun loginUser(context: Context,email : String, password : String, complete : (Boolean)  -> Unit) {

        val jsonBody = JSONObject()
        jsonBody.put("email", email)
        jsonBody.put("password",password)
        val requestBody = jsonBody.toString()

        val loginRequest = object : JsonObjectRequest(Method.POST, URL_LOGIN, null, Response.Listener {response ->
            //println(response)
            try{
                authToken = response.getString("token")
                userEmail = response.getString("user")
                isLoggedIn = true
                complete(true)
            }catch (e: JSONException){
                Log.d("JSON Exception","${e.printStackTrace()}")
                complete(false)
            }


        },Response.ErrorListener {error ->
            Log.d("ERROR","EXC : ${error.printStackTrace()}")
            complete(false)
        }){
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }
        }
        Volley.newRequestQueue(context).add(loginRequest) //not good to keep creating new requwsts every time may lead to memory leaks
    }

    fun addUser(context: Context, name : String, email: String, avatarName : String, avatarColor : String, complete: (Boolean) -> Unit){

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
                    headers.put("Authorization", "Bearer $authToken")
                }catch (e: Exception){
                    println("The token is not defined: ${e.printStackTrace()}")
                }
                return headers
            }
        }

        Volley.newRequestQueue(context).add(creatUserRequest)
    }



}