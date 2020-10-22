package healthscheduler.example.healthscheduler

import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.lang.Exception

class Login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private var mGoogleSignInClient : GoogleSignInClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        auth = Firebase.auth
        val currentUser = auth.currentUser

        val loginButton = findViewById<Button>(R.id.buttonLoginLogin)
        val editTextEmail = findViewById<EditText>(R.id.editTextTextEmailLogin)
        val editTextPassword = findViewById<EditText>(R.id.editTextTextPasswordLogin)

        loginButton.setOnClickListener {
            auth.signInWithEmailAndPassword(editTextEmail.text.toString(), editTextPassword.text.toString())
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
        //Esconder a barra de cima e as setas que estao em baixo
        /*window.decorView.apply {
            systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
        }*/
    }
}
