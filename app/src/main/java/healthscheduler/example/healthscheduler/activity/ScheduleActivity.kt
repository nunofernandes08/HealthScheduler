package healthscheduler.example.healthscheduler.activity

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import healthscheduler.example.healthscheduler.R
import healthscheduler.example.healthscheduler.databinding.ActivityScheduleV3Binding
import healthscheduler.example.healthscheduler.models.AppointDate
import healthscheduler.example.healthscheduler.models.DoctorsItem
import healthscheduler.example.healthscheduler.models.ScheduleItem
import healthscheduler.example.healthscheduler.models.UsersItem
import kotlinx.android.synthetic.main.item_view_pager_schedule.view.*
import kotlinx.android.synthetic.main.popwindow_schedule_detail.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class ScheduleActivity : AppCompatActivity() {

    private val db          = FirebaseFirestore.getInstance()
    private val auth        = Firebase.auth
    private val currentUser = auth.currentUser

    private var listAppointDates    : MutableList<AppointDate> = arrayListOf()
    private val listSchedule        : MutableList<ScheduleItem> = arrayListOf()
    private var datesAdapter        : ViewPagerAdapter? = null

    private var listUser : UsersItem? = null
    private var toUser : DoctorsItem? = null

    private lateinit var myDialog: Dialog

    //@RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityScheduleV3Binding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        photoUser(binding)
        buttonsActions(binding)
        imageViewActions(binding)
        //getAppointmentDates()

        datesAdapter = ViewPagerAdapter(listAppointDates)
        binding.viewPager.adapter = datesAdapter

        //Cria lista de datas para as tabs
        db.collection("consultas").orderBy("date")
            .whereEqualTo("userID", currentUser!!.uid)
            .addSnapshotListener { snapshot, error ->
                snapshot?.let {
                    listAppointDates.clear()
                    for (document in snapshot) {
                        val date = AppointDate(document.data.getValue("date").toString())
                        var exist = false
                        for (item in listAppointDates) {
                            if (date.date == item.date) {
                                exist = true
                            }
                        }
                        if (!exist) {
                            listAppointDates.add(date)
                        }
                    }
                    datesAdapter?.notifyDataSetChanged()
                }
            }

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

                val local = Locale("pt", "PT")
                val formatter = DateTimeFormatter.ofPattern("E dd LLLL", local)
                val date = LocalDate.parse(listAppointDates[position].date.toString())
                tab.text = date.format(formatter)
            }
            else {

                val local = Locale("pt", "PT")
                val parser = SimpleDateFormat("yyyy-MM-dd")
                val formatter = SimpleDateFormat("E dd LLLL", local)
                val output: String = formatter.format(parser.parse(listAppointDates[position].date.toString()))
                tab.text = output
            }
        }.attach()
    }

    //Funcao que vai buscar a foto do CURRENTUSER
    private fun photoUser(binding: ActivityScheduleV3Binding){

        currentUser?.uid?.let {

            db.collection("users").document(it)
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->

                    querySnapshot?.data?.let {

                        listUser = UsersItem.fromHash(querySnapshot.data as HashMap<String, Any?>)
                        listUser?.let { user ->

                            if (user.imagePath != "") {

                                Picasso.get().load(user.imagePath).into(binding.imageViewPhotoUser)
                            }
                            else {

                                Toast.makeText(this@ScheduleActivity, "Não tem foto de perfil",
                                        Toast.LENGTH_SHORT).show()
                            }
                        } ?: run{

                            Toast.makeText(this@ScheduleActivity, "Sem sessão iniciada",
                                    Toast.LENGTH_SHORT).show()
                        }
                    }
                }
        }
    }

    //Funcao com as acoes dos botoes
    private fun buttonsActions(binding: ActivityScheduleV3Binding){

        binding.floatingActionButton.setOnClickListener{

            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
    }

    //Funcao com as acoes das imageViews
    private fun imageViewActions(binding: ActivityScheduleV3Binding){
        //ImageViewUserPhoto ao clicar vai para o perfil
        binding.imageViewPhotoUser.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    //Funcao para ir buscar datas das consultas
    private fun getAppointmentDates () {

        db.collection("consultas").orderBy("date")
            .whereEqualTo("userID", currentUser!!.uid)
            .addSnapshotListener { snapshot, error ->

                snapshot?.let {

                    listAppointDates.clear()
                    for (document in snapshot!!) {

                        listAppointDates.add(AppointDate(
                                document.data.getValue("date").toString()))
                    }
                    datesAdapter?.notifyDataSetChanged()
                }
            }
    }

    inner class ViewPagerAdapter(private val dates: MutableList<AppointDate>) : RecyclerView.Adapter<ViewPagerAdapter.ViewPagerViewHolder>() {

        inner class ViewPagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {
            return ViewPagerViewHolder(LayoutInflater
                    .from(this@ScheduleActivity)
                    .inflate(R.layout.item_view_pager_schedule, parent, false))
        }

        override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {

            val scheduleAdapter = ScheduleAdapterV3()
            val mLayoutManager = LinearLayoutManager(this@ScheduleActivity,
                    LinearLayoutManager.VERTICAL,
                    false)

            holder.itemView.recyclerViewViewPagerSchedule.layoutManager = mLayoutManager
            holder.itemView.recyclerViewViewPagerSchedule.itemAnimator = DefaultItemAnimator()
            holder.itemView.recyclerViewViewPagerSchedule.setHasFixedSize(true)
            holder.itemView.recyclerViewViewPagerSchedule.adapter = scheduleAdapter

            listSchedule.clear()

            db.collection("consultas").orderBy("hour")
                .whereEqualTo("userID", currentUser!!.uid)
                .whereEqualTo("date", dates[position].date.toString())
                .addSnapshotListener { snapshot, error ->

                    snapshot?.let {

                        listSchedule.clear()
                        for (document in snapshot) {

                            listSchedule.add(ScheduleItem(
                                    document.data.getValue("date").toString(),
                                    document.data.getValue("doctorName").toString(),
                                    document.data.getValue("hour").toString(),
                                    document.data.getValue("local").toString(),
                                    document.data.getValue("floor").toString(),
                                    document.data.getValue("pavilion").toString(),
                                    document.data.getValue("cabinet").toString(),
                                    document.data.getValue("typeOfConsult").toString(),
                                    document.data.getValue("medicID").toString()))
                        }
                        scheduleAdapter.notifyDataSetChanged()
                    }
                }
        }

        override fun getItemCount(): Int {
            return dates.size
        }
    }

    inner class ScheduleAdapterV3 : RecyclerView.Adapter<ScheduleAdapterV3.ViewHolder>() {
        inner class ViewHolder(val v: View) : RecyclerView.ViewHolder(v)
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

           return ViewHolder(LayoutInflater
                   .from(parent.context)
                   .inflate(R.layout.row_schedule_v2, parent, false))
        }
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            holder.v.apply {

                getUserMedic(listSchedule[position].medicID!!)

                val textViewDoctorNameSchedule = this.findViewById<TextView>(R.id.textViewDoctorNameSchedule)
                val textViewLocationSchedule = this.findViewById<TextView>(R.id.textViewLocationSchedule)
                val textViewTypeOfConsultSchedule = this.findViewById<TextView>(R.id.textViewTypeOfConsultSchedule)
                val textViewHourSchedule = this.findViewById<TextView>(R.id.textViewHourSchedule)
                val textViewFloorSchedule = this.findViewById<TextView>(R.id.textViewFloorSchedule)
                val textViewCabinetSchedule = this.findViewById<TextView>(R.id.textViewCabinetSchedule)

                textViewDoctorNameSchedule.text = listSchedule[position].doctorName
                textViewHourSchedule.text = listSchedule[position].hour
                //textViewLocationSchedule.text = listSchedule[position].local
                textViewTypeOfConsultSchedule.text = listSchedule[position].typeOfConsult
                textViewFloorSchedule.text = "Piso " + listSchedule[position].floor
                textViewCabinetSchedule.text = "Gabinete " + listSchedule[position].cabinet
                val pavilion = listSchedule[position].pavilion.toString()


                textViewLocationSchedule.setOnClickListener {

                    val uri = String.format(Locale.UK, "geo:0,0?q=Centro+Hospitalar+de+Vila+Nova+de+Gaia+Unidade+1")
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                    startActivity(intent)
                }

                textViewDoctorNameSchedule.setOnClickListener {

                    val intent = Intent(
                            this@ScheduleActivity,
                            ChatMessagesActivity::class.java)
                    intent.putExtra(ContactsActivity.USER_KEY, toUser)
                    startActivity(intent)
                }

                this.setOnClickListener {
                    myDialog = Dialog(this@ScheduleActivity, R.style.AnimateDialog)
                    myDialog.setContentView(R.layout.popwindow_schedule_detail)
                    myDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

                    myDialog.textViewTypeOfConsultDetailV2.text = textViewTypeOfConsultSchedule.text
                    myDialog.textViewDoctorNameDetailV2.text = textViewDoctorNameSchedule.text
                    myDialog.textViewPavilionDetailV2.text = pavilion
                    myDialog.textViewFloorDetailV2.text = textViewFloorSchedule.text
                    myDialog.textViewDoorDetailV2.text = textViewCabinetSchedule.text
                    myDialog.textViewHourDetailV2.text = textViewHourSchedule.text

                    myDialog.show()
                }
            }
        }

        override fun getItemCount(): Int {
            return listSchedule.size
        }
    }

    private fun getUserMedic(medicID: String) {

        db.collection("users_medic").document(medicID)
            .get()
            .addOnSuccessListener { doc ->

                if (doc != null) {

                    toUser = DoctorsItem.fromHash(doc.data as HashMap<String, Any?>)
                }
            }
    }
}