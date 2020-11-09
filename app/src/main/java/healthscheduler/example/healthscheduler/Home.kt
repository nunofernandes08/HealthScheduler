package healthscheduler.example.healthscheduler

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import healthscheduler.example.healthscheduler.Login.MainActivity
import healthscheduler.example.healthscheduler.databinding.ActivityHomeBinding
import healthscheduler.example.healthscheduler.models.ScheduleItem
import healthscheduler.example.healthscheduler.models.UtilizadoresItem
import java.io.ByteArrayInputStream
import java.util.*

class Home : AppCompatActivity() {

    var listUser: UtilizadoresItem? = null

    val db = FirebaseFirestore.getInstance()

    private lateinit var auth: FirebaseAuth

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
                            querySnapshot?.let {
                                listUser = UtilizadoresItem.fromHash(querySnapshot.data as HashMap<String, Any?>)
                                listUser?.let { user ->
                                    user.userID = querySnapshot.id

                                    binding.textViewUserNameHome.setText(user.nomeUtilizador)
                                    binding.textViewUserNumberPhoneHome.setText(user.numeroTelemovelOuEmail)
                                    binding.textViewUserAddressHome.setText(user.moradaUtilizador)
                                }?: run {
                                    binding.textViewUserNameHome.text = "User name"
                                    binding.textViewUserNumberPhoneHome.text = "User email or phone number"
                                    binding.textViewUserAddressHome.text = "User address"
                                }
                            }?: run{
                                binding.textViewUserNameHome.text = "User name"
                                binding.textViewUserNumberPhoneHome.text = "User email or phone number"
                                binding.textViewUserAddressHome.text = "User address"
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