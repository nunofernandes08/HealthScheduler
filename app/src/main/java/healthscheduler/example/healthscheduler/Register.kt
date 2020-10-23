package healthscheduler.example.healthscheduler

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class Register : AppCompatActivity() {

    companion object {
        const val TAG = "RegisterActivity"
    }

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = Firebase.auth

        val buttonRegister = findViewById<Button>(R.id.buttonRegister)

        buttonRegister.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {
        val editTextEmail = findViewById<TextView>(R.id.editTextEmailRegister)
        val editTextPassword = findViewById<TextView>(R.id.editTextPasswordRegister)

        val email = editTextEmail.text.toString()
        val password = editTextPassword.text.toString()

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "registerSuccess! Email = + '$editTextEmail'")
                        val user = auth.currentUser
                        val intent = Intent(this, Login::class.java)
                        startActivity(intent)
                    } else {
                        Log.w(TAG, "registerFailed! Info = ", task.exception)
                        Toast.makeText(baseContext, "Falha no registo!.",
                                Toast.LENGTH_SHORT).show()
                    }
                }
    }
}

/* << --------------------------------------- COMENTÁRIOS --------------------------------------- >>

*/