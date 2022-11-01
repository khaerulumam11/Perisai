package co.id.perisaijava

import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    var etEmail: EditText? = null
    var etPassword:EditText? = null
    var btnLogin: AppCompatTextView? = null
    var btnRegis:AppCompatTextView? = null
    var conMgr: ConnectivityManager? = null
    var id: String? = null
    var email:kotlin.String? = null
    var name:kotlin.String? = null
    var chance:kotlin.String? = null
    var level:kotlin.String? = null
    var score:kotlin.String? = null
    var pDialog: ProgressDialog? = null
    var sharedpreferences: SharedPreferences? = null
    var session = false
    val my_shared_preferences = "my_shared_preferences"
    val session_status = "session_status"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

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
        btnLogin = findViewById(R.id.btn_login)
        btnRegis = findViewById(R.id.btn_register)

        // Cek session login jika TRUE maka langsung buka MainActivity

        // Cek session login jika TRUE maka langsung buka MainActivity
        sharedpreferences = getSharedPreferences(my_shared_preferences, MODE_PRIVATE)
        session = sharedpreferences!!.getBoolean(session_status, false)
        id = sharedpreferences!!.getString("id", null)
        email = sharedpreferences!!.getString("email", null)
        name = sharedpreferences!!.getString("name", null)
        chance = sharedpreferences!!.getString("chance", null)
        level = sharedpreferences!!.getString("level", null)
        score = sharedpreferences!!.getString("score", null)

        if (session) {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            intent.putExtra("id", id)
            intent.putExtra("email", email)
            intent.putExtra("name", name)
            intent.putExtra("chance", chance)
            intent.putExtra("level", level)
            startActivity(intent)
            finish()
        }

        btnRegis!!.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        })

        btnLogin!!.setOnClickListener(View.OnClickListener {
            val email = etEmail!!.getText().toString()
            val password = etPassword!!.getText().toString()

            // mengecek kolom yang kosong
            if (email.trim { it <= ' ' }.length > 0 && password.trim { it <= ' ' }.length > 0) {
                if (conMgr!!.activeNetworkInfo != null && conMgr!!.activeNetworkInfo!!.isAvailable
                    && conMgr!!.activeNetworkInfo!!.isConnected
                ) {
                    checkLogin(email, password)
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
    private fun checkLogin(email: String, password: String) {
        var pindah = Intent(this@LoginActivity,MainActivity::class.java)
        startActivity(pindah)
//        pDialog = ProgressDialog(this)
//        pDialog!!.setCancelable(false)
//        pDialog!!.setMessage("Logging in ...")
//        showDialog()
//        AndroidNetworking.post(Server.ENDPOINT_LOGIN)
//            .addBodyParameter("email", email)
//            .addBodyParameter("password", password)
//            .setPriority(Priority.HIGH)
//            .build()
//            .getAsJSONObject(object : JSONObjectRequestListener {
//                override fun onResponse(response: JSONObject) {
////                        setIsLoading(false);
//                    println("Response $response")
//                    val editor = sharedpreferences!!.edit()
//                    editor.putBoolean(LoginActivity.session_status, true)
//                    try {
//                        editor.putString("id", response.getString("id"))
//                        editor.putString("name", response.getString("name"))
//                        editor.putString("chance", response.getString("chance"))
//                        editor.putString("score", response.getString("score"))
//                        editor.putString("level", response.getString("level"))
//                    } catch (e: JSONException) {
//                        e.printStackTrace()
//                    }
//                    editor.putString("email", email)
//                    editor.commit()
//
//                    // Memanggil main activity
//                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
//                    intent.putExtra("id", id)
//                    intent.putExtra("email", email)
//                    hideDialog()
//                    startActivity(intent)
//                    finish()
//                }
//
//                override fun onError(anError: ANError) {
//                    println("Erorr Login")
//                    hideDialog()
//                }
//            })
    }

    private fun showDialog() {
        if (!pDialog!!.isShowing) pDialog!!.show()
    }

    private fun hideDialog() {
        if (pDialog!!.isShowing) pDialog!!.dismiss()
    }
}