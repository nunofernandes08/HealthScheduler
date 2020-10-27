package healthscheduler.example.healthscheduler

import android.content.Context
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import org.w3c.dom.Text
import java.io.ByteArrayInputStream
import java.util.ArrayList

class Schedule : AppCompatActivity() {

    var listschedule: MutableList<ScheduleItem> = ArrayList()
    var scheduleadapter: Schedule.ScheduleAdapter? = null

    val db = FirebaseFirestore.getInstance()

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)

        val listViewSchedule = findViewById<ListView>(R.id.listViewSchedule)

        // Initialize Firebase Auth
        auth = Firebase.auth
        val currentUser = auth.currentUser

        scheduleadapter = ScheduleAdapter()
        listViewSchedule.adapter = scheduleadapter

        listschedule.clear()

        db.collection("consultas").addSnapshotListener { snapshot, error ->
            listschedule.clear()
            for (document in snapshot!!) {
                //O Log.d é só para aparecer no logcat
                Log.d("exist", "${document.id} => ${document.data}")
                listschedule.add(ScheduleItem(
                    document.data.getValue("doctorname").toString(),
                    document.data.getValue("local").toString(),
                    document.data.getValue("typeofconsult").toString()))
            }
            scheduleadapter?.notifyDataSetChanged()
        }
    }

    inner class ScheduleAdapter : BaseAdapter() {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val rowView = layoutInflater.inflate(R.layout.row_schedule, parent, false)

            val textViewDoctorNameSchedule      = rowView.findViewById<TextView>(R.id.textViewDoctorNameSchedule)
            val textViewLocationSchedule            = rowView.findViewById<TextView>(R.id.textViewLocationSchedule)
            val textViewTypeOfConsultSchedule   = rowView.findViewById<TextView>(R.id.textViewTypeOfConsultSchedule)

            textViewDoctorNameSchedule.text     = listschedule[position].doctorname
            textViewLocationSchedule.text       = listschedule[position].local
            textViewTypeOfConsultSchedule.text  = listschedule[position].typeofconsult

            return rowView
        }

        override fun getItem(position: Int): Any {
            return listschedule[position]
        }

        override fun getItemId(position: Int): Long {
            return 0
        }

        override fun getCount(): Int {
            return listschedule.size
        }
    }
}