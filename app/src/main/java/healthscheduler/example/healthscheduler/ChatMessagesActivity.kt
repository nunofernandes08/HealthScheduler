package healthscheduler.example.healthscheduler

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import healthscheduler.example.healthscheduler.databinding.ActivityChatMessagesBinding

class ChatMessagesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityChatMessagesBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


    }
}