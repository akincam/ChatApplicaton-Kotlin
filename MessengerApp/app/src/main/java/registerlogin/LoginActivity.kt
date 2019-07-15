package registerlogin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.example.messengerapp.LatestMessagesActivity
import com.example.messengerapp.R
import com.example.messengerapp.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import database.LoginUserDatabase
import database.UserInfo
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*

class LoginActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        back_to_register_textview.setOnClickListener{
            finish()
            Animatoo.animateSlideRight(this);
        }

        login_button.setOnClickListener{
            val email   = email_login_edittext.text.toString()
            val password= password_login_edittext.text.toString()

            if(email.length ==0 || password.length == 0){
                Toast.makeText(this@LoginActivity, "Missing Info.",
                    Toast.LENGTH_SHORT).show()
            }else if(password.length < 0){
                Toast.makeText(this@LoginActivity, "Password must be at least 8 character.",
                    Toast.LENGTH_SHORT).show()
            }else{
                performLogin(email,password)
            }
        }
    }

    private fun performLogin(email : String,password : String){
        val auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this){
            if (it.isSuccessful) {
                //TODO add username
                LoginUserDatabase(this).addUser(UserInfo(email,password,"todooo"))
                val latestMessagesActivity = Intent(this, LatestMessagesActivity::class.java)
                latestMessagesActivity.flags =Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(latestMessagesActivity)
                finish()
                Animatoo.animateSlideLeft(this)
            } else {
                Toast.makeText(this@LoginActivity, "Authentication failed.\n Try Again",
                    Toast.LENGTH_SHORT).show()
            }

        }
    }
}