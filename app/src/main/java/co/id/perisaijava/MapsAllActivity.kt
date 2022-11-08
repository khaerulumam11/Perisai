package co.id.perisaijava

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import co.id.perisaijava.databinding.ActivityMapsAllBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng

class MapsAllActivity : AppCompatActivity() , OnMapReadyCallback {
    private var maps: GoogleMap? = null
    private lateinit var activityMapsAllBinding: ActivityMapsAllBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMapsAllBinding = ActivityMapsAllBinding.inflate(layoutInflater)
        setContentView(activityMapsAllBinding.root)
        val mapFragment = (supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?)!!
        mapFragment!!.getMapAsync(this)

        activityMapsAllBinding.toolbar.setTitle(resources.getString(R.string.safehosue))
        activityMapsAllBinding.toolbar.setNavigationIcon(R.drawable.ic_baseline_chevron_left_grey_24)
        activityMapsAllBinding.toolbar.setNavigationOnClickListener {
            finish()
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
                15f
            )
        )

    }
}