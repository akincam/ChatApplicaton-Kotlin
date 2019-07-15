package com.example.messengerapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.new_message_activity.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*
import database.ContactInfoDb
import android.net.ConnectivityManager





class NewMessageActivity : AppCompatActivity() {
    lateinit var list : ArrayList<ContactInfoDb>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_message_activity)
        val layoutManager = LinearLayoutManager(this)
        recycleview_newmessage.layoutManager = layoutManager
        supportActionBar?.title = "Select User"
        haveNetworkConnection() == true
        fetchUsersFirebase()

    }
    private fun haveNetworkConnection(): Boolean {
        var haveConnectedWifi = false
        var haveConnectedMobile = false

        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.allNetworkInfo
        for (ni in netInfo) {
            if (ni.typeName.equals("WIFI", ignoreCase = true))
                if (ni.isConnected)
                    haveConnectedWifi = true
            if (ni.typeName.equals("MOBILE", ignoreCase = true))
                if (ni.isConnected)
                    haveConnectedMobile = true
        }
        return haveConnectedWifi || haveConnectedMobile
    }
    private fun fetchUsersFirebase(){
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                val count = p0.getChildrenCount()
                val adapter = GroupAdapter<ViewHolder>()
                    p0.children.forEach {
                        val user = it.getValue(User::class.java)
                        if (user != null) {
                            if(FirebaseAuth.getInstance().uid!=user.uid)
                                adapter.add(UserItem(user))
                        }
                    }
                adapter.setOnItemClickListener{item, view ->
                    item.notifyChanged()
                    val userItem = item as UserItem
                    intent = Intent(view.context, ChatLogin::class.java);
                    intent.putExtra("tablename", userItem.user?.username)
                    intent.putExtra("profilephoto", userItem.user?.profileImageUrl)
                    startActivity(intent)
                    finish()
                }
                recycleview_newmessage.adapter = adapter

            }
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }
}
class UserItem(val user : User?) : Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.user_row_new_message
    }
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.username_textview_new_message.text = user?.username
        Glide.with(viewHolder.itemView.context)
            .load(user?.profileImageUrl)
            .apply(RequestOptions.circleCropTransform())
            .into(viewHolder.itemView.imageView_new_message);
    }
}

