package com.example.messengerapp

import android.graphics.*
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import co.intentservice.chatui.ChatView
import kotlinx.android.synthetic.main.activity_chat_log.*
import co.intentservice.chatui.models.ChatMessage
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import database.ChatDatabase
import database.LoginUserDatabase
import java.net.URL
import android.graphics.drawable.BitmapDrawable
import android.os.AsyncTask
import database.LastContactsDatabase


class ChatLogin : AppCompatActivity() {
    var userName : String = ""
    var imageUrl : String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        createUserActionBar();

        if(userName!= null) {
            ChatDatabase(this).createTable(userName)
        }
        supportActionBar?.title = userName.toUpperCase()

        getMessages()
        sendMessage()
        messageCome()
    }

    fun getMessages(){
        ChatDatabase(this).getMessages(userName)
        for(chatapp in ChatDatabase(this).getMessages(userName) ) {
            chat_view.addMessage(chatapp)
        }

    }

    fun messageCome(){
        val ref = FirebaseDatabase.getInstance().getReference("/messages/${LoginUserDatabase(this).getUser()?.userName}")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.getValue()!=null) {
                    p0.ref.removeValue()
                    ChatDatabase(this@ChatLogin).addMessage(ChatMessage(((p0.getValue()) as HashMap<*, *>)["message"] as String?, ((p0.getValue()) as HashMap<String, Any>)["timestamp"] as Long, ChatMessage.Type.RECEIVED), userName)
                    chat_view.addMessage((ChatMessage(((p0.getValue()) as HashMap<*, *>)["message"] as String?,
                        ((p0.getValue()) as HashMap<*, *>)["timestamp"] as Long, ChatMessage.Type.RECEIVED)))
                }
            }
            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }

    fun sendMessage(){
        chat_view.setOnSentMessageListener(ChatView.OnSentMessageListener {
            val ref = FirebaseDatabase.getInstance().getReference("/messages/${userName}")

            ChatDatabase(this).addMessage(ChatMessage(it.message,it.timestamp,it.type)
                ,userName)
            val chatMessage = ChatMessage(it.message,it.timestamp,it.type)
            LastContactsDatabase(this).addContacts(User("",userName,imageUrl))
            ref.setValue(ChatMessage(it.message,it.timestamp,it.type)).addOnSuccessListener {
            }
            true
        })
    }

    fun createUserActionBar(){
        val extras = intent.extras
        val exampleString = extras?.getString("tablename")
        userName = exampleString.toString()
        imageUrl = extras?.getString("profilephoto").toString()
        runOnUiThread({
            object : Runnable {
                override fun run() {
                    val url = URL(extras?.get("profilephoto").toString())
                    val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                    getSupportActionBar()?.setDisplayShowHomeEnabled(true)
                    getSupportActionBar()?.setDisplayUseLogoEnabled(true)
                    val d = BitmapDrawable(resources, getCroppedBitmap(bmp))
                    supportActionBar?.setIcon(d)
                }
            }
        })
    }

    fun getCroppedBitmap(bitmap: Bitmap): Bitmap {
        val output = Bitmap.createBitmap(
            bitmap.width,
            bitmap.height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(output)

        val color = -0xbdbdbe
        val paint = Paint()
        val rect = Rect(0, 0, bitmap.width, bitmap.height)

        paint.setAntiAlias(true)
        canvas.drawARGB(0, 0, 0, 0)
        paint.setColor(color)
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(
            (bitmap.width / 2).toFloat(), (bitmap.height / 2).toFloat(),
            (bitmap.width / 2).toFloat(), paint
        )
        paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_IN))
        canvas.drawBitmap(bitmap, rect, rect, paint)
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output
    }
}

