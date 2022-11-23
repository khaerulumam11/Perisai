package co.id.perisaijava

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import co.id.perisaijava.databinding.ActivityMainBinding
import co.id.perisaijava.databinding.ActivityMapsBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback{
    private var maps: GoogleMap? = null
    private lateinit var activityMapsBinding: ActivityMapsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMapsBinding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(activityMapsBinding.root)

        val mapFragment = (supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?)!!
        mapFragment!!.getMapAsync(this)
        loadDataProfile()

        activityMapsBinding.btnBack.setOnClickListener {
            finish()
        }

        activityMapsBinding.btnCari.setOnClickListener {
            var pindah = Intent(this@MapsActivity,MapsAllActivity::class.java)
            startActivity(pindah)
        }

        activityMapsBinding.homeBtn.setOnClickListener {
            var pindah = Intent(this@MapsActivity, MainActivity::class.java)
            startActivity(pindah)
        }
        activityMapsBinding.profileBtn.setOnClickListener {
            var pindah = Intent(this@MapsActivity, ProfileActivity::class.java)
            startActivity(pindah)
        }
    }

    private fun loadDataProfile() {
        val dateFormat: DateFormat = SimpleDateFormat("HH")
        val date = Date()
        var waktuSaatIni = dateFormat.format(date)
        println("Waktu Saat ini "+dateFormat.format(date))
        if (waktuSaatIni.toInt() in 5..11){
            activityMapsBinding.txtSelamat.text = "Selamat Pagi"
        } else if (waktuSaatIni.toInt() in 11..14){
            activityMapsBinding.txtSelamat.text = "Selamat Siang"
        }else if (waktuSaatIni.toInt() in 15..17){
            activityMapsBinding.txtSelamat.text = "Selamat Sore"
        } else {
            activityMapsBinding.txtSelamat.text = "Selamat Malam"
        }
        val acct = GoogleSignIn.getLastSignedInAccount(this)
        if (acct != null) {
            val personName = acct.displayName
            val personGivenName = acct.givenName
            val personFamilyName = acct.familyName
            val personEmail = acct.email
            val personId = acct.id
            val personPhoto: Uri? = acct.photoUrl

            activityMapsBinding.txtName.text = "Halo, $personName"
            activityMapsBinding.avatar.setImageURI(personPhoto)
        }
    }
    override fun onMapReady(googleMap: GoogleMap?) {
        maps = googleMap
        // Position the map's camera near Alice Springs in the center of Australia,
        // and set the zoom factor so most of Australia shows on the screen.

//        Polygon polygon2 = googleMap.addPolygon(new PolygonOptions()
//                .clickable(true)
//                .add(
//                        new LatLng(-31.673, 128.892),
//                        new LatLng(-31.952, 115.857),
//                        new LatLng(-17.785, 122.258),
//                        new LatLng(-12.4258, 130.7932)));
//        polygon2.setTag("beta");
//        stylePolygon(polygon2);
        // [END_EXCLUDE]

        // Position the map's camera near Alice Springs in the center of Australia,
        // and set the zoom factor so most of Australia shows on the screen.
        googleMap!!.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(-7.3364893,110.5014054),
                13f
            )
        )

    }
}