package healthscheduler.example.healthscheduler.models

import kotlin.collections.HashMap

class ScheduleItem {
    var date            : String? = null
    var doctorName      : String? = null
    var hour            : String? = null
    var local           : String? = null
    var floor           : String? = null
    var pavilion        : String? = null
    var cabinet         : String? = null
    var typeOfConsult   : String? = null


    constructor(
            date            : String?,
            doctorName      : String?,
            hour            : String?,
            local           : String?,
            floor           : String?,
            pavilion        : String?,
            cabinet         : String?,
            typeOfConsult   : String?
    ) {
        this.date           = date
        this.doctorName     = doctorName
        this.hour           = hour
        this.local          = local
        this.floor          = floor
        this.pavilion       = pavilion
        this.cabinet        = cabinet
        this.typeOfConsult  = typeOfConsult
    }
}