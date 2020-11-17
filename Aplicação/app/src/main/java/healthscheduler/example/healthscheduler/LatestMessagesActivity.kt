package healthscheduler.example.healthscheduler

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import healthscheduler.example.healthscheduler.databinding.ActivityLatestMessagesBinding

class LatestMessagesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityLatestMessagesBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.floatingActionButtonSendNewMessage.setOnClickListener {

            val intent = Intent(this, ContactsActivity::class.java)
            startActivity(intent)
        }
    }
}