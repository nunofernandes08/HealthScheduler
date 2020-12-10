package healthscheduler.example.healthscheduler.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

class DoctorsItem {
    var username : String? = null
    var phoneNumberEmail : String? = null
    var address : String? = null
    var imagePath : String? = null
    var medicID : String? = null


    constructor(username : String?, phoneNumberEmail : String?, address : String?, imagePath : String?, medicID: String?){
        this.username = username
        this.phoneNumberEmail = phoneNumberEmail
        this.address = address
        this.imagePath = imagePath
        this.medicID = medicID
    }

    fun toHashMap() : HashMap<String, Any?>{
        val hashMap = HashMap<String, Any?>()
        hashMap["username"] = username
        hashMap["phoneNumberEmail"] = phoneNumberEmail
        hashMap["address"] = address
        hashMap["imagePath"] = imagePath
        hashMap["medicID"] = medicID

        return hashMap
    }

    companion object{
        fun fromHash(hashMap:  HashMap<String, Any?>) : DoctorsItem {
            val item = DoctorsItem(
                    hashMap["username"].toString(),
                    hashMap["phoneNumberEmail"].toString(),
                    hashMap["address"].toString(),
                    hashMap["imagePath"].toString(),
                    hashMap["medicID"].toString()
            )
            return item
        }
    }
}