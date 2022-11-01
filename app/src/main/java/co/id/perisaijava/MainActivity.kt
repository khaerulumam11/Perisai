package co.id.perisaijava

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import co.id.perisaijava.R

class MainActivity : AppCompatActivity() {
    private var btnCard :CardView ?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnCard = findViewById(R.id.cardSafeHouse)

        btnCard!!.setOnClickListener {
            var pindah = Intent(this@MainActivity,MapsActivity::class.java)
            startActivity(pindah)
        }
    }
}