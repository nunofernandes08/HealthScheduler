package healthscheduler.example.healthscheduler.Login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import healthscheduler.example.healthscheduler.Home
import healthscheduler.example.healthscheduler.databinding.ActivityRegisterContinueBinding
import healthscheduler.example.healthscheduler.models.UtilizadoresItem

class RegisterContinue : AppCompatActivity() {

    var emailOrPhone : String? = null
    var users : UtilizadoresItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityRegisterContinueBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val currentUser = FirebaseAuth.getInstance().currentUser

        val bundle = intent.extras
        bundle?.let {
            emailOrPhone = it.getString("emailOrPhone")
        }

        binding.buttonContinueRegister.setOnClickListener {
            var moradaUtilizador = binding.editTextMoradaRegister.text.toString()
            var nomeUtilizador = binding.editTextNomeRegister.text.toString()
            if(nomeUtilizador == "" || moradaUtilizador == ""){
                Toast.makeText(
                        this@RegisterContinue, "Verifique o seu Nome ou Morada",
                        Toast.LENGTH_SHORT
                ).show()
            }else {
                val db = FirebaseFirestore.getInstance()
                val user = UtilizadoresItem(nomeUtilizador, emailOrPhone, moradaUtilizador, currentUser!!.uid)
                db.collection("users").document(currentUser!!.uid)
                        .set(user.toHashMap())
                        .addOnSuccessListener {
                            Log.d("writeBD", "DocumentSnapshot successfully written!")
                            val intent = Intent(this@RegisterContinue, Home::class.java)
                            startActivity(intent)
                        }
                        .addOnFailureListener {
                            e -> Log.w("writeBD", "Error writing document", e)
                        }
            }
        }
    }
}