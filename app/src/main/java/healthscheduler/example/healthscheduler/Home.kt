package healthscheduler.example.healthscheduler

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import healthscheduler.example.healthscheduler.Login.MainActivity
import healthscheduler.example.healthscheduler.databinding.ActivityHomeBinding
import healthscheduler.example.healthscheduler.models.UtilizadoresItem
import java.util.*

class Home : AppCompatActivity() {

    val REQUEST_CODE = 0

    var listUser: UtilizadoresItem? = null

    val db = FirebaseFirestore.getInstance()

    private lateinit var auth: FirebaseAuth

    private lateinit var myDialog : Dialog

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { getPermissionToPhoneCall() }


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
                                            val user = UtilizadoresItem(nomeUtilizador.text.toString(), currentUser.email, moradaUtilizador.text.toString(), "", currentUser!!.uid)
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
                                    val user = UtilizadoresItem(nomeUtilizador.text.toString(), currentUser.email, moradaUtilizador.text.toString(), "", currentUser!!.uid)
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


        binding.floatingActionButton.setOnClickListener {
            val callIntent = Intent(Intent.ACTION_CALL)
            callIntent.data = Uri.parse("tel:914644996")
            startActivity(callIntent)

        }

        binding.buttonScheduleHome.setOnClickListener {
            val intent = Intent(this, Schedule::class.java)
            startActivity(intent)
        }
    }
    private fun getPermissionToPhoneCall() {
        if (ContextCompat.checkSelfPermission(
                        this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(
                        Manifest.permission.CALL_PHONE ), REQUEST_CODE)
            }
        }
    }


     override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
         if (requestCode == REQUEST_CODE) {

             if (grantResults.size == 1
                     && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

             } else {

                 Toast.makeText(this, "You must give permissions to use this app. App is exiting.", Toast.LENGTH_SHORT).show()
                 finishAffinity()
             }
         }
     }
}