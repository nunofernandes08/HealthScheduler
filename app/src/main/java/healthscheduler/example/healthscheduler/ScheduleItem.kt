package healthscheduler.example.healthscheduler

import java.util.*
import kotlin.collections.HashMap

class ScheduleItem {
    var doctorname      :String?    = null
    var local           :String?    = null
    var typeofconsult   :String?    = null

    constructor(
        doctorname      :String?,
        local           :String?,
        typeofconsult   :String?
    ) {
        this.doctorname         = doctorname
        this.local              = local
        this.typeofconsult      = typeofconsult
    }
}