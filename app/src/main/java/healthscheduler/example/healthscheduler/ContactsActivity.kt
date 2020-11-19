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

    private lateinit var currentUser : UsersItem
    private val db = FirebaseFirestore.getInstance()
    private var referenceUsers = db.collection("users")

    private var mAdapter : RecyclerView.Adapter<*>? = null
    private var mLayoutManager : LinearLayoutManager? = null
    private var users : MutableList<UsersItem> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityContactsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        currentUser = intent.getParcelableExtra<UsersItem>(USER_KEY)!!

        mLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewContacts.layoutManager = mLayoutManager
        mAdapter = ContactsAdapter()
        binding.recyclerViewContacts.itemAnimator = DefaultItemAnimator()
        binding.recyclerViewContacts.setHasFixedSize(true)
        binding.recyclerViewContacts.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        binding.recyclerViewContacts.adapter = mAdapter

        currentUser.let {

            referenceUsers.addSnapshotListener { snapshot, error ->

                users.clear()
                if (snapshot != null) {

                    for (doc in snapshot) {

                        val user = UsersItem.fromHash(doc.data as HashMap<String, Any?>)
                        if (user.userID != currentUser.userID) {

                            users.add(user)
                        }
                    }
                }
                mAdapter?.notifyDataSetChanged()
            }
        }
    }

    inner class ContactsAdapter : RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {

        inner class ViewHolder(val v : View) : RecyclerView.ViewHolder(v)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_contact, parent, false))
        }

        override fun onBindViewHolder(holder: ContactsAdapter.ViewHolder, position: Int) {

            holder.v.apply {

                val imageViewUser = findViewById<ImageView>(R.id.imageViewChatContactsContactImage)
                val textViewUser = findViewById<TextView>(R.id.textViewChatContactsContactName)

                this.isClickable = true
                this.tag = position
                textViewUser.text = users[position].username

                if (users[position].imagePath != "") {

                    Picasso.get().load(users[position].imagePath).into(imageViewUser)
                }

                this.setOnClickListener {

                    val user = users[position]

                    val intent = Intent(this@ContactsActivity, ChatMessagesActivity::class.java)
                    intent.putExtra(USER_KEY, user)
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

