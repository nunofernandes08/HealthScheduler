package healthscheduler.example.healthscheduler.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.android.parcel.Parcelize

@Parcelize
class UsersItem (
    var username            : String? = null,
    var phoneNumberEmail    : String? = null,
    var address             : String? = null,
    var imagePath           : String? = null,
    var userID              : String? = null,
    var phoneNumber         : String? = null,
    var birthday            : String? = null,
    var hospitalNumber      : String? = null,
    var healthNumber        : String?= null): Parcelable {

    constructor() : this("", "", "", "", "", "", "", "", "")

    /*constructor(username : String?, phoneNumberEmail : String?, address : String?, imagePath : String?, userID: String?){
        this.username = username
        this.phoneNumberEmail = phoneNumberEmail
        this.address = address
        this.imagePath = imagePath
        this.userID = userID
    }*/

    fun toHashMap() : HashMap<String, Any?>{
        val hashMap = HashMap<String, Any?>()
        hashMap["username"]         = username
        hashMap["phoneNumberEmail"] = phoneNumberEmail
        hashMap["address"]          = address
        hashMap["imagePath"]        = imagePath
        hashMap["userID"]           = userID
        hashMap["phoneNumber"]      = phoneNumber
        hashMap["birthday"]         = birthday
        hashMap["hospitalNumber"]   = hospitalNumber
        hashMap["healthNumber"]     = healthNumber

        return hashMap
    }

    companion object{
        fun fromHash(hashMap:  HashMap<String, Any?>) : UsersItem {
            val item = UsersItem(
                hashMap["username"].toString(),
                hashMap["phoneNumberEmail"].toString(),
                hashMap["address"].toString(),
                hashMap["imagePath"].toString(),
                hashMap["userID"].toString(),
                hashMap["phoneNumber"].toString(),
                hashMap["birthday"].toString(),
                hashMap["hospitalNumber"].toString(),
                hashMap["healthNumber"].toString()
            )
            return item
        }
    }
}