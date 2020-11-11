package healthscheduler.example.healthscheduler

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import healthscheduler.example.healthscheduler.Login.MainActivity
import healthscheduler.example.healthscheduler.databinding.ActivityHomeBinding
import healthscheduler.example.healthscheduler.models.UtilizadoresItem
import java.util.*

class Home : AppCompatActivity() {

    var listUser: UtilizadoresItem? = null

    val db = FirebaseFirestore.getInstance()

    private lateinit var auth: FirebaseAuth

    private lateinit var myDialog : Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Initialize Firebase Auth
        auth = Firebase.auth
        val currentUser = auth.currentUser

        currentUser!!.uid?.let {
            db.collection("users").document(it)
                    .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                        querySnapshot?.data?.let {
                            listUser = UtilizadoresItem.fromHash(querySnapshot.data as HashMap<String, Any?>)
                            listUser?.let { user ->
                                user.userID = querySnapshot.id
                                binding.textViewUserNameHome.setText(user.nomeUtilizador)
                                binding.textViewUserNumberPhoneHome.setText(user.numeroTelemovelOuEmail)
                                binding.textViewUserAddressHome.setText(user.moradaUtilizador)
                            } ?: run {
                                myDialog = Dialog(this)
                                    myDialog.setContentView(R.layout.popwindow_register_continue)
                                    myDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

                                    myDialog.findViewById<Button>(R.id.buttonRegisterContinuePopWindow).setOnClickListener {
                                        var nomeUtilizador = myDialog.findViewById<EditText>(R.id.editTextNomeRegisterContinuePopWindow)
                                        var moradaUtilizador = myDialog.findViewById<EditText>(R.id.editTextMoradaRegisterContinuePopWindow)
                                        if(nomeUtilizador.text.toString() == "" || moradaUtilizador.text.toString() == ""){
                                            Toast.makeText(
                                                    this@Home, "Verifique o seu Nome ou Morada",
                                                    Toast.LENGTH_SHORT
                                            ).show()
                                        }else {
                                            val db = FirebaseFirestore.getInstance()
                                            val user = UtilizadoresItem(nomeUtilizador.text.toString(), currentUser.email, moradaUtilizador.text.toString(), currentUser!!.uid)
                                            db.collection("users").document(currentUser!!.uid)
                                                    .set(user.toHashMap())
                                                    .addOnSuccessListener {
                                                        Log.d("writeBD", "DocumentSnapshot successfully written!")
                                                        myDialog.dismiss()
                                                    }
                                                    .addOnFailureListener {
                                                        e -> Log.w("writeBD", "Error writing document", e)
                                                    }
                                        }
                                    }
                                myDialog.show()
                            }
                        } ?:run {
                            myDialog = Dialog(this)
                            myDialog.setContentView(R.layout.popwindow_register_continue)
                            myDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

                            myDialog.findViewById<Button>(R.id.buttonRegisterContinuePopWindow).setOnClickListener {
                                var nomeUtilizador = myDialog.findViewById<EditText>(R.id.editTextNomeRegisterContinuePopWindow)
                                var moradaUtilizador = myDialog.findViewById<EditText>(R.id.editTextMoradaRegisterContinuePopWindow)
                                if(nomeUtilizador.text.toString() == "" || moradaUtilizador.text.toString() == ""){
                                    Toast.makeText(
                                            this@Home, "Verifique o seu Nome ou Morada",
                                            Toast.LENGTH_SHORT
                                    ).show()
                                }else {
                                    val db = FirebaseFirestore.getInstance()
                                    val user = UtilizadoresItem(nomeUtilizador.text.toString(), currentUser.email, moradaUtilizador.text.toString(), currentUser!!.uid)
                                    db.collection("users").document(currentUser!!.uid)
                                            .set(user.toHashMap())
                                            .addOnSuccessListener {
                                                Log.d("writeBD", "DocumentSnapshot successfully written!")
                                                myDialog.dismiss()
                                            }
                                            .addOnFailureListener {
                                                e -> Log.w("writeBD", "Error writing document", e)
                                            }
                                }
                            }
                            myDialog.show()
                        }
                    }
        }

        binding.buttonLogoutHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            FirebaseAuth.getInstance().signOut()
            startActivity(intent)
        }

        binding.buttonScheduleHome.setOnClickListener {
            val intent = Intent(this, Schedule::class.java)
            startActivity(intent)
        }
    }
}