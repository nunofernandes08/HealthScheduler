package healthscheduler.example.healthscheduler.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import healthscheduler.example.healthscheduler.activity.HomeActivity
import healthscheduler.example.healthscheduler.R
import kotlinx.coroutines.*

class Splash : AppCompatActivity() {

    private val activityScope   = CoroutineScope(Dispatchers.Main)
    private val auth            = Firebase.auth
    private val currentUser     = auth.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val rotation = AnimationUtils.loadAnimation(this, R.anim.rotation)
        val logo : ImageView = findViewById(R.id.imageViewLogo_Splash)
        logo.startAnimation(rotation)

        //esconde barra de açoes do android
        window.decorView.apply {
            systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
        }

        activityScope.launch {
            delay(2000)

            currentUser?.let {
                val intent = Intent(this@Splash, HomeActivity::class.java)
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