package healthscheduler.example.healthscheduler.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class DoctorsItem(
    var username            : String? = null,
    var phoneNumberEmail    : String? = null,
    var address             : String? = null,
    var imagePath           : String? = null,
    var medicID             : String? = null): Parcelable{

        constructor() : this("", "", "", "", "")

        fun toHashMap(): HashMap<String, Any?> {
            val hashMap = HashMap<String, Any?>()
            hashMap["username"] = username
            hashMap["phoneNumberEmail"] = phoneNumberEmail
            hashMap["address"] = address
            hashMap["imagePath"] = imagePath
            hashMap["uid"] = medicID

            return hashMap
        }

        companion object {
        fun fromHash(hashMap: HashMap<String, Any?>): DoctorsItem {
            val item = DoctorsItem(
                    hashMap["username"].toString(),
                    hashMap["phoneNumberEmail"].toString(),
                    hashMap["address"].toString(),
                    hashMap["imagePath"].toString(),
                    hashMap["uid"].toString()
            )
            return item
        }
    }
}