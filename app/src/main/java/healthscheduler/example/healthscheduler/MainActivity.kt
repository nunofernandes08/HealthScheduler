package healthscheduler.example.healthscheduler

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = Firebase.auth
        val currentUser = auth.currentUser

        val buttonContinueWithEmailMain = findViewById<Button>(R.id.buttonContinueWithEmailMain)
        val imageViewRegistarMain = findViewById<ImageView>(R.id.imageViewRegistarMain)

        buttonContinueWithEmailMain.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        imageViewRegistarMain.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }

        //Logout
        /*btLogout.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            FirebaseAuth.getInstance().signOut()
            startActivity(intent)
        }*/
    }
}