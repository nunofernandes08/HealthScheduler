package healthscheduler.example.healthscheduler.models

class UtilizadoresItem {
    var nomeUtilizador : String? = null
    var numeroTelemovelOuEmail : String? = null
    var moradaUtilizador : String? = null
    var imagemPath : String? = null
    var userID : String? = null

    constructor(nomeUtilizador : String?, numeroTelemovelOuEmail : String?, moradaUtilizador : String?, imagemPath : String?, userID: String?){
        this.nomeUtilizador = nomeUtilizador
        this.numeroTelemovelOuEmail = numeroTelemovelOuEmail
        this.moradaUtilizador = moradaUtilizador
        this.imagemPath = imagemPath
        this.userID = userID
    }

    fun toHashMap() : HashMap<String, Any?>{
        val hashMap = HashMap<String, Any?>()
        hashMap["nomeUtilizador"] = nomeUtilizador
        hashMap["numeroTelemovelOuEmail"] = numeroTelemovelOuEmail
        hashMap["moradaUtilizador"] = moradaUtilizador
        hashMap["imagemPath"] = imagemPath
        hashMap["userID"] = userID

        return hashMap
    }

    companion object{
        fun fromHash(hashMap:  HashMap<String, Any?>) : UtilizadoresItem {
            val item = UtilizadoresItem(
                hashMap["nomeUtilizador"].toString(),
                hashMap["numeroTelemovelOuEmail"].toString(),
                hashMap["moradaUtilizador"].toString(),
                hashMap["imagemPath"].toString(),
                hashMap["userID"].toString()
            )
            return item
        }
    }
}