package healthscheduler.example.healthscheduler

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import healthscheduler.example.healthscheduler.databinding.ActivityContactsBinding
import healthscheduler.example.healthscheduler.models.UtilizadoresItem

class ContactsActivity : AppCompatActivity() {

    private var currentUserId : String? = null
    private val db = FirebaseFirestore.getInstance()
    private var ref = db.collection("users")

    private var mAdapter : RecyclerView.Adapter<*>? = null
    private var mLayoutManager : LinearLayoutManager? = null
    private var users : MutableList<UtilizadoresItem> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityContactsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        mLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewContacts.layoutManager = mLayoutManager
        mAdapter = ContactsAdapter()
        binding.recyclerViewContacts.itemAnimator = DefaultItemAnimator()
        binding.recyclerViewContacts.adapter = mAdapter

        currentUserId = FirebaseAuth.getInstance().uid

        currentUserId?.let {

            ref.addSnapshotListener { querySnapshot, Exception ->

                if (querySnapshot != null) {

                    for (doc in querySnapshot) {

                        val user = UtilizadoresItem.fromHash(doc.data as HashMap<String, Any?>)

                        if (user.userID != currentUserId) {

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

                this.isClickable = true
                this.tag = position
            }
        }

        override fun getItemCount(): Int {
            return users.size
        }
    }
}