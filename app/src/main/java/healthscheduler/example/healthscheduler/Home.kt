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
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import healthscheduler.example.healthscheduler.Login.MainActivity
import healthscheduler.example.healthscheduler.databinding.ActivityHomeBinding
import healthscheduler.example.healthscheduler.models.UtilizadoresItem
import java.util.*

class Home : AppCompatActivity() {

    val REQUEST_CODE =  0
    val db =            FirebaseFirestore.getInstance()
    val storageRef =    Firebase.storage.reference
    val imagesRef =     storageRef.child("images/${UUID.randomUUID()}.jpg")

    var currentUserName:    String? = null
    var currentUserAddress: String? = null
    var downUrl:            String? = null
    var listUser:           UtilizadoresItem? = null
    var bitmap:             Bitmap? = null
    var curFile:            Uri? = null

    private lateinit var auth:      FirebaseAuth
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

        // Initialize Firebase Auth
        auth = Firebase.auth
        val currentUser = auth.currentUser

        //Ve se o user tem dados, se nao tiver faz com que insira
        currentUser!!.uid?.let {
            db.collection("users").document(it)
                    .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                        querySnapshot?.data?.let {
                            listUser = UtilizadoresItem.fromHash(querySnapshot.data as HashMap<String, Any?>)
                            listUser?.let { user ->
                                if (user.imagemPath == "null"){
                                    user.userID = querySnapshot.id
                                    binding.textViewUserNameHome.setText(user.nomeUtilizador)
                                    binding.textViewUserNumberPhoneHome.setText(user.numeroTelemovelOuEmail)
                                    binding.textViewUserAddressHome.setText(user.moradaUtilizador)
                                }else{
                                    user.userID = querySnapshot.id
                                    binding.textViewUserNameHome.setText(user.nomeUtilizador)
                                    binding.textViewUserNumberPhoneHome.setText(user.numeroTelemovelOuEmail)
                                    binding.textViewUserAddressHome.setText(user.moradaUtilizador)
                                    Picasso.get().load(user.imagemPath).into(binding.imageViewUserPhotoHome)
                                }
                            } ?: run {
                                myDialog = Dialog(this)
                                myDialog.setContentView(R.layout.popwindow_register_continue)
                                myDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

                                myDialog.findViewById<Button>(R.id.buttonRegisterContinuePopWindow).setOnClickListener {
                                    var nomeUtilizador = myDialog.findViewById<EditText>(R.id.editTextNomeRegisterContinuePopWindow)
                                    var moradaUtilizador = myDialog.findViewById<EditText>(R.id.editTextMoradaRegisterContinuePopWindow)
                                    if (nomeUtilizador.text.toString() == "" || moradaUtilizador.text.toString() == "") {
                                        Toast.makeText(
                                                this@Home, "Verifique o seu Nome ou Morada",
                                                Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        val db = FirebaseFirestore.getInstance()
                                        //Colocar "imageRef.name" no imagemPath me baixo
                                        val user = UtilizadoresItem(nomeUtilizador.text.toString(), currentUser.email, moradaUtilizador.text.toString(), null, currentUser!!.uid)
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
                        } ?: run {
                            myDialog = Dialog(this)
                            myDialog.setContentView(R.layout.popwindow_register_continue)
                            myDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

                            myDialog.findViewById<Button>(R.id.buttonRegisterContinuePopWindow).setOnClickListener {
                                var nomeUtilizador = myDialog.findViewById<EditText>(R.id.editTextNomeRegisterContinuePopWindow)
                                var moradaUtilizador = myDialog.findViewById<EditText>(R.id.editTextMoradaRegisterContinuePopWindow)
                                if (nomeUtilizador.text.toString() == "" || moradaUtilizador.text.toString() == "") {
                                    Toast.makeText(
                                            this@Home, "Verifique o seu Nome ou Morada",
                                            Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    val db = FirebaseFirestore.getInstance()
                                    val user = UtilizadoresItem(nomeUtilizador.text.toString(), currentUser.email, moradaUtilizador.text.toString(), null, currentUser!!.uid)
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

        //Inicializacao da funcao para ir buscar a informacao do CURRENT USER
        getUser()

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
            startActivity(intent)
        }

        //Botap para editar
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

    //Funcao para ir buscar a informacao do CURRENT USER
    private fun getUser() {

        auth = Firebase.auth
        val currentUser = auth.currentUser

        db.collection("users").document(currentUser!!.uid)
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    querySnapshot?.data?.let {
                        listUser = UtilizadoresItem.fromHash(querySnapshot.data as HashMap<String, Any?>)
                        listUser?.let { user ->
                            var currentUserNamee = user.nomeUtilizador.toString()
                            currentUserName = currentUserNamee
                            var currentUserAddress2 = user.moradaUtilizador.toString()
                            currentUserAddress = currentUserAddress2
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

        auth = Firebase.auth
        val currentUser = auth.currentUser

        var moradaUtilizador = myDialog.findViewById<EditText>(R.id.editTextUserAddressEdit)

        if(moradaUtilizador.text.toString() == "") {
            val user = UtilizadoresItem(currentUserName, currentUser!!.email, currentUserAddress, downUrl, currentUser.uid)
            db.collection("users").document(currentUser.uid)
                .set(user.toHashMap())
                .addOnSuccessListener {
                    Log.d("writeBD", "DocumentSnapshot successfully written!")
                    myDialog.dismiss()
                }
                .addOnFailureListener { e ->
                    Log.w("writeBD", "Error writing document", e)
                }
        }else{
            val user = UtilizadoresItem(currentUserName, currentUser!!.email, moradaUtilizador.text.toString(), downUrl, currentUser.uid)
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