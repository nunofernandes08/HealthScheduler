package healthscheduler.example.healthscheduler.models

class UtilizadoresItem {
    var nomeUtilizador : String? = null
    var numeroTelemovelOuEmail : String? = null
    var moradaUtilizador : String? = null

    constructor(nomeUtilizador : String?, numeroTelemovelOuEmail : String?, moradaUtilizador : String?){
        this.nomeUtilizador = nomeUtilizador
        this.numeroTelemovelOuEmail = numeroTelemovelOuEmail
        this.moradaUtilizador = moradaUtilizador
    }

    fun toHashMap() : HashMap<String, Any?>{
        val hashMap = HashMap<String, Any?>()
        hashMap["nomeUtilizador"] = nomeUtilizador
        hashMap["numeroTelemovelOuEmail"] = numeroTelemovelOuEmail
        hashMap["moradaUtilizador"] = moradaUtilizador

        return hashMap
    }

    companion object{
        fun fromHash(hashMap:  HashMap<String, Any?>) : UtilizadoresItem {
            val item = UtilizadoresItem(
                hashMap["nomeUtilizador"].toString(),
                hashMap["numeroTelemovelOuEmail"].toString(),
                hashMap["moradaUtilizador"].toString()
            )
            return item
        }
    }


}