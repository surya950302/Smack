package com.surya.smack.Services

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.surya.smack.Model.Channel
import com.surya.smack.Utilities.URL_GET_CHANNELS
import org.json.JSONException
import java.lang.Exception

object MessageService {

    val channels = ArrayList<Channel>()

    fun getChannels(context: Context, complete: (Boolean) -> Unit){

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
                    Log.d("Auth Token","${AuthService.authToken}")
                    headers.put("Authorization", "Bearer ${AuthService.authToken}")
                }catch (e: Exception){
                    println("The token is not defined: ${e.printStackTrace()}")
                }
                return headers
            }

        }
        Volley.newRequestQueue(context).add(channelRequest)
    }
}