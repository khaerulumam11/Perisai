package co.id.perisaijava

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import com.google.gson.Gson
import com.weiwangcn.betterspinner.library.BetterSpinner
import java.text.SimpleDateFormat
import java.util.*

class RegisterActivity : AppCompatActivity() {
    var etEmail: EditText? = null
    var etPassword:EditText? = null
    var etName:EditText? = null
    var etDate:EditText? = null
    var btnLogin: AppCompatTextView? = null
    var btnRegis:AppCompatTextView? = null
    var atCities: BetterSpinner? = null
    var conMgr: ConnectivityManager? = null
    var pDialog: ProgressDialog? = null
//    private val list: ArrayList<CityModel.ResultEntity> = ArrayList<CityModel.ResultEntity>()
    var listName = ArrayList<String>()
    private val gson: Gson? = null
    private val idCity = "0"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

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

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etName = findViewById(R.id.etName)
        etDate = findViewById(R.id.etDateBirth)
        btnLogin = findViewById(R.id.btn_login)
        btnRegis = findViewById(R.id.btn_register)
        atCities = findViewById(R.id.etDomicile)

        etDate!!.setOnClickListener(View.OnClickListener { showDatePicker() })
//        loadDateCities()


        btnLogin!!.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
        })

        btnRegis!!.setOnClickListener(View.OnClickListener {
            val email = etEmail!!.getText().toString()
            val password = etPassword!!.getText().toString()
            val name = etName!!.getText().toString()

            // mengecek kolom yang kosong
            if (email.trim { it <= ' ' }.length > 0 && password.trim { it <= ' ' }.length > 0 && name.trim { it <= ' ' }.length > 0) {
                if (conMgr!!.activeNetworkInfo != null && conMgr!!.activeNetworkInfo!!.isAvailable
                    && conMgr!!.activeNetworkInfo!!.isConnected
                ) {
//                    register(email, password, name)
                } else {
                    Toast.makeText(applicationContext, "No Internet Connection", Toast.LENGTH_LONG)
                        .show()
                }
            } else {
                // Prompt user to enter credentials
                Toast.makeText(applicationContext, "Kolom tidak boleh kosong", Toast.LENGTH_LONG)
                    .show()
            }
        })
    }

    private var calendar: Calendar? = null
    private var year = 0
    private  var month:kotlin.Int = 0
    private  var days:kotlin.Int = 0

    private fun showDatePicker() {
        calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.US)
        year = calendar!!.get(Calendar.YEAR)
        month = calendar!!.get(Calendar.MONTH)
        days = calendar!!.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(this,
            { datePicker, i, i1, i2 ->
                calendar!!.set(i, i1, i2)
                etDate!!.setText(sdf.format(calendar!!.getTime()))
            }, year, month, days
        )
        datePickerDialog.show()
    }
}