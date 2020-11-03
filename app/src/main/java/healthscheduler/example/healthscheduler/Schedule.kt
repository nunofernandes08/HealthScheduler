package healthscheduler.example.healthscheduler

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import healthscheduler.example.healthscheduler.models.ScheduleItem
import java.util.ArrayList

class Schedule : AppCompatActivity() {

    var listSchedule: MutableList<ScheduleItem> = ArrayList()
    var scheduleAdapter: Schedule.ScheduleAdapter? = null

    private val db = FirebaseFirestore.getInstance()

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)

        val listViewSchedule = findViewById<ListView>(R.id.listViewSchedule)

        // Initialize Firebase Auth
        auth = Firebase.auth
        val currentUser = auth.currentUser

        scheduleAdapter = ScheduleAdapter()
        listViewSchedule.adapter = scheduleAdapter

        listSchedule.clear()

        db.collection("consultas")
            .whereEqualTo("userID", currentUser!!.uid)
            .addSnapshotListener { snapshot, error ->
            listSchedule.clear()
            for (document in snapshot!!) {
                //O Log.d é só para aparecer no logcat
                Log.d("exist", "${document.id} => ${document.data}")
                listSchedule.add(ScheduleItem(
                        document.data.getValue("date").toString(),
                        document.data.getValue("doctorName").toString(),
                        document.data.getValue("hour").toString(),
                        document.data.getValue("local").toString(),
                        document.data.getValue("typeOfConsult").toString()))
            }
            scheduleAdapter?.notifyDataSetChanged()
        }
    }

    inner class ScheduleAdapter : BaseAdapter() {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val rowView = layoutInflater.inflate(R.layout.row_schedule, parent, false)

            val textViewDoctorNameSchedule = rowView.findViewById<TextView>(R.id.textViewDoctorNameSchedule)
            val textViewLocationSchedule = rowView.findViewById<TextView>(R.id.textViewLocationSchedule)
            val textViewTypeOfConsultSchedule = rowView.findViewById<TextView>(R.id.textViewTypeOfConsultSchedule)
            val textViewHourSchedule = rowView.findViewById<TextView>(R.id.textViewHourSchedule)
            val textViewDateSchedule = rowView.findViewById<TextView>(R.id.textViewDateSchedule)

            textViewDateSchedule.text = listSchedule[position].date
            textViewDoctorNameSchedule.text = listSchedule[position].doctorName
            textViewHourSchedule.text = listSchedule[position].hour
            textViewLocationSchedule.text = listSchedule[position].local
            textViewTypeOfConsultSchedule.text = listSchedule[position].typeOfConsult

            return rowView
        }

        override fun getItem(position: Int): Any {
            return listSchedule[position]
        }

        override fun getItemId(position: Int): Long {
            return 0
        }

        override fun getCount(): Int {
            return listSchedule.size
        }
    }
}