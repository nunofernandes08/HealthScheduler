package healthscheduler.example.healthscheduler.models


import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class MessageItem (
    val message : String? = null,
    val fromId : String? = null,
    val toId : String? = null,
    val timeStamp : Long? = null,
    val messageType : String? = null): Parcelable {

    constructor() : this("", "", "", -1, "")

    fun toHashMap() : HashMap<String, Any?>{
        val hashMap = HashMap<String, Any?>()
        hashMap["message"] = message
        hashMap["fromId"] = fromId
        hashMap["toId"] = toId
        hashMap["timeStamp"] = timeStamp
        hashMap["messageType"] = messageType

        return hashMap
    }

    companion object{
        fun fromHash(hashMap:  HashMap<String, Any?>) : MessageItem {
            val item = MessageItem(
                hashMap["message"].toString(),
                hashMap["fromId"].toString(),
                hashMap["toId"].toString(),
                hashMap["timeStamp"] as Long?,
                hashMap["messageType"].toString()
            )
            return item
        }
    }
}