package healthscheduler.example.healthscheduler

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import healthscheduler.example.healthscheduler.Login.MainActivity
import healthscheduler.example.healthscheduler.databinding.ActivityContactsBinding
import healthscheduler.example.healthscheduler.models.UsersItem

class ContactsActivity : AppCompatActivity() {

    private var currentUserId : String? = null
    private val db = FirebaseFirestore.getInstance()
    private var ref = db.collection("users")

    private var mAdapter : RecyclerView.Adapter<*>? = null
    private var mLayoutManager : LinearLayoutManager? = null
    private var users : MutableList<UsersItem> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityContactsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        mLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewContacts.layoutManager = mLayoutManager
        mAdapter = ContactsAdapter()
        binding.recyclerViewContacts.itemAnimator = DefaultItemAnimator()
        binding.recyclerViewContacts.setHasFixedSize(true)
        binding.recyclerViewContacts.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        binding.recyclerViewContacts.adapter = mAdapter

        currentUserId = FirebaseAuth.getInstance().uid

        currentUserId?.let {

            ref.addSnapshotListener { querySnapshot, Exception ->

                users.clear()
                if (querySnapshot != null) {

                    for (doc in querySnapshot) {

                        val user = UsersItem.fromHash(doc.data as HashMap<String, Any?>)
                        if (user.userID != currentUserId) {

                            users.add(user)
                        }
                    }
                }
                mAdapter?.notifyDataSetChanged()
            }
        } ?: run {

            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    inner class ContactsAdapter : RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {

        inner class ViewHolder(val v : View) : RecyclerView.ViewHolder(v)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_contact, parent, false))
        }

        override fun onBindViewHolder(holder: ContactsAdapter.ViewHolder, position: Int) {

            holder.v.apply {

                var imageViewUser = findViewById<ImageView>(R.id.imageViewChatContactsContactImage)
                var textViewUser = findViewById<TextView>(R.id.textViewChatContactsContactName)

                this.isClickable = true
                this.tag = position
                textViewUser.text = users[position].username

                if (users[position].imagePath != null) {

                    Picasso.get().load(users[position].imagePath).into(imageViewUser)
                }

                this.setOnClickListener {

                    val user = users[position]

                    val intent = Intent(this@ContactsActivity, ChatMessagesActivity::class.java)
                    intent.putExtra(USER_KEY, user)
                    //intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
            }
        }

        override fun getItemCount(): Int {
            return users.size
        }
    }

    companion object {

        val USER_KEY = "USER_KEY"
    }
}

