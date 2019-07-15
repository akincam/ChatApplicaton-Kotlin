package com.example.messengerapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import database.ChatDatabase
import database.LastContactsDatabase
import database.LoginUserDatabase
import kotlinx.android.synthetic.main.activity_latest_messages.*
import kotlinx.android.synthetic.main.new_message_activity.*
import kotlinx.android.synthetic.main.user_row_last_message.view.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*
import java.util.*
import kotlin.collections.ArrayList

class LatestMessagesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messages)
        verifyUserIsLoggedIn()

        val layoutManager = LinearLayoutManager(this)
        recycleview_latestmessage.layoutManager = layoutManager
        supportActionBar?.title = "Conversations"
        listConversations()
    }

    fun verifyUserIsLoggedIn(){
        val uid = FirebaseAuth.getInstance().uid
        if(uid == null){
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags =Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId){
            R.id.menu_new_message ->{
                val registerIntent = Intent(this, NewMessageActivity::class.java)
                startActivity(registerIntent)
                Animatoo.animateZoom(this)
            }

            R.id.menu_sign_out ->{
                FirebaseAuth.getInstance().signOut()
                val registerIntent = Intent(this, RegisterActivity::class.java)
                intent.flags =Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                LoginUserDatabase(this).deleteUser()
                startActivity(registerIntent)
                finish()
                Animatoo.animateZoom(this)

            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    fun listConversations(){
        val adapter = GroupAdapter<ViewHolder>()
        recycleview_latestmessage.adapter = adapter
        val tempElements = ArrayList<User>(LastContactsDatabase(this).getContacts())
        Collections.reverse(tempElements)
        tempElements.forEach {
            adapter.add(LastConversation(it))
        }

        adapter.setOnItemClickListener{item, view ->
            val userItem = item as LastConversation
            intent = Intent(view.context, ChatLogin::class.java);
            intent.putExtra("tablename", userItem.user?.username)
            intent.putExtra("profilephoto", userItem.user?.profileImageUrl)
            startActivity(intent)
        }

    }
}
class LastConversation(val user : User?) : Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.user_row_last_message
    }
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.username_textview_new_messagel.text = user?.username
        Glide.with(viewHolder.itemView.context)
            .load(user?.profileImageUrl)
            .apply(RequestOptions.circleCropTransform())
            .into(viewHolder.itemView.imageView_new_messagel);

        viewHolder.itemView.last_messagel.text = ChatDatabase(viewHolder.itemView.context).getMessages(user!!.username).last().message
    }
}