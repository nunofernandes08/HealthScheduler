package healthscheduler.example.healthscheduler.Login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import healthscheduler.example.healthscheduler.Home
import healthscheduler.example.healthscheduler.R
import kotlinx.coroutines.*

class Splash : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val activityScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


        val rotation = AnimationUtils.loadAnimation(this, R.anim.rotation)
        val logo : ImageView = findViewById(R.id.imageViewLogo_Splash)
        logo.startAnimation(rotation)
        
        window.decorView.apply {
            systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
        }

        auth = Firebase.auth
        val currentUser = auth.currentUser

        /*Handler().postDelayed({
             currentUser?.let {
                 val intent = Intent(this, Home::class.java)
                 startActivity(intent)
                 finish()
             }?:run{
                 val intent = Intent(this, MainActivity::class.java)
                 startActivity(intent)
                 finish()
             }
         },2000) */

        activityScope.launch {
            delay(2000)

            currentUser?.let {
                val intent = Intent(this@Splash, Home::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            } ?: run {
                val intent = Intent(this@Splash, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

}

/* << --------------------------------------- COMENTÁRIOS --------------------------------------- >>

--> A função "finish()" - nas linhas 47 e 51 - encerra a Splash Activity. Ou seja, se formos voltando
    as páginas da app a SplashScreen não aparece de novo.

--> Comentei o "Handler().postDelayed()" - linha 29 ~ 39 - para testar a Coroutine - linha 41 ~53 -.
    no meu tlmv, a aplicação não crashou e a verificação está sendo feita com sucesso.

*/