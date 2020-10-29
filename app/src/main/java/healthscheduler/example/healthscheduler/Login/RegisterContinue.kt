package healthscheduler.example.healthscheduler.Login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import healthscheduler.example.healthscheduler.Home
import healthscheduler.example.healthscheduler.databinding.ActivityRegisterContinueBinding

class RegisterContinue : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityRegisterContinueBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.buttonContinueRegister.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
        }
    }
}