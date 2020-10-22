package healthscheduler.example.healthscheduler

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var mGoogleSignInClient : GoogleSignInClient? = null
    //facebook
    private var FBloginButton : LoginButton? = null
    private lateinit var mAuth : FirebaseAuth
    val callbackManager: CallbackManager = CallbackManager.Factory.create()

    companion object {
        val TAG = "MainActivity"
        private const val REQUEST_CODE_SIGN_IN = 9001
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = Firebase.auth
        val currentUser = auth.currentUser

        val buttonContinueWithEmailMain = findViewById<Button>(R.id.buttonContinueWithEmailMain)
        val imageViewRegistarMain = findViewById<ImageView>(R.id.imageViewRegistarMain)
        val buttonGoogle = findViewById<ImageView>(R.id.imageViewGoogleMain)

        buttonContinueWithEmailMain.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        imageViewRegistarMain.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }

        //google

        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, options)

        buttonGoogle.setOnClickListener {
            signIn()
        }

        //facebook
        FacebookSdk.sdkInitialize(this);
        mAuth = FirebaseAuth.getInstance();
        
        FBloginButton = findViewById(R.id.buttonLoginFacebook)

        FBloginButton!!.setReadPermissions("email", "public_profile")
        FBloginButton!!.registerCallback(callbackManager, object :
                FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d(TAG, "facebook:onSuccess:$loginResult")
                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {
                Log.d(TAG, "facebook:onCancel")

            }

            override fun onError(error: FacebookException) {
                Log.d(TAG, "facebook:onError", error)
            }
        })

        val accessToken = AccessToken.getCurrentAccessToken()
        val isLoggedIn = accessToken != null && !accessToken.isExpired
    }

    //facebook
    public override fun onStart() {
        super.onStart()
        val currentUser = mAuth.currentUser
        if(currentUser != null){
            updateUI(currentUser)
        }

    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    val user = mAuth.currentUser
                    updateUI(user)
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(baseContext, "Falha ao entrar na conta facebook!",
                            Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if(user != null){
            LoginManager.getInstance().logInWithReadPermissions(this, listOf("public_profile"));
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
        }else {
            Toast.makeText(baseContext, "Faça o login para continuar!",
                    Toast.LENGTH_SHORT).show()
        }
    }


    //google
    private fun signIn() {
        val signInIntent = mGoogleSignInClient?.signInIntent
        startActivityForResult(signInIntent, REQUEST_CODE_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //google
        if (requestCode == REQUEST_CODE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                Log.d("", "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)

            } catch (e: ApiException) {
                Log.w("", "Google sign in failed", e)
            }
        }
        //facebook
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    //google
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("", "loginSuccess!")
                    val user = auth.currentUser
                    val intent = Intent(this, Home::class.java)
                    startActivity(intent)
                } else {
                    Log.w("", "loginFailed! Info = ", task.exception)

                    Toast.makeText(baseContext, "Falha ao entrar na conta.",
                            Toast.LENGTH_SHORT).show()
                }
            }
    }
}