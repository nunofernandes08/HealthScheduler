package healthscheduler.example.healthscheduler.activity

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import healthscheduler.example.healthscheduler.login.MainActivity
import healthscheduler.example.healthscheduler.R
import healthscheduler.example.healthscheduler.databinding.ActivityProfileBinding
import healthscheduler.example.healthscheduler.models.UsersItem
import java.io.ByteArrayOutputStream
import java.util.*

class ProfileActivity : AppCompatActivity() {

    private val db          = FirebaseFirestore.getInstance()
    private val auth        = Firebase.auth
    private val currentUser = auth.currentUser

    private var currentUserName             : String? = null
    private var currentUserAddress          : String? = null
    private var currentUserPhone            : String? = null
    private var currentUserPhoto            : String? = null
    private var currentUserBirthday         : String? = null
    private var currentUserHealthNumber     : String? = null
    private var currentUserHospitalNumber   : String? = null
    private var downUrl                     : String? = null
    private var curFile                     : Uri? = null
    private var user                        : UsersItem? = null
    private var listUser                    : UsersItem? = null


    private lateinit var myDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityProfileBinding.inflate(layoutInflater)
        //val binding = ActivityProfileV2Binding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        getUser()
        userInformation(binding)
        buttonsActions(binding)
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
                            currentUserPhone = user.phoneNumber.toString()
                            currentUserPhoto = user.imagePath.toString()
                            currentUserBirthday = user.birthday.toString()
                            currentUserHealthNumber = user.healthNumber.toString()
                            currentUserHospitalNumber = user.hospitalNumber.toString()
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
    private fun buttonsActions(binding: ActivityProfileBinding) {
        binding.buttonEditProfile.setOnClickListener {
            myDialog = Dialog(this, R.style.AnimateDialog)
            //myDialog = Dialog(this)
            myDialog.setContentView(R.layout.popwindow_edit)
            myDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

            val userAddress = myDialog.findViewById<TextView>(R.id.editTextUserAddressEdit)
            userAddress.text = currentUserAddress.toString()

            val userPhone = myDialog.findViewById<TextView>(R.id.editTextUserPhoneEdit)
            userPhone.text = currentUserPhone.toString()

            //Botao para escolher foto
            myDialog.findViewById<ImageView>(R.id.imageViewUserPhotoEdit).setOnClickListener {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent, HomeActivity.REQUEST_CODE_IMAGE_PICK)
            }

            //Botao para submeter a atualizacao
            myDialog.findViewById<Button>(R.id.buttonEditarEdit).setOnClickListener {
                uploadImageToFirebaseStorage()
                //updateUser()
            }
            myDialog.show()
        }
    }

    //Funcao para fazer upload da imagem para o FireStorage
    private fun uploadImageToFirebaseStorage() {

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        curFile?.let { uri ->
            val bitmap: Bitmap
            if (Build.VERSION.SDK_INT < 28) {
                bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
            } else {
                bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, uri))
            }

            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos)
            val data = baos.toByteArray()

            ref.putBytes(data).addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    downUrl = it.toString()
                    updateUser()
                }
            }
        }?: run {
            downUrl = currentUserPhoto
            updateUser()
        }
    }

    //Funcao para fazer update do utilizador
    private fun updateUser(){

        val address = myDialog.findViewById<EditText>(R.id.editTextUserAddressEdit)
        val phone = myDialog.findViewById<EditText>(R.id.editTextUserPhoneEdit)

        if (address.text.toString() == ""|| phone.text.toString() == "") {
            val user = UsersItem(currentUserName, currentUser!!.email, currentUserAddress, downUrl, currentUser.uid, currentUserPhone)
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
        else {
            val user = UsersItem(currentUserName, currentUser!!.email, address.text.toString(), downUrl, currentUser.uid, phone.text.toString(), currentUserBirthday, currentUserHealthNumber, currentUserHospitalNumber)
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
                                binding.textViewUserNameProfile.text = user.username
                                binding.textViewUserEmailProfile.text = user.phoneNumberEmail
                                binding.textViewUserName2Profile.text = user.username
                                binding.textViewUserPhone2Profile.text = user.phoneNumber
                                binding.textViewUserAddress2Profile.text = user.address
                                binding.textViewUserBirthday2Profile.text = user.birthday
                                Picasso.get().load(user.imagePath).into(binding.imageViewUserPhotoProfile)
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
            val intent = Intent(this, HomeActivity::class.java)
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

        val imageViewUserPhotoEdit = myDialog.findViewById<ImageView>(R.id.imageViewUserPhotoEdit)

        if (resultCode === Activity.RESULT_OK) {
            if (requestCode == HomeActivity.REQUEST_CODE_IMAGE_PICK) {
                data?.data?.let {

                    curFile = it
                    Picasso.get().load(it).into(imageViewUserPhotoEdit)
                    //myDialog.findViewById<ImageView>(R.id.imageViewUserPhotoEdit).setImageURI(curFile)
                }
            }
        }
    }
}