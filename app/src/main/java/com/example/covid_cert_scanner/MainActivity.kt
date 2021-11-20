package com.example.covid_cert_scanner

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.*
import com.github.kittinunf.fuel.Fuel
import android.text.method.ScrollingMovementMethod
import kotlinx.serialization.*
import kotlinx.serialization.json.*




//import java.security.KeyStore

private const val CAMERA_REQUEST_CODE = 101
class MainActivity : AppCompatActivity() {
    private lateinit var codeScanner: CodeScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupPerms()
        codeScanner()
    }
    @Serializable
    data class NZCPJson(val success: Boolean, val credentialSubject: NZCPCredential?, val violates: NZCPViolates?,val cwtClaims:String? = null)
    @Serializable
    data class NZCPViolates(val message: String?, val link: String?, val section: String? )
    @Serializable
    data class NZCPCredential(val givenName: String?, val familyName: String?, val dob: String?)
    private fun decodeCovidQR(code:String){

        //host a node API and call it from here
        val tvtextView = findViewById<TextView>(R.id.tv_textView)
        val serverurl = findViewById<EditText>(R.id.serverurl)
        tvtextView.movementMethod = ScrollingMovementMethod()
try{
        Fuel.get("${serverurl.text}${code}")//make it so this is changeable in the app
                .response { _, response, _ ->

                    val obj = Json.decodeFromString<NZCPJson>(String(response.data))
                    val outString =
                        "Valid: ${obj.success} \n First Name(s): ${obj.credentialSubject?.givenName} \n Family Name:${obj.credentialSubject?.familyName} \n dob: ${obj.credentialSubject?.dob} \n Violates: ${obj.violates?.message}"
                    tvtextView.text = outString

                }


            }
catch(e:Throwable){
  val  errortext = "error scanning vaccine certificate"
    tvtextView.text  = errortext
}

    }

    private fun codeScanner(){
        val scannerView = findViewById<CodeScannerView>(R.id.scanner_view)
        codeScanner = CodeScanner(this, scannerView)
        codeScanner.apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.TWO_DIMENSIONAL_FORMATS

            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.SINGLE
            isAutoFocusEnabled = true
            isFlashEnabled = false

            decodeCallback = DecodeCallback {
                runOnUiThread {
                    //decode covid QR code
                    decodeCovidQR(it.text)
                    //tvtextView.text = validated
                }
            }
            errorCallback = ErrorCallback {
                runOnUiThread {
                    Log.e("main","camera init error ${it.message}")
                }
            }
            scannerView.setOnClickListener{
                codeScanner.startPreview()
            }


      }
    }

    override fun onResume() {
        val tvtextView = findViewById<TextView>(R.id.tv_textView)
        super.onResume()
        codeScanner.startPreview()
        val resumeText = "Scan Vaccine Passport QR"
        tvtextView.text = resumeText
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    private fun setupPerms(){
        val permission:Int = ContextCompat.checkSelfPermission(this,
        Manifest.permission.CAMERA)
        if (permission != PackageManager.PERMISSION_GRANTED){
            makeRequest()
        }

    }
    private fun makeRequest(){
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)

    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode){
            CAMERA_REQUEST_CODE ->{
                if(grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,"you need the camera permission",Toast.LENGTH_SHORT).show()
                } else{
                    return
                    // successful request
                }
            }
        }
    }

}