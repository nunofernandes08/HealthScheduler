package healthscheduler.example.healthscheduler

import java.text.SimpleDateFormat
import java.util.*

fun dateToString(date : Date) : String{
    val formatter = SimpleDateFormat("dd MM yyyy hh:mm", Locale.getDefault())
    return formatter.format(date)
}

fun stringToDate(dateStr: String) : Date {
    val formatter = SimpleDateFormat("dd MM yyyy hh:mm", Locale.getDefault())
    val date = formatter.parse(dateStr)
    return date ?: Date()
}

//Nao e assim
fun stringToHour(strHour: String) : Date {
    val formatter = SimpleDateFormat("hh:mm", Locale.getDefault())
    val strHour = formatter.parse(strHour)
    return strHour ?: Date()
}