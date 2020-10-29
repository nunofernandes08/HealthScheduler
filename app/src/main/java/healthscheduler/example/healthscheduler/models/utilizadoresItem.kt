package healthscheduler.example.healthscheduler.models

class utilizadoresItem {
    var nomeUtilizador : String? = null
    var numeroTelemovel : Int? = null
    var moradaUtilizador : String? = null

    constructor(nomeUtilizador : String?, numeroTelemovel : Int?, moradaUtilizador : String?){
        this.nomeUtilizador = nomeUtilizador
        this.numeroTelemovel = numeroTelemovel
        this.moradaUtilizador = moradaUtilizador
    }

    fun toHasMap() : HashMap<String, Any?>{
        val hashMap = HashMap<String, Any?>()
        hashMap["nomeUtilizador"] = nomeUtilizador
        hashMap["numeroTelemovel"] = numeroTelemovel
        hashMap["moradaUtilizador"] = moradaUtilizador

        return hashMap
    }

    companion object{
        fun formHash(hashMap:  HashMap<String, Any?>) : utilizadoresItem {
            val item = utilizadoresItem(
                hashMap["nomeUtilizador"].toString(),
                hashMap["numeroTelemovel"].toString().toInt(),
                hashMap["moradaUtilizador"].toString()
            )
            return item
        }
    }


}