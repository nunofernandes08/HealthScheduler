package healthscheduler.example.healthscheduler.Login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.common.base.Verify.verify
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import healthscheduler.example.healthscheduler.databinding.ActivityRegisterBinding
import java.util.concurrent.TimeUnit

class Register : AppCompatActivity() {

    companion object {
        const val TAG = "RegisterActivity"
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth

        binding.buttonContinueRegister.setOnClickListener {
            registerUser(binding)
        }
    }

    private fun registerUser(binding : ActivityRegisterBinding) {

        val emailOrPhone = binding.editTextEmailOrPhoneRegister.text.toString()
        val password = binding.editTextPasswordRegister.text.toString()
        val passwordConfirm = binding.editTextConfirmPasswordRegister.text.toString()
        if(emailOrPhone.contains("@") && emailOrPhone.contains(".") && (emailOrPhone.contains("com") || emailOrPhone.contains("pt"))) {
            if (password == passwordConfirm) {
                auth.createUserWithEmailAndPassword(emailOrPhone, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            val intent = Intent(this, RegisterContinue::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, "Falha no registo!.",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(
                    this, "Verifique o email ou palavra-passe!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }else if(emailOrPhone.toInt() in 900000001..999999998){
            Log.d("1", "Numero telemovel: $emailOrPhone")
            if(password == passwordConfirm){
                Log.d("2", "Numero telemovel: $emailOrPhone")
                Log.d("2", "Password telemovel: $password")
                verificationCallbacks()
            }else {
                Log.d("3", "Numero telemovel: $emailOrPhone")
                Log.d("3", "Password telemovel: $password")
                Toast.makeText(this, "Verifique o numero de telemovel ou palavra-passe!",
                    Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(this, "Verifique o numero de telemovel ou palavra-passe!",
                Toast.LENGTH_SHORT).show()
        }
    }

    private fun verificationCallbacks(){
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                signInPhone(credential)
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                TODO("Not yet implemented")
            }

            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(p0, p1)
            }

        }
    }

    private fun signInPhone(credential: PhoneAuthCredential){
        auth.signInWithCredential(credential)
            .addOnCompleteListener {
                    task: Task<AuthResult> ->
                if(task.isSuccessful){
                    val intent = Intent(this, RegisterContinue::class.java)
                    startActivity(intent)
                }
            }
    }
}

/* << --------------------------------------- COMENTÁRIOS --------------------------------------- >>

*/