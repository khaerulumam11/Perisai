package co.id.perisaijava

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import co.id.perisaijava.databinding.ActivityMapsAllBinding
import co.id.perisaijava.model.SafehouseModel
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.*


class MapsAllActivity : AppCompatActivity() , OnMapReadyCallback, OnMarkerClickListener {
    private var maps: GoogleMap? = null
    private lateinit var activityMapsAllBinding: ActivityMapsAllBinding
    private var dialogLayout: BottomSheetDialog?=null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    private var geoCoder: Geocoder?=null
    private var listLatLng = mutableListOf<SafehouseModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMapsAllBinding = ActivityMapsAllBinding.inflate(layoutInflater)
        setContentView(activityMapsAllBinding.root)
        geoCoder = Geocoder(this, Locale.getDefault())
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val mapFragment = (supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?)!!
        mapFragment!!.getMapAsync(this)
        showDialog()
        listLatLng.add(0,SafehouseModel("KPI Salatiga",LatLng(-7.339747616448067, 110.51723166702338),"Jl. Tritis Sari, Sidorejo Kidul, Kec. Tingkir, Kota Salatiga, Jawa Tengah 50741","https://drive.google.com/file/d/1HowRQtBETpZcbQAOVWRvQaGIw5D-td1o/view?usp=share_link"))
        listLatLng.add(0,SafehouseModel("Polres Salatiga",LatLng(-7.3296281492705875, 110.49978621779775),"Jl. Adisucipto, Kalicacing, Kec. Sidomukti, Kota Salatiga, Jawa Tengah 50711","https://drive.google.com/file/d/1bS2SIwukA0KJ6ajeWZjjT8NNKw5KluBt/view?usp=share_link"))
        listLatLng.add(0,SafehouseModel("DP3APPKB Salatiga",LatLng(-7.346581204384676, 110.49073779284778),"Jl. Hasanudin No.110 B Kota Salatiga, Jawa Tengah, 50721","https://drive.google.com/file/d/152tNuBWhIFr2H0JtGTzLhQQjKqTxG2GI/view?usp=share_link"))
        activityMapsAllBinding.toolbar.setTitle(resources.getString(R.string.safehosue))
        activityMapsAllBinding.toolbar.setNavigationIcon(R.drawable.ic_baseline_chevron_left_grey_24)
        activityMapsAllBinding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    fun showDialog() {
        dialogLayout= BottomSheetDialog(this)
        dialogLayout!!.setContentView(R.layout.dialog_info_safehouse)
    }
    override fun onMapReady(googleMap: GoogleMap?) {
        maps = googleMap
        // Position the map's camera near Alice Springs in the center of Australia,
        // and set the zoom factor so most of Australia shows on the screen.
        for (i in  0 until listLatLng.size) {

            var marker :Marker?=null
            marker = googleMap!!.addMarker(MarkerOptions().position(listLatLng.get(i).latLng!!).title(listLatLng.get(i).name).icon(BitmapFromVector(this@MapsAllActivity,R.drawable.ic_baseline_home_pink_24)))
            marker.tag = i
            // below line is use to add marker to each location of our array list.

            // below lin is use to zoom our camera on map.
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(18.0f))

            // below line is use to move our camera to the specific location.
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(listLatLng.get(i).latLng,15f))
        }

        maps!!.setOnMarkerClickListener(this)

    }

    private fun BitmapFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        // below line is use to generate a drawable.
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)

        // below line is use to set bounds to our vector drawable.
        vectorDrawable!!.setBounds(
            0,
            0,
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        )

        // below line is use to create a bitmap for our
        // drawable which we have added.
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )

        // below line is use to add bitmap in our canvas.
        val canvas = Canvas(bitmap)

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas)

        // after generating our bitmap we are returning our bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        for (i in  0 until listLatLng.size) {
            if (marker!!.tag == i){
                showDetailMap(i)
//                openDialog(i)
            }
        }
        return false
    }

    private fun showDetailMap(i: Int) {
        activityMapsAllBinding.lyDetail.visibility = View.VISIBLE
        activityMapsAllBinding.txtTitle.text = listLatLng.get(i).name
        Glide.with(this@MapsAllActivity).load(listLatLng.get(i).foto).circleCrop().into(activityMapsAllBinding.imgSafehouse!!)
        activityMapsAllBinding.btnDirection.setOnClickListener {
            getDirection(i)
        }
        loadLocationNow(i)
    }

    private fun openDialog(i: Int) {
        val txtTitle= dialogLayout!!.findViewById<TextView>(R.id.txtTitle)
        txtTitle!!.text = listLatLng.get(i).name
        val btnDirection= dialogLayout!!.findViewById<CardView>(R.id.btnDirection)
        val image = dialogLayout!!.findViewById<ImageView>(R.id.imgSafehouse)
        Glide.with(this@MapsAllActivity).load(listLatLng.get(i).foto).circleCrop().into(image!!)
        btnDirection!!.setOnClickListener {
            getDirection(i)
        }
        loadLocationNow(i)
        dialogLayout!!.show()
    }

    private fun getDirection(a: Int) {
        var latLng:LatLng ?=null
        for (i in  0 until listLatLng.size) {
            if (i == a){
                latLng = listLatLng[i].latLng
            }
        }

        var finalLatLng = "google.navigation:q="+latLng!!.latitude.toString()+","+latLng.longitude.toString()
        val gmmIntentUri = Uri.parse(finalLatLng)
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
    }

    private fun loadLocationNow(a:Int) {
        if (ActivityCompat.checkSelfPermission(
                this@MapsAllActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this@MapsAllActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@MapsAllActivity,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                1000
            )
        } else {
                fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this@MapsAllActivity) { location ->
                        if (location != null) {
                            try {
                                for (i in  0 until listLatLng.size) {
                                    if (a == i) {
                                        var addresses =
                                            geoCoder!!.getFromLocation(
                                                listLatLng.get(i).latLng!!.latitude,
                                                listLatLng.get(i).latLng!!.longitude,
                                                1
                                            );
                                        if (addresses.isEmpty()) {
                                            activityMapsAllBinding.txtAlamat!!.setText("Waiting for Location");
                                        } else {
                                            activityMapsAllBinding.txtAlamat!!.setText(
                                                addresses.get(0).getAddressLine(0)
                                            );
                                        }
                                    }
                                }
                            } catch (e: java.lang.Exception) {
                                println("Catch " + e.message)
                            }
                        }
                    }

        }


//
    }

}