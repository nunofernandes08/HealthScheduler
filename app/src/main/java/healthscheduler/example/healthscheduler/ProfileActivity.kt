package healthscheduler.example.healthscheduler

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.google.gson.TypeAdapterFactory
import com.squareup.picasso.Picasso
import healthscheduler.example.healthscheduler.Login.MainActivity
import healthscheduler.example.healthscheduler.databinding.ActivityHomeBinding
import healthscheduler.example.healthscheduler.databinding.ActivityProfileBinding
import healthscheduler.example.healthscheduler.databinding.ActivityProfileV2Binding
import healthscheduler.example.healthscheduler.models.MessageItem
import healthscheduler.example.healthscheduler.models.UsersItem
import java.util.*

class ProfileActivity : AppCompatActivity() {

    private val db          = FirebaseFirestore.getInstance()
    private val auth        = Firebase.auth
    private val currentUser = auth.currentUser

    private var currentUserName:    String?         = null
    private var currentUserAddress: String?         = null
    private var downUrl:            String?         = null
    private var curFile:            Uri?            = null
    private var user:               UsersItem?      = null
    private var listUser:           UsersItem? = null

    private lateinit var myDialog:  Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityProfileBinding.inflate(layoutInflater)
        //val binding = ActivityProfileV2Binding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        getUser()
        buttonsActions(binding)
        userInformation(binding)
        styleTextView(binding)
        backToHome(binding)
        logout(binding)
    }

    //Funcao para ir buscar a informacao do CURRENTUSER
    private fun getUser() {
        db.collection("users").document(currentUser!!.uid)
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                querySnapshot?.data?.let {
                    user = UsersItem.fromHash(querySnapshot.data as HashMap<String, Any?>)
                    user?.let { user ->
                        currentUserName = user.username.toString()
                        currentUserAddress = user.address.toString()
                    }
                } ?: run {
                    Toast.makeText(
                            this, "Sem utilizador",
                            Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    //Funcao com as acoes dos botoes
    private fun buttonsActions(binding: ActivityProfileBinding){
        binding.buttonEditProfile.setOnClickListener {
            myDialog = Dialog(this)
            myDialog.setContentView(R.layout.popwindow_edit)
            myDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

            val userAddress = myDialog.findViewById<TextView>(R.id.editTextUserAddressEdit)
            userAddress.text = currentUserAddress.toString()


            //Botao para escolher foto
            myDialog.findViewById<ImageView>(R.id.imageViewUserPhotoEdit).setOnClickListener {
                Intent(Intent.ACTION_GET_CONTENT).also {
                    it.type = "image/*"
                    startActivityForResult(it, Home.REQUEST_CODE_IMAGE_PICK)
                }
            }

            //Botao para submeter a atualizacao
            myDialog.findViewById<Button>(R.id.buttonEditarEdit).setOnClickListener {
                uploadImageToFirebaseStorage()
                updateUser()
            }
            myDialog.show()
        }
    }

    //Funcao para fazer upload da imagem para o FireStorage
    private fun uploadImageToFirebaseStorage() {

        val filename    = UUID.randomUUID().toString()
        val ref         = FirebaseStorage.getInstance().getReference("/images/$filename")

        curFile?.let{
            ref.putFile(curFile!!)
                .addOnSuccessListener {
                    ref.downloadUrl.addOnSuccessListener {
                        downUrl = it.toString()
                    }
                }
                .addOnFailureListener {

                }
        }
    }

    //Funcao para fazer update do utilizador
    private fun updateUser(){

        val address = myDialog.findViewById<EditText>(R.id.editTextUserAddressEdit)

        if(address.text.toString() == "") {
            val user = UsersItem(currentUserName, currentUser!!.email, currentUserAddress, downUrl, currentUser.uid)
            db.collection("users").document(currentUser.uid)
                    .set(user.toHashMap())
                    .addOnSuccessListener {
                        Log.d("writeBD", "DocumentSnapshot successfully written!")
                        myDialog.dismiss()
                    }
                    .addOnFailureListener { e ->
                        Log.w("writeBD", "Error writing document", e)
                    }
        } else {
            val user = UsersItem(currentUserName, currentUser!!.email, address.text.toString(), downUrl, currentUser.uid)
            db.collection("users").document(currentUser.uid)
                    .set(user.toHashMap())
                    .addOnSuccessListener {
                        Log.d("writeBD", "DocumentSnapshot successfully written!")
                        myDialog.dismiss()
                    }
                    .addOnFailureListener { e ->
                        Log.w("writeBD", "Error writing document", e)
                    }
        }
    }

    //Funcao que da informacao do CURRENTUSER
    private fun userInformation(binding: ActivityProfileBinding /*binding: ActivityProfileV2Binding*/){
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

    //Funcao com estilo das textViews
    private fun styleTextView (binding: ActivityProfileBinding /*binding: ActivityProfileV2Binding*/){
        binding.textViewInformacaoConta.setTypeface(null, Typeface.BOLD)
        binding.textViewUserNameNomeProfile.setTypeface(null, Typeface.BOLD)
        binding.textViewUserPhoneProfile.setTypeface(null, Typeface.BOLD)
        binding.textViewUserAddressProfile.setTypeface(null, Typeface.BOLD)
        binding.textViewUserBirthdayProfile.setTypeface(null, Typeface.BOLD)
    }

    //Funcao que volta para o home
    private fun backToHome(binding: ActivityProfileBinding /*binding: ActivityProfileV2Binding*/){
        binding.floatingActionButton.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
        }
    }

    //Funcao para dar logout
    private fun logout(binding: ActivityProfileBinding /*binding: ActivityProfileV2Binding*/){
        binding.buttonLogoutProfile.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            FirebaseAuth.getInstance().signOut()
            startActivity(intent)
        }

        binding.imageViewLogoutProfile.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            FirebaseAuth.getInstance().signOut()
            startActivity(intent)
        }
    }

    //Abrir a janela para escolher a foto
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val imageViewUserPhotoEdit = findViewById<ImageView>(R.id.imageViewUserPhotoEdit)

        if (resultCode === Activity.RESULT_OK) {
            if (requestCode == Home.REQUEST_CODE_IMAGE_PICK) {
                data?.data?.let {

                    curFile = it
                    myDialog.findViewById<ImageView>(R.id.imageViewUserPhotoEdit).setImageURI(curFile)
                }
            }
        }
    }
}