package healthscheduler.example.healthscheduler

import java.util.*
import kotlin.collections.HashMap

class ScheduleItem {
    var doctorName: String? = null
    var local: String? = null
    var typeOfConsult: String? = null

    constructor(
            doctorName: String?,
            local: String?,
            typeOfConsult: String?
    ) {
        this.doctorName = doctorName
        this.local = local
        this.typeOfConsult = typeOfConsult
    }
}