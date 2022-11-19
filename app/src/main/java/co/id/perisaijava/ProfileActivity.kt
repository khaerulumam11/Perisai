package co.id.perisaijava

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import co.id.perisaijava.databinding.ActivityProfileBinding
import co.id.perisaijava.util.Server
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnCompleteListener
import org.json.JSONException
import org.json.JSONObject
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class ProfileActivity : AppCompatActivity(), OnMapReadyCallback {
    private var maps: GoogleMap? = null
    private lateinit var activityProfileBinding: ActivityProfileBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    private var geoCoder: Geocoder?=null
    private var wayLatitude = 0.0
    private  var wayLongitude= 0.0

    private val isContinue = false
    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback? = null
    private val LOCATION_REQUEST: Int = 1340
    var idUser=""
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityProfileBinding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(activityProfileBinding.root)
        geoCoder = Geocoder(this, Locale.getDefault())
        val mapFragment = (supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?)!!
        mapFragment!!.getMapAsync(this)
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
        loadDataProfile()

        activityProfileBinding.btnLogout.setOnClickListener {
            signOut()
        }

        activityProfileBinding.btnEditProfile.setOnClickListener {
            activityProfileBinding.lyBtnProfil.visibility = View.GONE
            activityProfileBinding.etPhone.visibility = View.GONE
            activityProfileBinding.lyEditProfil.visibility = View.VISIBLE
            activityProfileBinding.etPhoneEdit.visibility = View.VISIBLE
        }

        activityProfileBinding.btnCancel.setOnClickListener {
            getDataUser(idUser)
            activityProfileBinding.lyBtnProfil.visibility = View.VISIBLE
            activityProfileBinding.etPhone.visibility = View.VISIBLE
            activityProfileBinding.lyEditProfil.visibility = View.GONE
            activityProfileBinding.etPhoneEdit.visibility = View.GONE
        }

        activityProfileBinding.btnSubmit.setOnClickListener {
            updatePhone()
        }

        activityProfileBinding.homeBtn.setOnClickListener {
            var pindah = Intent(this@ProfileActivity, MainActivity::class.java)
            startActivity(pindah)
        }
        activityProfileBinding.profileBtn.setOnClickListener {
            var pindah = Intent(this@ProfileActivity, ProfileActivity::class.java)
            startActivity(pindah)
        }
    }

    private fun updatePhone() {
        AndroidNetworking.post(Server.ENDPOINT_UPDATE_PHONE + idUser)
            .addBodyParameter("telp", activityProfileBinding.etPhoneEdit.text.toString())
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
//                        setIsLoading(false);
                    try {
                        if (response.getString("message").equals("success", ignoreCase = true)) {
                            println("Response $response")
                            getDataUser(idUser)
                            activityProfileBinding.lyBtnProfil.visibility = View.VISIBLE
                            activityProfileBinding.etPhone.visibility = View.VISIBLE
                            activityProfileBinding.lyEditProfil.visibility = View.GONE
                            activityProfileBinding.etPhoneEdit.visibility = View.GONE
                        } else {
                            Toast.makeText(
                                this@ProfileActivity,
                                "Update Phone Failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }

                override fun onError(anError: ANError) {
                    println("Erorr " + anError.message)
                }
            })
    }

    private fun isUserSignedIn(): Boolean {

        val account = GoogleSignIn.getLastSignedInAccount(this)
        return account != null

    }

    private fun signOut() {
        if (isUserSignedIn()) {
            getGoogleSinginClient().signOut()
                .addOnCompleteListener(this, OnCompleteListener<Void?> {
                    if (it.isSuccessful){
                        Toast.makeText(this@ProfileActivity, " Signed out ", Toast.LENGTH_SHORT).show()
                        var pindah = Intent(this@ProfileActivity,LoginActivity::class.java)
                        startActivity(pindah)
                        finish()
                    } else {
                        Toast.makeText(this@ProfileActivity, " Error ", Toast.LENGTH_SHORT).show()
                    }
                })
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
    private fun loadLocationNow() {
        if (ActivityCompat.checkSelfPermission(
                this@ProfileActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this@ProfileActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@ProfileActivity,
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
                    .addOnSuccessListener(this@ProfileActivity) { location ->
                        if (location != null) {
                            wayLatitude = location.getLatitude()
                            wayLongitude = location.getLongitude()
                            println("Lat :" + location!!.latitude)
                            println("Long :" + location.longitude)
                            try {
                                var addresses =
                                    geoCoder!!.getFromLocation(wayLatitude, wayLongitude, 1);
                                if (addresses.isEmpty()) {
                                    activityProfileBinding.txtAlamatKota.setText("Waiting for Location");
                                    activityProfileBinding.txtAlamatKotaLengkap.setText("Waiting for Location");
                                } else {
                                    activityProfileBinding.txtAlamatKota.setText(
                                        addresses.get(0)
                                            .getLocality()
                                    );
                                    activityProfileBinding.txtAlamatKotaLengkap.setText(
                                        addresses.get(0)
                                            .getAddressLine(0)
                                    );
                                }
                            } catch (e: java.lang.Exception) {
                                println("Catch " + e.message)
                            }
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
    override fun onMapReady(googleMap: GoogleMap?) {
        maps = googleMap
        // Position the map's camera near Alice Springs in the center of Australia,
        // and set the zoom factor so most of Australia shows on the screen.
        googleMap!!.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(wayLatitude,wayLongitude),
                13f
            )
        )

    }
    @RequiresApi(Build.VERSION_CODES.M)
    private fun canAccessLocation(): Boolean {
        return hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    }
    @RequiresApi(Build.VERSION_CODES.M)
    private fun hasPermission(perm: String): Boolean {
        return PackageManager.PERMISSION_GRANTED == checkSelfPermission(perm)
    }
    private fun loadDataProfile() {
        val dateFormat: DateFormat = SimpleDateFormat("HH")
        val date = Date()
        var waktuSaatIni = dateFormat.format(date)
        println("Waktu Saat ini "+dateFormat.format(date))
        if (waktuSaatIni.toInt() in 5..11){
            activityProfileBinding.txtSelamat.text = "Selamat Pagi"
        } else if (waktuSaatIni.toInt() in 11..14){
            activityProfileBinding.txtSelamat.text = "Selamat Siang"
        }else if (waktuSaatIni.toInt() in 15..17){
            activityProfileBinding.txtSelamat.text = "Selamat Sore"
        } else {
            activityProfileBinding.txtSelamat.text = "Selamat Malam"
        }
        val acct = GoogleSignIn.getLastSignedInAccount(this)
        if (acct != null) {
            val personName = acct.displayName
            val personGivenName = acct.givenName
            val personFamilyName = acct.familyName
            val personEmail = acct.email
            val personId = acct.id
            val personPhoto: Uri? = acct.photoUrl
            idUser = acct.id.toString()

            getDataUser(idUser)
            activityProfileBinding.txtName.text = "Halo, $personName"
            activityProfileBinding.avatar.setImageURI(personPhoto)
            activityProfileBinding.etName.setText( personName.toString())
            activityProfileBinding.etEmail.setText(personEmail.toString())
        }
    }

    private fun getDataUser(id:String?) {
        AndroidNetworking.post(Server.ENDPOINT_GET_DETAIL_USER + id)
            .setPriority(Priority.HIGH)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
//                        setIsLoading(false);
                    println("Response $response")
                    try {
                        activityProfileBinding.etPhone.setText(response.getString("telepon"))
                        activityProfileBinding.etPhoneEdit.setText(response.getString("telepon"))
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }

                override fun onError(anError: ANError) {
                    println("Erorr Login")
                }
            })
    }
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
}