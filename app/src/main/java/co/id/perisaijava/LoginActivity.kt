package co.id.perisaijava

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import co.id.perisaijava.databinding.ActivityLoginBinding
import co.id.perisaijava.util.Server
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.location.*
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    private var wayLatitude = 0.0
    private  var wayLongitude= 0.0

    private val isContinue = false
    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback? = null
    private val LOCATION_REQUEST: Int = 1340
    var conMgr: ConnectivityManager? = null
    var id: String? = null
    var pDialog: ProgressDialog? = null
    var sharedpreferences: SharedPreferences? = null
    var session = false
    val my_shared_preferences = "my_shared_preferences"
    val session_status = "session_status"
    private lateinit var activityLoginBinding: ActivityLoginBinding
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityLoginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(activityLoginBinding.root)
        initListeners()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.create()
        locationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest!!.interval = 20 * 1000
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (locationResult == null) {
                    return
                }
                for (location in locationResult.locations) {
                    if (location != null) {
                        wayLatitude = location.latitude
                        wayLongitude = location.longitude
                    }
                }
            }
        }
        if (canAccessLocation()) {
            loadLocationNow()
        }
        else {
            requestPermissions(LOCATION_PERMS, LOCATION_REQUEST)
        }
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

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1000 -> if (canAccessLocation()) {
                if (grantResults.isNotEmpty()
                    && grantResults[0] === PackageManager.PERMISSION_GRANTED
                ) {
                    if (isContinue) {
                        loadLocationNow()
                    } else {
                        loadLocationNow()
                    }
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            } else {
                requestPermissions(LOCATION_PERMS, LOCATION_REQUEST)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadLocationNow()
    }

    private fun loadLocationNow() {
        if (ActivityCompat.checkSelfPermission(
                this@LoginActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this@LoginActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@LoginActivity,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                1000
            )
        } else {
            if (isContinue) {
                fusedLocationClient.requestLocationUpdates(locationRequest!!, locationCallback!!, null)
            } else {
                fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this@LoginActivity) { location ->
                        if (location != null) {
                            wayLatitude = location.getLatitude()
                            wayLongitude = location.getLongitude()
                            println("Lat :" + location!!.latitude)
                            println("Long :" + location.longitude)
                        } else {
                            fusedLocationClient.requestLocationUpdates(
                                locationRequest!!,
                                locationCallback!!,
                                null
                            )
                        }
                    }
            }
        }


//
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

    @RequiresApi(Build.VERSION_CODES.M)
    private fun canAccessLocation(): Boolean {
        return hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    }
    @RequiresApi(Build.VERSION_CODES.M)
    private fun hasPermission(perm: String): Boolean {
        return PackageManager.PERMISSION_GRANTED == checkSelfPermission(perm)
    }

    private fun isUserSignedIn(): Boolean {

        val account = GoogleSignIn.getLastSignedInAccount(this)
        return account != null

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            checkUser()

//            handleSignData(data)

        }
    }

    private fun checkUser() {
        val acct = GoogleSignIn.getLastSignedInAccount(this)
        if (acct != null) {
            val personName = acct.displayName
            val personGivenName = acct.givenName
            val personFamilyName = acct.familyName
            val personEmail = acct.email
            val personId = acct.id
            val personPhoto: Uri? = acct.photoUrl

            AndroidNetworking.post(Server.ENDPOINT_GET_DETAIL_USER + personId)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject) {
//                        setIsLoading(false);
                        println("Response $response")
                        try {
                            if (response.getString("message").equals("Success")){
                                var pindah = Intent(this@LoginActivity,MainActivity::class.java)
                                startActivity(pindah)
                                finish()
                            } else {
                                createDataUser()
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }

                    override fun onError(anError: ANError) {
                        println("Erorr Login")
                        hideDialog()
                    }
                })
        }
    }

    private fun createDataUser() {
        val acct = GoogleSignIn.getLastSignedInAccount(this)
        if (acct != null) {
            val personName = acct.displayName
            val personGivenName = acct.givenName
            val personFamilyName = acct.familyName
            val personEmail = acct.email
            val personId = acct.id
            val personPhoto: Uri? = acct.photoUrl

            AndroidNetworking.post(Server.ENDPOINT_REGISTER)
                .addBodyParameter("id", personId)
                .addBodyParameter("email", personEmail)
                .addBodyParameter("name", personName)
                .addBodyParameter("lat", wayLatitude.toString())
                .addBodyParameter("lng", wayLongitude.toString())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject) {
//                        setIsLoading(false);
                        println("Response $response")
                        Toast.makeText(
                            this@LoginActivity,
                            "Register Success",
                            Toast.LENGTH_SHORT
                        ).show()
                        var pindah = Intent(this@LoginActivity,MainActivity::class.java)
                        startActivity(pindah)
                        finish()
                    }

                    override fun onError(anError: ANError) {
                        println("Erorr " + anError.message)
                        hideDialog()
                    }
                })
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