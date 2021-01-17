package healthscheduler.example.healthscheduler.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import healthscheduler.example.healthscheduler.databinding.ActivityCodeVerifyPhoneBinding


//nao esta a ser usado
class CodeVerifyPhone : AppCompatActivity() {

    private lateinit var auth   : FirebaseAuth
    
    var emailOrPhone            : String? = null
    var codeVerify              : Int? = null

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
            val credential = PhoneAuthProvider.getCredential(codeVerify.toString(), binding.editTextPhoneCode.text.toString())
            signInWithPhoneAuthCredential(credential)
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("", "signInWithCredential:success")
                    val user = task.result?.user
                    val intent = Intent(this@CodeVerifyPhone, RegisterContinue::class.java)
                    intent.putExtra("emailOrPhone", emailOrPhone)
                    startActivity(intent)
                } else {
                    Log.w("", "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {

                    }
                }
            }
    }
}