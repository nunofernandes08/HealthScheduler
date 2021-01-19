package healthscheduler.example.healthscheduler.activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso
import healthscheduler.example.healthscheduler.R
import healthscheduler.example.healthscheduler.databinding.ActivityLatestMessagesBinding
import healthscheduler.example.healthscheduler.models.DoctorsItem
import healthscheduler.example.healthscheduler.models.MessageItem
import healthscheduler.example.healthscheduler.models.UsersItem
import kotlinx.android.synthetic.main.popwindow_alertinternet.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class LatestMessagesActivity : AppCompatActivity() {

    private val db                      = FirebaseFirestore.getInstance()
    private var referenceUsersMedic     = db.collection("users_medic")
    private var refLatestMessages       = db.collection("latest_messages")

    private lateinit var currentUser    : UsersItem
    private var message                 : MessageItem? = null
    private var toUser                  : DoctorsItem? = null
    private var latestMessages          : MutableList<MessageItem> = arrayListOf()
    private var users                   : MutableList<DoctorsItem> = arrayListOf()
    private var mAdapter                : RecyclerView.Adapter<*>? = null
    private var mLayoutManager          : LinearLayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityLatestMessagesBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        checkConnection()
        buttonsActions(binding)
        imageViewActions(binding)

        currentUser = intent.getParcelableExtra<UsersItem>(ContactsActivity.USER_KEY)!!
        if (currentUser.imagePath != "") {
            Picasso.get().load(currentUser.imagePath).into(binding.imageViewChatHomePhotoUser)
        }

        getLatestMessages()

        mLayoutManager = LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                false)
        binding.recyclerViewLatestMessages.layoutManager = mLayoutManager
        mAdapter = LatestMessagesAdapter()
        binding.recyclerViewLatestMessages.itemAnimator = DefaultItemAnimator()
        binding.recyclerViewLatestMessages.setHasFixedSize(true)
        binding.recyclerViewLatestMessages.addItemDecoration(DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL))
        binding.recyclerViewLatestMessages.adapter = mAdapter
    }

    private fun checkConnection(){
        val manager = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = manager.activeNetworkInfo

        if(null != networkInfo){
            if(networkInfo.type == ConnectivityManager.TYPE_WIFI){
            }else if(networkInfo.type == ConnectivityManager.TYPE_MOBILE){
                Toast.makeText(this, "Mobile Data Connected", Toast.LENGTH_SHORT).show()
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

    private fun getLatestMessages() {

        referenceUsersMedic.addSnapshotListener { snapshot, error ->
            users.clear()
            if (snapshot != null) {
                for (doc in snapshot) {
                    val user = DoctorsItem.fromHash(doc.data as HashMap<String, Any?>)
                    users.add(user)
                }
            }
            mAdapter?.notifyDataSetChanged()
        }

        refLatestMessages.document(currentUser.userID.toString())
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
                mAdapter?.notifyDataSetChanged()
            }
    }

    //Funcao com as acoes dos botoes
    private fun buttonsActions(binding: ActivityLatestMessagesBinding){
        binding.floatingActionButtonSendNewMessage.setOnClickListener {
            val intent = Intent(this, ContactsActivity::class.java)
            intent.putExtra(ContactsActivity.USER_KEY, currentUser)
            startActivity(intent)
        }

        binding.floatingActionButton.setOnClickListener{
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    //Funcao com as acoes das imageViews
    private fun imageViewActions(binding: ActivityLatestMessagesBinding){
        //ImageViewUserPhoto ao clicar vai para o perfil
        binding.imageViewChatHomePhotoUser.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    inner class LatestMessagesAdapter : RecyclerView.Adapter<LatestMessagesAdapter.ViewHolder>() {

        inner class ViewHolder(val v : View) : RecyclerView.ViewHolder(v)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.row_latest_messages, parent, false))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            holder.v.apply {

                val toUserId: String?

                val textViewChatHomeLatestContactLatestMessage = this.findViewById<TextView>(
                        R.id.textViewChatHomeLatestContactLatestMessage)
                val textViewChatHomeLatestContactName = this.findViewById<TextView>(
                        R.id.textViewChatHomeLatestContactName)
                val textViewChatHomeLatestContactDate = this.findViewById<TextView>(
                        R.id.textViewChatHomeLatestContactDate)
                val imageViewChatHomeLatestContactImage = this.findViewById<ImageView>(
                        R.id.imageViewChatHomeLatestContactImage)

                this.isClickable = true
                this.tag = position

                if (latestMessages[position].fromId == currentUser.userID) {

                    toUserId = latestMessages[position].toId.toString()
                }
                else {

                    toUserId = latestMessages[position].fromId.toString()
                }

                for (item in users) {

                    if (item.medicID == toUserId) {

                        toUser = item
                    }
                }

                if (latestMessages[position].messageType == "text") {

                    textViewChatHomeLatestContactLatestMessage.text = latestMessages[position].message
                }
                else if (latestMessages[position].messageType == "image") {

                    textViewChatHomeLatestContactLatestMessage.text = "Image"
                }
                else {

                    textViewChatHomeLatestContactLatestMessage.text = "Audio"
                }

                textViewChatHomeLatestContactName.text = "Dr. " +  toUser?.username

                val sec = (System.currentTimeMillis().div(1000))
                        .minus(latestMessages[position].timeStamp!!)
                if (sec <= 86400) {

                    val sdf = SimpleDateFormat("HH:mm", Locale.UK)
                    val netDate = Date(latestMessages[position].timeStamp?.times(1000)!!)
                    val date = sdf.format(netDate)
                    textViewChatHomeLatestContactDate.text = date
                }
                else {

                    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.UK)
                    val netDate = Date(latestMessages[position].timeStamp?.times(1000)!!)
                    val date = sdf.format(netDate)
                    textViewChatHomeLatestContactDate.text = date
                }

                if (toUser?.imagePath != "") {

                    Picasso.get().load(toUser?.imagePath).into(imageViewChatHomeLatestContactImage)
                }
                else {
                    imageViewChatHomeLatestContactImage
                            .setBackgroundResource(R.drawable.imageviewfotofavorito1)
                }

                this.setOnClickListener {

                    for (item in users) {

                        if (item.medicID == latestMessages[position].toId || item.medicID == latestMessages[position].fromId) {

                            toUser = item
                        }
                    }

                    val intent = Intent(
                            this@LatestMessagesActivity,
                            ChatMessagesActivity::class.java)
                    intent.putExtra(ContactsActivity.USER_KEY, toUser)
                    startActivity(intent)
                }
            }
        }

        override fun getItemCount(): Int {
            return latestMessages.size
        }
    }
}