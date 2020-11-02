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
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private var verificationInProgress = false
    private var storedVerificationId: String? = ""


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
                            intent.putExtra("emailOrPhone", emailOrPhone)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        } else {

                        }
                    }
            } else {
                Toast.makeText(
                        this@Register, "Verifique o email ou palavra-passe!",
                        Toast.LENGTH_SHORT
                ).show()
            }
        } else{
            Toast.makeText(
                    this@Register, "Verifique o email ou palavra-passe!",
                    Toast.LENGTH_SHORT
            ).show()
        }/*else if(emailOrPhone.toInt() in 900000001..999999998){
            if(password == passwordConfirm){

                callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                        signInWithPhoneAuthCredential(credential)
                    }

                    override fun onVerificationFailed(e: FirebaseException) {
                        if (e is FirebaseAuthInvalidCredentialsException) {
                            // Invalid request
                            // ...
                        } else if (e is FirebaseTooManyRequestsException) {
                            // The SMS quota for the project has been exceeded
                            // ...
                        }
                    }

                    override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                        val intent = Intent(this@Register, RegisterContinue::class.java)
                        startActivity(intent)

                        storedVerificationId = verificationId
                        resendToken = token
                    }
                }

                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        emailOrPhone,
                        60,
                        TimeUnit.SECONDS,
                        this,
                        callbacks)
            }
        }else {
            Toast.makeText(this, "Verifique o numero de telemovel ou palavra-passe!",
                Toast.LENGTH_SHORT).show()
        }*/
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    val user = task.result?.user
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {

                    }
                }
            }
    }
}
/* << --------------------------------------- COMENTÁRIOS --------------------------------------- >>

*/