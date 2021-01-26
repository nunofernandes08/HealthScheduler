package healthscheduler.example.healthscheduler.activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
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
import kotlinx.android.synthetic.main.popwindow_alertinternet.*

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


        checkConnection()
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

    private fun checkConnection(){
        val manager = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = manager.activeNetworkInfo

        if(null != networkInfo){
            if(networkInfo.type == ConnectivityManager.TYPE_WIFI){
            }else if(networkInfo.type == ConnectivityManager.TYPE_MOBILE){
                //Toast.makeText(this, "Mobile Data Connected", Toast.LENGTH_SHORT).show()
            }
        }else{
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.popwindow_alertinternet)
            //USAR ISTO NOS OUTROS DIALOGS
            dialog.setCanceledOnTouchOutside(false)
            dialog.window!!.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            dialog.buttonTryAgainPopWindowAlert.setOnClickListener {
                recreate()
            }
            dialog.show()

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

