package healthscheduler.example.healthscheduler

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import healthscheduler.example.healthscheduler.databinding.ActivityChatMessagesBinding
import healthscheduler.example.healthscheduler.models.MessageItem
import healthscheduler.example.healthscheduler.models.UsersItem
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class ChatMessagesActivity : AppCompatActivity() {

    private val currentUid = FirebaseAuth.getInstance().uid
    private var currentUser : UsersItem? = null
    private var toUser : UsersItem? = null
    private val db = FirebaseFirestore.getInstance()
    private var refCurrentUser = db.collection("users")
    private var refMessages = db.collection("chat_messages")

    private var message : MessageItem? = null
    private var mAdapter : RecyclerView.Adapter<*>? = null
    private var mLayoutManager : LinearLayoutManager? = null
    private var messagesList : MutableList<MessageItem> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityChatMessagesBinding.inflate(layoutInflater)
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

        binding.buttonChatMessageSendTextMessage.setOnClickListener {

            val messageText = binding.editTextChatMessagesWriteMessage.text.toString()

            if (messageText != "") {

                performSendTextMessage(messageText)
                binding.editTextChatMessagesWriteMessage.text.clear()
                binding.recyclerViewChatLog.scrollToPosition(
                        (mAdapter as ChatMessagesAdapter).itemCount -1)
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

        fromReference.add(message!!.toHashMap()).addOnSuccessListener {

        }

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
                    //return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_image_message_from, parent, false))
                }
                4 -> {
                    //return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_image_message_to, parent, false))
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
                        }
                    }
                }
                "image" -> {

                    if (messagesList[position].fromId == currentUser?.userID) {


                    }
                    else {


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