package healthscheduler.example.healthscheduler.login

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import healthscheduler.example.healthscheduler.activity.HomeActivity
import healthscheduler.example.healthscheduler.R
import healthscheduler.example.healthscheduler.databinding.ActivityLoginBinding
import healthscheduler.example.healthscheduler.models.UsersItem
import kotlinx.android.synthetic.main.activity_login.*


class Login : AppCompatActivity() {

    private val db          = FirebaseFirestore.getInstance()
    private val auth        = Firebase.auth

    private var referenceUsers     = db.collection("users")
    private var user               : UsersItem? = null
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private lateinit var myDialog : Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        buttonActions(binding)
        textViewActions(binding)
    }

    private fun verifyEmail() {

        val currentUser = auth.currentUser

        currentUser?.let {
            if (it.isEmailVerified) {
                val intent = Intent(this, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            else {
                Toast.makeText(
                        this@Login, "Valide o seu e-mail!",
                        Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    //Funcao com as acoes dos botoes
    private fun buttonActions(binding: ActivityLoginBinding){

        binding.buttonLogin.setOnClickListener {
            verifyUser(binding)
        }

        binding.buttonInfoLogin.setOnClickListener {
            myDialog = Dialog(this, R.style.AnimateDialog)
            myDialog.setContentView(R.layout.popwindow_info)
            myDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
            myDialog.show()
        }
    }

    //Funcao com as acoes das textViews
    private fun textViewActions(binding: ActivityLoginBinding) {

        var count = 0

        binding.textViewRecoveryPasswordLogin.setOnClickListener {

            myDialog = Dialog(this, R.style.AnimateDialog)
            myDialog.setContentView(R.layout.popwindow_recoverypassword)
            myDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

            myDialog.findViewById<Button>(R.id.buttonEnviarPop).setOnClickListener {

                val emailAddress = myDialog.findViewById<TextView>(R.id.editTextRecoveryEmailPop).text.toString()

                if (emailAddress != "") {
                    Firebase.auth.sendPasswordResetEmail(emailAddress)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this@Login, "Email enviado com sucesso",
                                        Toast.LENGTH_SHORT).show()
                                myDialog.dismiss()
                            }
                            else {
                                Toast.makeText(this@Login, "Falha ao enviar o email",
                                        Toast.LENGTH_SHORT).show()
                            }
                        }
                }
                else {
                    Toast.makeText(this@Login, "Inserir um e-mail valido",
                            Toast.LENGTH_SHORT).show()
                }
            }
            myDialog.show()
        }

        binding.imageViewHidePasswordLogin.setOnClickListener {

            if (count === 1 ) {
                editTextPasswordLogin.transformationMethod = PasswordTransformationMethod()
            }
            if (count === 0) {
                editTextPasswordLogin.transformationMethod = null
                count = 1
            }
            else if (count === 1) {
                editTextPasswordLogin.transformationMethod = PasswordTransformationMethod()
                count = 0
            }
        }
    }

    //verifica se email introduzido é um utente e esta no firestore
    private fun verifyUser(binding : ActivityLoginBinding) {
        referenceUsers.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    if (document.data.getValue("phoneNumberEmail") == binding.editTextEmailLogin.text.toString()) {
                        signInWithEmailAndPassword(binding)
                    }
                }
            }
    }

    private fun signInWithEmailAndPassword(binding : ActivityLoginBinding) {

        val userEmail = binding.editTextEmailLogin.text.toString()
        val userPassword = binding.editTextPasswordLogin.text.toString()
        var email : String? = null

        if (userEmail == "" || userPassword == "") {
            Toast.makeText(
                    this@Login, "Verifique o seu Email ou Palavra-passe",
                    Toast.LENGTH_SHORT
            ).show()
        }
        else {
            auth.signInWithEmailAndPassword(userEmail, userPassword)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            verifyEmail()
                        }
                        else {
                            Toast.makeText(this@Login, "Falha ao entrar na conta.",
                                    Toast.LENGTH_SHORT).show()
                        }
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
