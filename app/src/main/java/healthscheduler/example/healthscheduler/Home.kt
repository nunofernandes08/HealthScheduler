package healthscheduler.example.healthscheduler

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth

class Home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val buttonLogout = findViewById<Button>(R.id.buttonLogout)


        //Logout
        buttonLogout.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            FirebaseAuth.getInstance().signOut()
            startActivity(intent)
        }
    }
}