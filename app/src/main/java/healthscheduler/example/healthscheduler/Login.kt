package healthscheduler.example.healthscheduler

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class Login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private var mGoogleSignInClient: GoogleSignInClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth
        val currentUser = auth.currentUser

        val loginButton = findViewById<Button>(R.id.buttonLogin)

        loginButton.setOnClickListener {
            signInWithEmailAndPassword()
        }
    }

    private fun signInWithEmailAndPassword() {
        val editTextEmail = findViewById<EditText>(R.id.editTextEmailLogin)
        val editTextPassword = findViewById<EditText>(R.id.editTextPasswordLogin)

        val emailLogin = editTextEmail.text.toString()
        val passwordLogin = editTextPassword.text.toString()

        auth.signInWithEmailAndPassword(emailLogin, passwordLogin)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d("", "loginSuccess!")
                        val intent = Intent(this, Home::class.java)
                        startActivity(intent)
                    } else {
                        Log.w("", "loginFailed! Info = ", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                    }
                }
    }
}

/* << --------------------------------------- COMENTÁRIOS --------------------------------------- >>

--> Esconder a barra de cima e as setas que estão em baixo

        window.decorView.apply {
            systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
        }

*/
