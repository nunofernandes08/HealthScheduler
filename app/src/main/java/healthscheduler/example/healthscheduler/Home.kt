package healthscheduler.example.healthscheduler

import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import com.google.firebase.auth.FirebaseAuth

class Home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val buttonLogout = findViewById<Button>(R.id.buttonLogoutHome)
        val buttonSchedule = findViewById<ImageView>(R.id.buttonScheduleHome)


        //Logout
        buttonLogout.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            FirebaseAuth.getInstance().signOut()
            startActivity(intent)
        }

        buttonSchedule.setOnClickListener {
            val intent = Intent(this, Schedule::class.java)
            startActivity(intent)
        }
    }
}