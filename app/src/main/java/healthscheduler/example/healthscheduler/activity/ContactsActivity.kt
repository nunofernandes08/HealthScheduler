package healthscheduler.example.healthscheduler.activity

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
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import healthscheduler.example.healthscheduler.R
import healthscheduler.example.healthscheduler.databinding.ActivityContactsBinding
import healthscheduler.example.healthscheduler.models.DoctorsItem
import healthscheduler.example.healthscheduler.models.UsersItem

class ContactsActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    private lateinit var currentUser    : UsersItem
    private var mAdapter                : RecyclerView.Adapter<*>? = null
    private var mLayoutManager          : LinearLayoutManager? = null
    private var users                   : MutableList<DoctorsItem> = arrayListOf()
    private var referenceUsersMedic     = db.collection("users_medic")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityContactsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        currentUser = intent.getParcelableExtra<UsersItem>(USER_KEY)!!

        //RecyclerView
        mLayoutManager = LinearLayoutManager(
                this, LinearLayoutManager.VERTICAL,false)
        binding.recyclerViewContacts.layoutManager = mLayoutManager
        mAdapter = ContactsAdapter()
        binding.recyclerViewContacts.itemAnimator = DefaultItemAnimator()
        binding.recyclerViewContacts.setHasFixedSize(true)
        binding.recyclerViewContacts.addItemDecoration(DividerItemDecoration(
                this, DividerItemDecoration.VERTICAL))
        binding.recyclerViewContacts.adapter = mAdapter

        buttonsActions(binding)

        //Lista de Contactos
        currentUser.let {
            referenceUsersMedic.whereEqualTo("typeOfAcc", "Medico").addSnapshotListener { snapshot, error ->
                users.clear()
                if (snapshot != null) {
                    for (doc in snapshot) {
                        val user = DoctorsItem.fromHash(doc.data as HashMap<String, Any?>)
                        users.add(user)
                    }
                }
                mAdapter?.notifyDataSetChanged()
            }
        }
    }

    //Funcao com as acoes dos botoes
    private fun buttonsActions(binding: ActivityContactsBinding){
        binding.floatingActionButton.setOnClickListener{
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    //Adapter da RecyclerView
    inner class ContactsAdapter : RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {

        inner class ViewHolder(val v : View) : RecyclerView.ViewHolder(v)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.row_contact, parent, false))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            holder.v.apply {

                val imageViewUser = this.findViewById<ImageView>(R.id.imageViewChatContactsContactImage)
                val textViewUser = this.findViewById<TextView>(R.id.textViewChatContactsContactName)
                val textViewChatContactsContactSpec = this.findViewById<TextView>(R.id.textViewChatContactsContactSpec)

                this.isClickable = true
                this.tag = position

                textViewUser.text = "Dr. " +  users[position].username
                textViewChatContactsContactSpec.text = users[position].typeOfMedic

                if (users[position].imagePath != "") {

                    Picasso.get().load(users[position].imagePath).into(imageViewUser)
                }
                else {
                    imageViewUser.setBackgroundResource(R.drawable.imageviewfotofavorito1)
                }

                this.setOnClickListener {

                    val user = users[position]
                    val intent = Intent(
                            this@ContactsActivity,
                            ChatMessagesActivity::class.java)
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

