package healthscheduler.example.healthscheduler

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import healthscheduler.example.healthscheduler.databinding.ActivityChatMessagesBinding
import healthscheduler.example.healthscheduler.models.UtilizadoresItem

class ChatMessagesActivity : AppCompatActivity() {

    private var toUserId : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityChatMessagesBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val bundle = intent.extras
        bundle?.let {

            toUserId = it.getString(ContactsActivity.USER_KEY)
        }
    }
}