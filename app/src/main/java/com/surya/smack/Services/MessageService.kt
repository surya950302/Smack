package com.surya.smack.Services

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.surya.smack.Controller.App
import com.surya.smack.Model.Channel
import com.surya.smack.Model.Message
import com.surya.smack.Utilities.URL_GET_CHANNELS
import com.surya.smack.Utilities.URL_GET_MESSAGES
import org.json.JSONException
import java.lang.Exception

object MessageService {

    val channels = ArrayList<Channel>()
    val messages = ArrayList<Message>()

    fun getChannels(complete: (Boolean) -> Unit){

        val channelRequest = object : JsonArrayRequest(Method.GET, URL_GET_CHANNELS, null, Response.Listener {response ->
            try{
                for(x in 0 until response.length()){
                    val channel = response.getJSONObject(x)
                    val name= channel.getString("name")
                    val chanDesc = channel.getString("description")
                    val chanId = channel.getString("_id")

                    val newChannel = Channel(name, chanDesc,chanId)
                    this.channels.add(newChannel)
                }
                complete(true)

            }catch(e: JSONException){
                Log.d("JSON Exception","EXC : ${e.printStackTrace()}")
                complete(false)
            }
        }, Response.ErrorListener {error ->
            Log.d("ERROR","could not get Channels : ${error.printStackTrace()}")
            complete(false)
        }){
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
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
        App.sp.requestQueue.add(channelRequest)
    }

    fun getMessages(channelId : String, complete: (Boolean) -> Unit){

        val url = "$URL_GET_MESSAGES$channelId"
        val messagesRequest = object : JsonArrayRequest(Method.GET, url, null, Response.Listener { response ->
            clearMessages()
            try{
                for(x in 0 until response.length()){
                    val message = response.getJSONObject(x)
                    val msgId = message.getString("_id")
                    val body= message.getString("messageBody")
                    val  channelId = message.getString("channelId")
                    val userName = message.getString("userName")
                    val userAvatar = message.getString("userAvatar")
                    val userAvatarColor = message.getString("userAvatarColor")
                    val time = message.getString("timeStamp")

                    val newmsg = Message(body, userName, channelId, userAvatar, userAvatarColor, msgId, time)
                    this.messages.add(newmsg)
                }
                complete(true)

            }catch(e: JSONException){
                Log.d("JSON Exception","EXC : ${e.printStackTrace()}")
                complete(false)
            }
        }, Response.ErrorListener { error ->
            Log.d("ERROR","could not get messages : ${error.printStackTrace()}")
            complete(false)
        }){
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
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
        App.sp.requestQueue.add(messagesRequest)
    }

    fun clearMessages(){
        messages.clear()
    }
    fun clearChannels(){
        channels.clear()
    }
}