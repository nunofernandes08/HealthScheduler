package healthscheduler.example.healthscheduler

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import healthscheduler.example.healthscheduler.databinding.ActivityLatestMessagesBinding

class LatestMessagesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityLatestMessagesBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


    }
}