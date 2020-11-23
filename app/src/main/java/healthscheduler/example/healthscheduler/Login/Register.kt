package healthscheduler.example.healthscheduler.Login

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import healthscheduler.example.healthscheduler.Home
import healthscheduler.example.healthscheduler.PasswordStrength
import healthscheduler.example.healthscheduler.databinding.ActivityRegisterBinding
import kotlinx.android.synthetic.main.activity_register.*
import java.util.concurrent.TimeUnit
import androidx.lifecycle.Observer


class Register : AppCompatActivity() {

    companion object {
        const val TAG = "RegisterActivity"
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private var verificationInProgress = false
    private var storedVerificationId: String? = ""

    var color: Int = Color.RED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth

        binding.buttonContinueRegister.setOnClickListener {
            registerUser(binding)
        }

        val passwordStrength = PasswordStrength()
        editTextPasswordRegister.addTextChangedListener(passwordStrength)

        passwordStrength.strengthLevel.observe(this, Observer{
            strengthLevel -> displayStrengthLevel(strengthLevel)
        })

        passwordStrength.strengthColor.observe(this, Observer {
            strengthColor -> color = strengthColor
        })
    }

    private fun displayStrengthLevel(strengthLevel: String) {
        textViewPasswordCalculator.text = strengthLevel
        textViewPasswordCalculator.setTextColor(ContextCompat.getColor(this, color))
    }

    private fun registerUser(binding: ActivityRegisterBinding) {

        val emailOrPhone = binding.editTextEmailOrPhoneRegister.text.toString()
        val password = binding.editTextPasswordRegister.text.toString()
        val passwordConfirm = binding.editTextConfirmPasswordRegister.text.toString()
        if(emailOrPhone == "" || password == "" || passwordConfirm == "") {
            Toast.makeText(
                    this@Register, "Verifique o seu Email ou Palavra-passe",
                    Toast.LENGTH_SHORT
            ).show()
        } else {
            if (emailOrPhone.contains("@") && emailOrPhone.contains(".") && (emailOrPhone.contains("com") || emailOrPhone.contains("pt"))) {
                if (password == passwordConfirm) {
                    auth.createUserWithEmailAndPassword(emailOrPhone, password)
                            .addOnCompleteListener(this) { task ->
                                if (task.isSuccessful) {
                                    val user = auth.currentUser
                                    val intent = Intent(this, Home::class.java)
                                    intent.putExtra("emailOrPhone", emailOrPhone)
                                    intent.flags =
                                            Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
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
            } else if (emailOrPhone.toInt() in 900000001..999999998) {
                if (password == passwordConfirm) {
                    val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                            signInWithPhoneAuthCredential(credential)
                            /*binding.buttonContinueRegisterContinue.setOnClickListener {
                                val intent = Intent(this@Register, Home::class.java)
                                intent.putExtra("codigoVerificacao", storedVerificationId)
                                intent.putExtra("emailOrPhone", emailOrPhone)
                                startActivity(intent)
                            }*/
                        }

                        override fun onVerificationFailed(e: FirebaseException) {
                            print(e.localizedMessage)
                        }

                        override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                            storedVerificationId = verificationId
                            resendToken = token

                            binding.textViewEmailRegister.visibility = View.GONE
                            binding.textViewPasswordRegister.visibility = View.GONE
                            binding.textViewConfirmPasswordRegister.visibility = View.GONE

                            binding.editTextEmailOrPhoneRegister.visibility = View.GONE
                            binding.editTextPasswordRegister.visibility = View.GONE
                            binding.editTextConfirmPasswordRegister.visibility = View.GONE

                            binding.buttonContinueRegister.visibility = View.GONE

                            /*binding.textViewCodeSent.visibility = View.VISIBLE
                            binding.editTextCodeSent.visibility = View.VISIBLE
                            binding.buttonContinueRegisterContinue.visibility = View.VISIBLE*/
                        }
                    }

                    val options = PhoneAuthOptions.newBuilder(auth)
                            .setPhoneNumber("+351$emailOrPhone")
                            .setTimeout(60L, TimeUnit.SECONDS)
                            .setActivity(this)
                            .setCallbacks(callbacks)
                            .build()
                    PhoneAuthProvider.verifyPhoneNumber(options)
                } else {
                    Toast.makeText(
                            this, "Verifique o numero de telemovel ou palavra-passe!",
                            Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                        this, "Verifique o numero de telemovel ou palavra-passe!",
                        Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    val user = task.result?.user
                    val intent = Intent(this@Register, RegisterContinue::class.java)
                    startActivity(intent)
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