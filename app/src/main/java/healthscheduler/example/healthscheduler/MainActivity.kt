package healthscheduler.example.healthscheduler

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btLogout = findViewById<Button>(R.id.btlogout)

        btLogout.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            FirebaseAuth.getInstance().signOut()
            startActivity(intent)
        }
    }
}