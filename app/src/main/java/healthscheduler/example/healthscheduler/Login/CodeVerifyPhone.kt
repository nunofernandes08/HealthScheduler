package healthscheduler.example.healthscheduler.Login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import healthscheduler.example.healthscheduler.databinding.ActivityCodeVerifyPhoneBinding

class CodeVerifyPhone : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    var emailOrPhone : String? = null
    var codeVerify : Int? = null
    var token : PhoneAuthProvider.ForceResendingToken? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityCodeVerifyPhoneBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth

        val bundle = intent.extras
        bundle?.let {
            emailOrPhone = it.getString("emailOrPhone")
            codeVerify = it.getInt("codigoVerificacao")
        }

        binding.buttonVerifyCode.setOnClickListener {
            if(codeVerify == binding.editTextPhoneCode.text.toString().toInt()){
                val intent = Intent(this@CodeVerifyPhone, RegisterContinue::class.java)
                intent.putExtra("emailOrPhone", emailOrPhone)
                startActivity(intent)
            }
        }
    }
}