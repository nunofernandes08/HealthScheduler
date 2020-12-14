package healthscheduler.example.healthscheduler

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.opengl.Visibility
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import healthscheduler.example.healthscheduler.databinding.ActivityChatMessagesBinding
import healthscheduler.example.healthscheduler.databinding.ActivityChatMessagesV2Binding
import healthscheduler.example.healthscheduler.models.MessageItem
import healthscheduler.example.healthscheduler.models.UsersItem
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class ChatMessagesActivity : AppCompatActivity() {

    companion object {
        const val IMAGE_PICK = 0
    }

    private val currentUid  = FirebaseAuth.getInstance().uid
    private val db          = FirebaseFirestore.getInstance()

    private val refCurrentUser      = db.collection("users")
    private val refMessages         = db.collection("chat_messages")
    private val ref                 = FirebaseStorage.getInstance()
    private var currentUser         : UsersItem? = null
    private var toUser              : UsersItem? = null
    private var message             : MessageItem? = null
    private var mAdapter            : RecyclerView.Adapter<*>? = null
    private var selectedPhotoUri    : Uri? = null


    private var mLayoutManager  : LinearLayoutManager? = null
    private var messagesList    : MutableList<MessageItem> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityChatMessagesV2Binding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        toUser = intent.getParcelableExtra<UsersItem>(ContactsActivity.USER_KEY)
        binding.textViewTitleChatMessagesContactName.text = toUser?.username.toString()

        if (toUser?.imagePath != "") {

            Picasso.get().load(toUser?.imagePath).into(binding.imageViewChatMessagesContactPhoto)
        }

        mLayoutManager = LinearLayoutManager(
            this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewChatLog.layoutManager = mLayoutManager
        mAdapter = ChatMessagesAdapter()
        binding.recyclerViewChatLog.itemAnimator = DefaultItemAnimator()
        binding.recyclerViewChatLog.adapter = mAdapter

        getCurrentUser()

        listenForMessages(binding.recyclerViewChatLog)

        buttonsActions(binding)
    }

    //Funcao com as acoes dos botoes
    private fun buttonsActions(binding: ActivityChatMessagesV2Binding){

        binding.editTextChatMessagesWriteMessage.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                if (p0.isNullOrBlank()) {

                    binding.buttonChatMessageSendImageMessage.visibility = View.VISIBLE
                    binding.editTextChatMessagesWriteMessage.layoutParams.width = resources.displayMetrics.density.toInt() * 260
                }
                else {

                    binding.buttonChatMessageSendImageMessage.visibility = View.GONE
                    binding.editTextChatMessagesWriteMessage.layoutParams.width = resources.displayMetrics.density.toInt() * 306
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
            override fun afterTextChanged(p0: Editable?) { }
        })

        binding.buttonChatMessageSendTextMessage.setOnClickListener {

            val messageText = binding.editTextChatMessagesWriteMessage.text.toString()

            if (messageText != "") {

                performSendTextMessage(messageText)
                binding.editTextChatMessagesWriteMessage.text.clear()
                binding.recyclerViewChatLog.scrollToPosition(
                        (mAdapter as ChatMessagesAdapter).itemCount -1)
            }
        }

        binding.buttonChatMessageSendImageMessage.setOnClickListener {

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, IMAGE_PICK)
            binding.recyclerViewChatLog.scrollToPosition(
                    (mAdapter as ChatMessagesAdapter).itemCount -1)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_PICK && resultCode == Activity.RESULT_OK && data != null) {

            selectedPhotoUri = data.data
            uploadImageToFirebaseStorage()
        }
    }

    private fun uploadImageToFirebaseStorage() {

        val filename = UUID.randomUUID().toString()
        val refUploadImage = ref.getReference("/images/$filename")
        selectedPhotoUri?.let { uri ->

            val bitmap : Bitmap
            if (Build.VERSION.SDK_INT < 28) {

                bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedPhotoUri)
            }
            else {

                bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, uri))
            }

            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos)
            val data = baos.toByteArray()

            refUploadImage.putBytes(data).addOnSuccessListener {

                refUploadImage.downloadUrl.addOnSuccessListener {

                    performSendImageMessage(it.toString())
                }
            }
        }
    }

    private fun getCurrentUser() {

        refCurrentUser
                .document(currentUid.toString())
                .addSnapshotListener { snapshot, error ->

                    if (snapshot != null && snapshot.exists()) {

                        currentUser = UsersItem.fromHash(snapshot.data as HashMap<String, Any?>)
                    }
                }
    }

    private fun listenForMessages(recyclerView: RecyclerView) {

        refMessages
                .document(currentUid.toString())
                .collection(toUser?.userID!!)
                .orderBy("timeStamp")
                .addSnapshotListener { snapshot, error ->

                    messagesList.clear()
                    if (snapshot != null) {

                        for (doc in snapshot) {

                            message = MessageItem.fromHash(doc.data as HashMap<String, Any?>)
                            messagesList.add(message!!)
                        }
                    }
                    mAdapter?.notifyDataSetChanged()
                    recyclerView.scrollToPosition(
                            (mAdapter as ChatMessagesAdapter).itemCount -1)
                }
    }

    private fun performSendTextMessage(messageText : String) {

        val fromReference = refMessages
                .document(currentUser?.userID!!)
                .collection(toUser?.userID!!)

        val toReference = refMessages
                .document(toUser?.userID!!)
                .collection(currentUser?.userID!!)

        message = MessageItem(messageText, currentUser?.userID!!, toUser?.userID!!,
                System.currentTimeMillis() / 1000,"text")

        fromReference.add(message!!.toHashMap())
        toReference.add(message!!.toHashMap())

        val fromLatestReference = FirebaseFirestore.getInstance()
                .collection("latest_messages")
                .document(currentUser?.userID!!)
                .collection("latest_message")
                .document(toUser?.userID!!)
        fromLatestReference.set(message!!.toHashMap())

        val toLatestReference = FirebaseFirestore.getInstance()
                .collection("latest_messages")
                .document(toUser?.userID!!)
                .collection("latest_message")
                .document(currentUser?.userID!!)
        toLatestReference.set(message!!.toHashMap())
    }

    private fun performSendImageMessage (imageURL : String) {

        val fromReference = refMessages
                .document(currentUser?.userID!!)
                .collection(toUser?.userID!!)

        val toReference = refMessages
                .document(toUser?.userID!!)
                .collection(currentUser?.userID!!)

        message = MessageItem(imageURL, currentUser?.userID!!, toUser?.userID!!,
                System.currentTimeMillis() / 1000,"image")

        fromReference.add(message!!.toHashMap())
        toReference.add(message!!.toHashMap())

        val fromLatestReference = FirebaseFirestore.getInstance()
                .collection("latest_messages")
                .document(currentUser?.userID!!)
                .collection("latest_message")
                .document(toUser?.userID!!)
        fromLatestReference.set(message!!.toHashMap())

        val toLatestReference = FirebaseFirestore.getInstance()
                .collection("latest_messages")
                .document(toUser?.userID!!)
                .collection("latest_message")
                .document(currentUser?.userID!!)
        toLatestReference.set(message!!.toHashMap())
    }

    inner class ChatMessagesAdapter : RecyclerView.Adapter<ChatMessagesAdapter.ViewHolder>() {

        inner class ViewHolder(val v : View) : RecyclerView.ViewHolder(v)

        override fun getItemViewType(position: Int): Int {

            when (messagesList[position].messageType) {

                "text" -> {

                    if (messagesList[position].fromId == currentUser?.userID) {

                        return 1
                    }
                    else {

                        return 2
                    }
                }
                "image" -> {

                    if (messagesList[position].fromId == currentUser?.userID) {

                        return 3
                    }
                    else {

                        return 4
                    }
                }
                "audio" -> {

                    if (messagesList[position].fromId == currentUser?.userID) {

                        return 5
                    }
                    else {

                        return 6
                    }
                }
            }
            return 0
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

            when (viewType) {

                1 -> {
                    return ViewHolder(LayoutInflater.from(parent.context)
                            .inflate(R.layout.row_text_message_from, parent, false))
                }
                2 -> {
                    return ViewHolder(LayoutInflater.from(parent.context)
                            .inflate(R.layout.row_text_message_to, parent, false))
                }
                3 -> {
                    return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_image_message_from, parent, false))
                }
                4 -> {
                    return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_image_message_to, parent, false))
                }
                5 -> {
                    //return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_audio_message_from, parent, false))
                }
                6 -> {
                    //return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_audio_message_to, parent, false))
                }
            }
            return ViewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.row_text_message_from, parent, false))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            when (messagesList[position].messageType) {

                "text" -> {

                    if (messagesList[position].fromId == currentUid) {

                        holder.v.apply {

                            val textViewChatMessageFrom = findViewById<TextView>(
                                    R.id.textViewChatMessageFrom)
                            val imageViewChatMessageContactPhotoFrom = findViewById<ImageView>(
                                    R.id.imageViewChatMessageContactPhotoFrom)
                            val textViewChatMessageTimeStampFrom = findViewById<TextView>(
                                    R.id.textViewChatMessageTimeStampFrom)

                            textViewChatMessageFrom.text = messagesList[position].message

                            val sec = (System.currentTimeMillis() / 1000) - messagesList[position].timeStamp!!
                            if (sec <= 86400) {

                                val sdf = SimpleDateFormat("HH:mm", Locale.UK)
                                val netDate = Date(messagesList[position].timeStamp?.times(1000)!!)
                                val date = sdf.format(netDate)
                                textViewChatMessageTimeStampFrom.text = date
                            }
                            else {

                                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.UK)
                                val netDate = Date(messagesList[position].timeStamp?.times(1000)!!)
                                val date = sdf.format(netDate)
                                textViewChatMessageTimeStampFrom.text = date
                            }

                            if (currentUser?.imagePath != "") {

                                Picasso.get().load(currentUser?.imagePath).into(imageViewChatMessageContactPhotoFrom)
                            }
                            else {
                                imageViewChatMessageContactPhotoFrom.setBackgroundResource(R.drawable.imageviewfotofavorito1)
                            }
                        }
                    }
                    else {

                        holder.v.apply {

                            val textViewChatMessageTo = findViewById<TextView>(
                                    R.id.textViewChatMessageTo)
                            val imageViewChatMessageContactPhotoTo = findViewById<ImageView>(
                                    R.id.imageViewChatMessageContactPhotoTo)
                            val textViewChatMessageTimeStampTo = findViewById<TextView>(
                                    R.id.textViewChatMessageTimeStampTo)

                            textViewChatMessageTo.text = messagesList[position].message

                            val sec = (System.currentTimeMillis() / 1000) - messagesList[position].timeStamp!!
                            if (sec <= 86400) {

                                val sdf = SimpleDateFormat("HH:mm", Locale.UK)
                                val netDate = Date(messagesList[position].timeStamp?.times(1000)!!)
                                val date = sdf.format(netDate)
                                textViewChatMessageTimeStampTo.text = date
                            }
                            else {

                                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.UK)
                                val netDate = Date(messagesList[position].timeStamp?.times(1000)!!)
                                val date = sdf.format(netDate)
                                textViewChatMessageTimeStampTo.text = date
                            }

                            if (toUser?.imagePath != "") {

                                Picasso.get().load(toUser?.imagePath).into(imageViewChatMessageContactPhotoTo)
                            }
                            else {
                                imageViewChatMessageContactPhotoTo.setBackgroundResource(R.drawable.imageviewfotofavorito1)
                            }
                        }
                    }
                }
                "image" -> {

                    if (messagesList[position].fromId == currentUser?.userID) {

                        holder.v.apply {

                            val imageViewChatImageMessageFrom = findViewById<ImageView>(
                                    R.id.imageViewChatImageMessageFrom)
                            val imageViewChatImageMessageContactPhotoFrom = findViewById<ImageView>(
                                    R.id.imageViewChatImageMessageContactPhotoFrom)
                            val textViewChatImageMessageTimeStampFrom = findViewById<TextView>(
                                    R.id.textViewChatImageMessageTimeStampFrom)

                            Picasso.get().load(messagesList[position].message).into(imageViewChatImageMessageFrom)

                            val sec = (System.currentTimeMillis() / 1000) - messagesList[position].timeStamp!!
                            if (sec <= 86400) {

                                val sdf = SimpleDateFormat("HH:mm", Locale.UK)
                                val netDate = Date(messagesList[position].timeStamp?.times(1000)!!)
                                val date = sdf.format(netDate)
                                textViewChatImageMessageTimeStampFrom.text = date
                            }
                            else {

                                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.UK)
                                val netDate = Date(messagesList[position].timeStamp?.times(1000)!!)
                                val date = sdf.format(netDate)
                                textViewChatImageMessageTimeStampFrom.text = date
                            }

                            if (currentUser?.imagePath != "") {

                                Picasso.get().load(currentUser?.imagePath).into(imageViewChatImageMessageContactPhotoFrom)
                            }
                            else {
                                imageViewChatImageMessageContactPhotoFrom.setBackgroundResource(R.drawable.imageviewfotofavorito1)
                            }
                        }
                    }
                    else {

                        holder.v.apply {

                            val imageViewChatImageMessageTo = findViewById<ImageView>(
                                    R.id.imageViewChatImageMessageTo)
                            val imageViewChatImageMessageContactPhotoTo = findViewById<ImageView>(
                                    R.id.imageViewChatImageMessageContactPhotoTo)
                            val textViewChatImageMessageTimeStampTo = findViewById<TextView>(
                                    R.id.textViewChatImageMessageTimeStampTo)

                            Picasso.get().load(messagesList[position].message).into(imageViewChatImageMessageTo)

                            val sec = (System.currentTimeMillis() / 1000) - messagesList[position].timeStamp!!
                            if (sec <= 86400) {

                                val sdf = SimpleDateFormat("HH:mm", Locale.UK)
                                val netDate = Date(messagesList[position].timeStamp?.times(1000)!!)
                                val date = sdf.format(netDate)
                                textViewChatImageMessageTimeStampTo.text = date
                            }
                            else {

                                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.UK)
                                val netDate = Date(messagesList[position].timeStamp?.times(1000)!!)
                                val date = sdf.format(netDate)
                                textViewChatImageMessageTimeStampTo.text = date
                            }

                            if (toUser?.imagePath != "") {

                                Picasso.get().load(toUser?.imagePath).into(imageViewChatImageMessageContactPhotoTo)
                            }
                            else {
                                imageViewChatImageMessageContactPhotoTo.setBackgroundResource(R.drawable.imageviewfotofavorito1)
                            }
                        }
                    }
                }
                "audio" -> {

                    if (messagesList[position].fromId == currentUser?.userID) {


                    }
                    else {


                    }
                }
            }
        }

        override fun getItemCount(): Int {
            return messagesList.size
        }
    }
}