package healthscheduler.example.healthscheduler.Login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import healthscheduler.example.healthscheduler.Home
import healthscheduler.example.healthscheduler.databinding.ActivityLoginBinding

class Login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private var mGoogleSignInClient: GoogleSignInClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth
        val currentUser = auth.currentUser

        binding.buttonLogin.setOnClickListener {
            signInWithEmailAndPassword(binding)
        }
    }

    private fun signInWithEmailAndPassword(binding : ActivityLoginBinding) {
        auth.signInWithEmailAndPassword(binding.editTextEmailLogin.text.toString(), binding.editTextPasswordLogin.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val intent = Intent(this, Home::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@Login, "Falha ao entrar na conta.",
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
