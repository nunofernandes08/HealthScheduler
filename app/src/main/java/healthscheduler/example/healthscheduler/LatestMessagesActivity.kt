package healthscheduler.example.healthscheduler

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso
import healthscheduler.example.healthscheduler.databinding.ActivityLatestMessagesBinding
import healthscheduler.example.healthscheduler.models.MessageItem
import healthscheduler.example.healthscheduler.models.UsersItem

class LatestMessagesActivity : AppCompatActivity() {

    private lateinit var currentUser : UsersItem
    private val db = FirebaseFirestore.getInstance()
    private var referenceUsers = db.collection("users")
    private var refLatestMessages = db.collection("latest_messages")

    private var message : MessageItem? = null
    private var latestMessages : MutableList<MessageItem> = arrayListOf()
    private var mAdapter : RecyclerView.Adapter<*>? = null
    private var mLayoutManager : LinearLayoutManager? = null
    private var users : MutableList<MessageItem> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityLatestMessagesBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        currentUser = intent.getParcelableExtra<UsersItem>(ContactsActivity.USER_KEY)!!
        if (currentUser.imagePath != "") {

            Picasso.get().load(currentUser.imagePath).into(binding.imageViewChatHomePhotoUser)
        }

        mLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewLatestMessages.layoutManager = mLayoutManager
        mAdapter = LatestMessagesAdapter()
        binding.recyclerViewLatestMessages.itemAnimator = DefaultItemAnimator()
        binding.recyclerViewLatestMessages.setHasFixedSize(true)
        binding.recyclerViewLatestMessages.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        binding.recyclerViewLatestMessages.adapter = mAdapter

        refLatestMessages.document(currentUser.userID.toString())
                .collection("latest_message")
                .orderBy("timeStamp", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->

            if (snapshot != null) {

                for (doc in snapshot) {

                    message = MessageItem.fromHash(doc.data as HashMap<String, Any?>)
                    latestMessages.add(message!!)
                }
            }
                    mAdapter?.notifyDataSetChanged()
        }

        binding.floatingActionButtonSendNewMessage.setOnClickListener {

            val intent = Intent(this, ContactsActivity::class.java)
            intent.putExtra(ContactsActivity.USER_KEY, currentUser)
            startActivity(intent)
        }
    }

    inner class LatestMessagesAdapter : RecyclerView.Adapter<LatestMessagesAdapter.ViewHolder>() {

        inner class ViewHolder(val v : View) : RecyclerView.ViewHolder(v)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_latest_messages, parent, false))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            holder.v.apply {

                val textViewChatHomeLatestContactLatestMessage = findViewById<TextView>(R.id.textViewChatHomeLatestContactLatestMessage)
                val textViewChatHomeLatestContactName = findViewById<TextView>(R.id.textViewChatHomeLatestContactName)
                val textViewChatHomeLatestContactDate = findViewById<TextView>(R.id.textViewChatHomeLatestContactDate)
                val imageViewChatHomeLatestContactImage = findViewById<ImageView>(R.id.imageViewChatHomeLatestContactImage)

                this.isClickable = true
                this.tag = position


            }
        }

        override fun getItemCount(): Int {
            return latestMessages.size
        }


    }
}