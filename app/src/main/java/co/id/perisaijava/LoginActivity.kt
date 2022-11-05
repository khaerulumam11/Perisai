package co.id.perisaijava

import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import co.id.perisaijava.databinding.ActivityLoginBinding
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    var conMgr: ConnectivityManager? = null
    var id: String? = null
    var pDialog: ProgressDialog? = null
    var sharedpreferences: SharedPreferences? = null
    var session = false
    val my_shared_preferences = "my_shared_preferences"
    val session_status = "session_status"
    private lateinit var activityLoginBinding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityLoginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(activityLoginBinding.root)
        initListeners()
        conMgr = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        {
            if (conMgr!!.activeNetworkInfo != null && conMgr!!.activeNetworkInfo!!.isAvailable
                && conMgr!!.activeNetworkInfo!!.isConnected
            ) {

            } else {
                Toast.makeText(
                    applicationContext, "No Internet Connection",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        // Cek session login jika TRUE maka langsung buka MainActivity

        // Cek session login jika TRUE maka langsung buka MainActivity
        sharedpreferences = getSharedPreferences(my_shared_preferences, MODE_PRIVATE)
        session = sharedpreferences!!.getBoolean(session_status, false)
        id = sharedpreferences!!.getString("id", null)

        if (isUserSignedIn()){
            var pindah = Intent(this@LoginActivity,MainActivity::class.java)
            startActivity(pindah)
            finish()
        }

    }

    private fun initListeners() {

        activityLoginBinding.signInButton.setOnClickListener {
            loginGoogle()
        }

//        activityMainBinding.tvSignOut.setOnClickListener {
//            signout()
//        }

    }

    private fun loginGoogle() {
        val signInIntent = getGoogleSinginClient().signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    //region User Google Sign-in and sign-out Code

    fun getGoogleSinginClient() : GoogleSignInClient {
        /**
         * Configure sign-in to request the user's ID, email address, and basic
         * profile. ID and basic profile are included in DEFAULT_SIGN_IN.
         */
        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .build()


        /**
         * Build a GoogleSignInClient with the options specified by gso.
         */
        return GoogleSignIn.getClient(this, gso);
    }

    private fun isUserSignedIn(): Boolean {

        val account = GoogleSignIn.getLastSignedInAccount(this)
        return account != null

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            var pindah = Intent(this@LoginActivity,MainActivity::class.java)
            startActivity(pindah)
            finish()
//            handleSignData(data)

        }
    }

    private fun handleSignData(data: Intent?) {
        // The Task returned from this call is always completed, no need to attach
        // a listener.
        GoogleSignIn.getSignedInAccountFromIntent(data)
            .addOnCompleteListener {
                "isSuccessful ${it.isSuccessful}".print()
                if (it.isSuccessful){
                    // user successfully logged-in
                    "account ${it.result?.account}".print()
                    "displayName ${it.result?.displayName}".print()
                    "Email ${it.result?.email}".print()
                } else {
                    // authentication failed
                    "exception ${it.exception}".print()
                }
            }

    }

    companion object{
        const val RC_SIGN_IN = 0
        const val TAG_KOTLIN = "TAG_KOTLIN"
    }
    fun Any.print(){
        Log.v(TAG_KOTLIN, " $this")
    }

    private fun showDialog() {
        if (!pDialog!!.isShowing) pDialog!!.show()
    }

    private fun hideDialog() {
        if (pDialog!!.isShowing) pDialog!!.dismiss()
    }
}