package com.surya.smack.Adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.surya.smack.Model.Message
import com.surya.smack.R
import com.surya.smack.Services.UserDataService
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MessageAdapter(val context : Context, val messages : ArrayList<Message>) :  RecyclerView.Adapter<MessageAdapter.ViewHolder>(){

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val userImage = itemView?.findViewById<ImageView>(R.id.messageUserImage)
        val timeStamp = itemView?.findViewById<TextView>(R.id.timeStampLabel)
        val userName = itemView?.findViewById<TextView>(R.id.msgUserNameLabel)
        val msgBody = itemView?.findViewById<TextView>(R.id.msgBodyLabel)

        fun bindMsg(context :Context, msg: Message){
            val resouceId = context.resources.getIdentifier(msg.userAvatar,"drawable",context.packageName)
            userImage.setImageResource(resouceId)
            userImage.setBackgroundColor(UserDataService.returnAvatarColor(msg.userAvatarColor))
            userName.text = msg.userName
            timeStamp.text = returnDateString(msg.timestamp)
            msgBody.text = msg.message
        }

        fun returnDateString(isoString :String) : String{
            val isoFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            isoFormatter.timeZone = TimeZone.getTimeZone("UTC")
            var convertedDate = Date()
            try{
                convertedDate = isoFormatter.parse(isoString)
            }catch (e: ParseException){
                Log.d("Parse", "Cannot parse Date")
            }
            val outDateString = SimpleDateFormat("E,h:mm a", Locale.getDefault())
            return outDateString.format(convertedDate)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val  view = LayoutInflater.from(context).inflate(R.layout.message_list_view, parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return messages.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder?.bindMsg(context, messages[position])
    }
}