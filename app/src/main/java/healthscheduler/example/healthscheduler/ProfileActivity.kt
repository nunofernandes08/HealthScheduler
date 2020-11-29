package healthscheduler.example.healthscheduler

import android.app.Dialog
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.gson.TypeAdapterFactory
import com.squareup.picasso.Picasso
import healthscheduler.example.healthscheduler.databinding.ActivityHomeBinding
import healthscheduler.example.healthscheduler.databinding.ActivityProfileBinding
import healthscheduler.example.healthscheduler.models.MessageItem
import healthscheduler.example.healthscheduler.models.UsersItem
import java.util.HashMap

class ProfileActivity : AppCompatActivity() {

    val db =            FirebaseFirestore.getInstance()

    var listUser:           UsersItem? = null

    private val auth = Firebase.auth
    private val currentUser = auth.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        userInfomation(binding)
        styleTextView(binding)
    }

    private fun userInfomation(binding: ActivityProfileBinding){
        currentUser?.uid.let {
            if (it != null) {
                db.collection("users").document(it)
                    .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                        querySnapshot?.data?.let {
                            listUser = UsersItem.fromHash(querySnapshot.data as HashMap<String, Any?>)
                            listUser?.let { user ->
                                if (user.imagePath != "") {
                                    binding.textViewUserNameProfile.text = user.username
                                    binding.textViewUserEmailProfile.text = user.phoneNumberEmail
                                    binding.textViewUserName2Profile.text = user.username
                                    //binding.textViewUserPhone2Profile.text = user.phoneNumberEmail
                                    binding.textViewUserAddress2Profile.text = user.address
                                    //binding.textViewUserBirthday2Profile.text = user.
                                    Picasso.get().load(user.imagePath).into(binding.imageViewUserPhotoProfile)
                                }
                            }
                        }
                    }
            }
        }
    }

    private fun styleTextView (binding: ActivityProfileBinding){
        binding.textViewInformacaoConta.setTypeface(null, Typeface.BOLD)
        binding.textViewUserNameNomeProfile.setTypeface(null, Typeface.BOLD)
        binding.textViewUserPhoneProfile.setTypeface(null, Typeface.BOLD)
        binding.textViewUserAddressProfile.setTypeface(null, Typeface.BOLD)
        binding.textViewUserBirthdayProfile.setTypeface(null, Typeface.BOLD)
    }
}