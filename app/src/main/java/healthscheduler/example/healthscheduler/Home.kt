package healthscheduler.example.healthscheduler

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import healthscheduler.example.healthscheduler.Login.MainActivity
import healthscheduler.example.healthscheduler.databinding.ActivityHomeBinding
import healthscheduler.example.healthscheduler.models.MessageItem
import healthscheduler.example.healthscheduler.models.UsersItem
import java.util.*

class Home : AppCompatActivity() {

    val REQUEST_CODE =  0
    val db =            FirebaseFirestore.getInstance()
    val storageRef =    Firebase.storage.reference
    val imagesRef =     storageRef.child("images/${UUID.randomUUID()}.jpg")

    var currentUserName:    String? = null
    var currentUserAddress: String? = null
    var downUrl:            String? = null
    var user:           UsersItem? = null
    var bitmap:             Bitmap? = null
    var curFile:            Uri? = null

    private var refLatestMessages = db.collection("latest_messages")
    private var referenceUsers = db.collection("users")
    private var referenceSchedule = db.collection("consultas")
    private var users : MutableList<UsersItem> = arrayListOf()
    private var latestMessages : MutableList<MessageItem> = arrayListOf()
    private var message : MessageItem? = null
    private var user1 : UsersItem? = null
    private var user2 : UsersItem? = null
    private var user3 : UsersItem? = null

    private val auth = Firebase.auth
    private val currentUser = auth.currentUser
    private lateinit var myDialog:  Dialog

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getPermissionToPhoneCall()
        }

        binding.textViewUserNameHome.text = ""
        binding.textViewUserNumberPhoneHome.text = ""
        binding.textViewUserAddressHome.text = ""

        //Ve se o user tem dados, se nao tiver faz com que insira
        currentUser?.uid.let {
            if (it != null) {
                db.collection("users")
                    .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                        if (querySnapshot != null) {
                            users.clear()
                            for (doc in querySnapshot) {
                                if (doc.id == it) {
                                    user = UsersItem.fromHash(doc.data as HashMap<String, Any?>)
                                }
                                else {
                                    val listUser = UsersItem.fromHash(doc.data as HashMap<String, Any?>)
                                    users.add(listUser)
                                }
                            }
                            user?.let { item ->
                                if (item.imagePath == "") {
                                    item.userID = it
                                    binding.textViewUserNameHome.text = item.username
                                    binding.textViewUserNumberPhoneHome.text = item.phoneNumberEmail
                                    binding.textViewUserAddressHome.text = item.address
                                }
                                else {
                                    item.userID = it
                                    binding.textViewUserNameHome.text = item.username
                                    binding.textViewUserNumberPhoneHome.text = item.phoneNumberEmail
                                    binding.textViewUserAddressHome.text = item.address
                                    Picasso.get().load(item.imagePath).into(binding.imageViewUserPhotoHome)
                                }
                                refLatestMessages
                                    .document(it)
                                    .collection("latest_message")
                                    .orderBy("timeStamp", Query.Direction.DESCENDING)
                                    .addSnapshotListener { snapshot, error ->
                                        latestMessages.clear()
                                        if (snapshot != null) {
                                            for (doc in snapshot) {
                                                message = MessageItem.fromHash(doc.data as HashMap<String, Any?>)
                                                latestMessages.add(message!!)
                                            }
                                            latestMessages.let {
                                                for ((i, message) in latestMessages.withIndex()) {
                                                    for (item1 in users) {
                                                        if (message.fromId == item1.userID || message.toId == item1.userID) {
                                                            when (i) {
                                                                0 -> {
                                                                    user1 = item1
                                                                    binding.textViewFavoriteName1Home.text = item1.username
                                                                    if (item1.imagePath != "") {
                                                                        Picasso.get().load(item1.imagePath).into(binding.imageViewFavoriteUserPhoto1Home)
                                                                    }
                                                                    else {
                                                                        binding.imageViewFavoriteUserPhoto1Home.setBackgroundResource(R.drawable.imageviewfotofavorito1)
                                                                    }
                                                                }
                                                                1 -> {
                                                                    user2 = item1
                                                                    binding.textViewFavoriteName2Home.text = item1.username
                                                                    if (item1.imagePath != "") {
                                                                        Picasso.get().load(item1.imagePath).into(binding.imageViewFavoriteUserPhoto2Home)
                                                                    }
                                                                    else {
                                                                        binding.imageViewFavoriteUserPhoto1Home.setBackgroundResource(R.drawable.imageviewfotofavorito1)
                                                                    }
                                                                }
                                                                2 -> {
                                                                    user3 = item1
                                                                    binding.textViewFavoriteName3Home.text = item1.username
                                                                    if (item1.imagePath != "") {
                                                                        Picasso.get().load(item1.imagePath).into(binding.imageViewFavoriteUserPhoto3Home)
                                                                    }
                                                                    else {
                                                                        binding.imageViewFavoriteUserPhoto1Home.setBackgroundResource(R.drawable.imageviewfotofavorito1)
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                            } ?: run {
                                myDialog = Dialog(this)
                                myDialog.setContentView(R.layout.popwindow_register_continue)
                                myDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                                myDialog.findViewById<Button>(R.id.buttonRegisterContinuePopWindow).setOnClickListener {
                                    val username = myDialog.findViewById<EditText>(R.id.editTextNomeRegisterContinuePopWindow)
                                    val address = myDialog.findViewById<EditText>(R.id.editTextMoradaRegisterContinuePopWindow)
                                    if (username.text.toString() == "" || address.text.toString() == "") {
                                        Toast.makeText(
                                                this@Home, "Verifique o seu Nome ou Morada",
                                                Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    else {
                                        val db = FirebaseFirestore.getInstance()
                                        //Colocar "imageRef.name" no imagemPath me baixo
                                        val user = UsersItem(username.text.toString(), currentUser?.email, address.text.toString(), "", currentUser?.uid)
                                        db.collection("users").document(currentUser!!.uid)
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
                                myDialog.show()
                            }
                        }
                    }
            }
        }

        //Inicializações das funções
        getUser()
        getCountNotification()

        //Botao LOGOUT
        binding.buttonLogoutHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            FirebaseAuth.getInstance().signOut()
            startActivity(intent)
        }

        //Botao SOS
        binding.floatingActionButton.setOnClickListener {
            val callIntent = Intent(Intent.ACTION_CALL)
            callIntent.data = Uri.parse("tel:914644996")
            startActivity(callIntent)
        }

        //Botao para ir as consultas
        binding.buttonScheduleHome.setOnClickListener {
            val intent = Intent(this, Schedule::class.java)
            startActivity(intent)
        }

        //Botao para ir ao Chat
        binding.buttonChatHome.setOnClickListener {
            val intent = Intent(this, LatestMessagesActivity::class.java)
            intent.putExtra(ContactsActivity.USER_KEY, user)
            startActivity(intent)
        }

        //Botao para ultimas mensagens User1
        binding.imageViewFavoriteUserPhoto1Home.setOnClickListener {

            if (user1 != null) {

                val intent = Intent(this, ChatMessagesActivity::class.java)
                intent.putExtra(ContactsActivity.USER_KEY, user1)
                startActivity(intent)
            }
        }

        //Botao para ultimas mensagens User2
        binding.imageViewFavoriteUserPhoto2Home.setOnClickListener {

            if (user2 != null) {

                val intent = Intent(this, ChatMessagesActivity::class.java)
                intent.putExtra(ContactsActivity.USER_KEY, user2)
                startActivity(intent)
            }
        }

        //Botao para ultimas mensagens User3
        binding.imageViewFavoriteUserPhoto3Home.setOnClickListener {

            if (user3 != null) {

                val intent = Intent(this, ChatMessagesActivity::class.java)
                intent.putExtra(ContactsActivity.USER_KEY, user3)
                startActivity(intent)
            }
        }

        //Botao para editar
        binding.buttonEditHome.setOnClickListener {
            myDialog = Dialog(this)
            myDialog.setContentView(R.layout.popwindow_edit)
                myDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

                //Botao para escolher foto
                myDialog.findViewById<ImageView>(R.id.imageViewUserPhotoEdit).setOnClickListener {
                    Intent(Intent.ACTION_GET_CONTENT).also {
                        it.type = "image/*"
                        startActivityForResult(it, REQUEST_CODE_IMAGE_PICK)
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

    //Funcao para buscar quantas consultas tem o CURRENTUSER
    private fun getCountNotification(){
        val imageViewScheduleNotification = findViewById<ImageView>(R.id.imageViewScheduleNotification)
        val textViewScheduleNotification = findViewById<TextView>(R.id.textViewScheduleNotification)
        currentUser?.let{
            db.collection("consultas")
            .whereEqualTo("userID", currentUser!!.uid)
            .addSnapshotListener { snapshot, error ->
                snapshot?.let {
                    var count = snapshot?.count()
                    if(count > 0){
                        imageViewScheduleNotification.visibility = View.VISIBLE
                        textViewScheduleNotification.visibility = View.VISIBLE
                        textViewScheduleNotification.text = count.toString()
                    }else{
                        imageViewScheduleNotification.visibility = View.INVISIBLE
                        textViewScheduleNotification.visibility = View.INVISIBLE
                    }
                }
            }
        }

    }

    private fun getAllUsers() {

        currentUser.let {

            referenceUsers.addSnapshotListener { snapshot, error ->

                users.clear()
                if (snapshot != null) {

                    for (doc in snapshot) {

                        val user = UsersItem.fromHash(doc.data as HashMap<String, Any?>)
                        if (user.userID != currentUser?.uid) {

                            users.add(user)
                        }
                    }
                }
            }
        }
    }

    private fun getLatestMessages() {

        refLatestMessages
                .document(currentUser?.uid.toString())
                .collection("latest_message")
                .orderBy("timeStamp", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->

                    latestMessages.clear()
                    if (snapshot != null) {

                        for (doc in snapshot) {

                            message = MessageItem.fromHash(doc.data as HashMap<String, Any?>)
                            latestMessages.add(message!!)
                        }
                    }
                }
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
                            this@Home, "Sem utilizador",
                            Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    //Funcao para fazer upload da imagem para o FireStorage
    private fun uploadImageToFirebaseStorage() {

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

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

    //Funcao para buscar permissao para fazer chamada
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

    //Abrir a janela para escolher a foto
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val imageViewUserPhotoEdit = findViewById<ImageView>(R.id.imageViewUserPhotoEdit)

        if (resultCode === Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_IMAGE_PICK) {
                data?.data?.let {

                    curFile = it
                    myDialog.findViewById<ImageView>(R.id.imageViewUserPhotoEdit).setImageURI(curFile)
                }
            }
        }
    }

    companion object {

        const val REQUEST_CODE_PHOTO = 23524
        const val REQUEST_CODE_IMAGE_PICK = 0
        const val ONE_MEGABYTE : Long = 1024 * 1024 * 5
    }
}