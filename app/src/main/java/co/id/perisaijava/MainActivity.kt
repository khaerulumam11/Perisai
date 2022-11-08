package co.id.perisaijava

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import co.id.perisaijava.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.location.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
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
    private var geoCoder: Geocoder?=null
    private var dialogLayout: BottomSheetDialog?=null
    private lateinit var activityMainActivity: ActivityMainBinding
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainActivity = ActivityMainBinding.inflate(layoutInflater)
        geoCoder = Geocoder(this, Locale.getDefault())
        setContentView(activityMainActivity.root)
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
        showDialog()
        if (canAccessLocation()) {
            loadLocationNow()
        }
        else {
            requestPermissions(LOCATION_PERMS, LOCATION_REQUEST)
        }
        loadDataProfile()
        activityMainActivity.cardSafeHouse.setOnClickListener {
            var pindah = Intent(this@MainActivity,MapsActivity::class.java)
            startActivity(pindah)
        }

        activityMainActivity.btnPanic.setOnClickListener {
            var pindah = Intent(this@MainActivity,AlarmInfoActivity::class.java)
            startActivity(pindah)
        }

        activityMainActivity.btnInfoPanic.setOnClickListener {
            dialogLayout!!.show()
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

    fun showDialog() {

        dialogLayout= BottomSheetDialog(this)
        dialogLayout!!.setContentView(R.layout.dialog_panic_button_info)
        val btnClose= dialogLayout!!.findViewById<ImageView>(R.id.btnClose)
        btnClose?.setOnClickListener {
            dialogLayout!!.hide()
        }
    }
    private fun loadLocationNow() {
        if (ActivityCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@MainActivity,
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
                .addOnSuccessListener(this@MainActivity) { location ->
                    if (location != null) {
                        wayLatitude = location.getLatitude()
                        wayLongitude = location.getLongitude()
                        println("Lat :" + location!!.latitude)
                        println("Long :" + location.longitude)
                        try {
            var addresses =
                geoCoder!!.getFromLocation(wayLatitude, wayLongitude, 1);
            if (addresses.isEmpty()) {
                activityMainActivity.txtLocation.setText("Waiting for Location");
            } else {
                activityMainActivity.txtLocation.setText(
                   addresses.get(0)
                        .getLocality() + ", " + addresses.get(0)
                        .getAdminArea()
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
            activityMainActivity.txtSelamat.text = "Selamat Pagi"
        } else if (waktuSaatIni.toInt() in 11..14){
            activityMainActivity.txtSelamat.text = "Selamat Siang"
        }else if (waktuSaatIni.toInt() in 15..17){
            activityMainActivity.txtSelamat.text = "Selamat Sore"
        } else {
            activityMainActivity.txtSelamat.text = "Selamat Malam"
        }
        val acct = GoogleSignIn.getLastSignedInAccount(this)
        if (acct != null) {
            val personName = acct.displayName
            val personGivenName = acct.givenName
            val personFamilyName = acct.familyName
            val personEmail = acct.email
            val personId = acct.id
            val personPhoto: Uri? = acct.photoUrl

            activityMainActivity.txtName.text = "Halo, $personName"
            activityMainActivity.avatar.setImageURI(personPhoto)
        }
    }
}