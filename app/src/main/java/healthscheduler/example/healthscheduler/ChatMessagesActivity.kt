package healthscheduler.example.healthscheduler

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.auth.User
import com.squareup.picasso.Picasso
import healthscheduler.example.healthscheduler.databinding.ActivityChatMessagesBinding
import healthscheduler.example.healthscheduler.models.UsersItem

class ChatMessagesActivity : AppCompatActivity() {

    private var toUser : UsersItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityChatMessagesBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        toUser = intent.getParcelableExtra<UsersItem>(ContactsActivity.USER_KEY)
        binding.textViewTitleChatMessagesContactName.text = toUser?.username.toString()
        Picasso.get().load(toUser?.imagePath).into(binding.imageViewChatMessagesContactPhoto)
    }
}