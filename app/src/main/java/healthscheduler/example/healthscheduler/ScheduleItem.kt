package healthscheduler.example.healthscheduler

import java.util.*
import kotlin.collections.HashMap

class ScheduleItem {
    var nomemedico    : String?   = null
    var tipodeconsulta : String?   = null

    constructor(
        nomemedico: String?,
        description: String?
    ) {
        this.nomemedico       = nomemedico
        this.tipodeconsulta    = tipodeconsulta
    }

    fun toHasMap() : HashMap<String, Any?>{
        val hasMap = HashMap<String, Any?>()
        hasMap["nomemedico"] = nomemedico
        hasMap["tipodeconsulta"] = tipodeconsulta

        return hasMap
    }

    companion object{
        fun formHash(hashMap:  HashMap<String, Any?>) : ScheduleItem{
            val item = ScheduleItem(
                hashMap["nomemedico"].toString(),
                hashMap["tipodeconsulta"].toString()
            )
            return item
        }
    }
}