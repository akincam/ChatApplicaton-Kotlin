package com.example.messengerapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.kaopiz.kprogresshud.KProgressHUD
import database.LoginUserDatabase
import database.UserInfo
import kotlinx.android.synthetic.main.activity_main.*
import registerlogin.LoginActivity
import java.util.*


class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var selectorPhotoUri : Uri
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkUserAlreadyLogin();
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance();
        register_button.setOnClickListener {
            performRegister()

        }
        already_have_account_textview.setOnClickListener{
            val loginActivity = Intent(this, LoginActivity::class.java)
            startActivity(loginActivity)
            Animatoo.animateZoom(this);
        }

        select_photo_register_button.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,0)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(!(requestCode != 0 && requestCode != Activity.RESULT_OK) && data!= null){
            val uri = data.data
            this.selectorPhotoUri = data.data!!

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,uri)

            select_photo_imageview_register.setImageBitmap(bitmap)

            select_photo_register_button.alpha = 0f

        }
    }

    public override fun onStart() {
        super.onStart()
    }

    private fun performRegister(){
        val email = email_edittext.text.toString()
        val password = password_edittext.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter text in email/password", Toast.LENGTH_SHORT).show()
            return
        }
        if(password.length<8){
            Toast.makeText(this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show()
            return
        }
        else{
            KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.PIE_DETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                if (!it.isSuccessful) {
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    return@addOnCompleteListener
                } else{
                    LoginUserDatabase(this).addUser(UserInfo(email,password,username_edittext.text.toString()))
                    uploadImageFirebaseStorage()
                }

            }
    }
    private fun uploadImageFirebaseStorage(){
        val filename = UUID.randomUUID().toString()
        val ref      =FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectorPhotoUri)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    saveUserToFirebaseDatabase(it.toString())
                }
        }


    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String){
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(uid,username_edittext.text.toString(),profileImageUrl)
        ref.setValue(user)
            .addOnSuccessListener {
                val latestMessagesActivity = Intent(this, LatestMessagesActivity::class.java)
                latestMessagesActivity.flags =Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(latestMessagesActivity)
                finish()
                Animatoo.animateShrink(this)
        }
    }
    private fun checkUserAlreadyLogin(){
        val db = LoginUserDatabase(this)

        if(db.getUser() == null){
            //
        }else {
            val latestMessagesActivity = Intent(this, LatestMessagesActivity::class.java)
            latestMessagesActivity.flags =Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(latestMessagesActivity)
            finish()
            Animatoo.animateSlideLeft(this)
        }
    }
}

class User(val uid : String, val username : String , val profileImageUrl : String){
    constructor() : this("","","")
}
