package com.android.reversegeocoding.Activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.text.bold
import com.android.reversegeocoding.R
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_geo_location.*
import java.lang.Exception
import java.util.*


class GeoLocationActivity : AppCompatActivity(), LocationListener {

    val auth = FirebaseAuth.getInstance()
    private val LOCATION_REQ = 123
    private var locationManager: LocationManager? = null
    private var myLat = 0.0
    private var myLong = 0.0
    private val CHECK_REQ = 121
    var type : String = ""
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var myCoordinates : LatLng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_geo_location)

        //type variable is used fo logout purpose
        //if type = auth then Firebase.auth.currentUser != null
        //if type = login then Firebase.auth.currentUser = null
        type = intent.getStringExtra("type")

        //code to execute when user requests geocode
        traceLocation.setOnClickListener {
            checkUserSettingsAndGetLocation()
        }

        //code to execute when user clicks on the logout button
        logout.setOnClickListener {
            logout()
        }
    }

    override fun onLocationChanged(p0: Location?) {
        p0?.let { it ->
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location : Location? ->
                    myLat = location!!.latitude
                    myLong = location!!.longitude
                    myCoordinates = LatLng(myLat, myLong)
                    var address = SpannableStringBuilder()
                        .append("Your last location traced was: \n\n")
                        .bold {
                            append("${getCityName(myCoordinates)}")
                        }
                    tvLocation.text = address
                }
        }
    }

    private fun getCityName(myCoordinates : LatLng) : String {
        var myCity = ""

        val geoCoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses = geoCoder.getFromLocation(
                myCoordinates.latitude,
                myCoordinates.longitude,
                1
            )
            myCity = addresses[0].getAddressLine(0)
        } catch (e : Exception) {
            Log.e("PUI", "${e.printStackTrace()}")
        }
        return myCity
    }

    //this function will check whether the location access permission allowed to the app or not
    //if the location is not permitted the user will be logged out
    //if the location is permitted then it will call startLocationUpdates()
    private fun checkAndStartLocationUpdates() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                LOCATION_REQ
            )
        }else{
            startLocationUpdates()
        }
    }

    //this function will check the location setting
    //if the location of the device is off then if will prompt an alert for the user to switch it on
    //if the location is on then it will call checkAndStartLocationUpdates()
    //if the user denies to switch on the location the he will logged out
    private fun checkUserSettingsAndGetLocation(){
        val locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            val alertDialog = AlertDialog.Builder(this)
                .setTitle("Location turned on") // GPS not found
                .setMessage("Please logout for better experience") // Want to enable?
                .setPositiveButton("YES"
                ) { dialogInterface, i ->
                    val locationRequest = LocationRequest().apply {
                        interval = 10000
                        fastestInterval = 5000
                        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                    }

                    val request = LocationSettingsRequest.Builder()
                        .addLocationRequest(locationRequest)
                        .build()
                    val client = LocationServices.getSettingsClient(this)

                    client.checkLocationSettings(request).apply {
                        addOnSuccessListener {
                            Log.d("PUI","success")
                            checkAndStartLocationUpdates()
                        }
                        addOnFailureListener{
                            Log.d("PUI", "$it")
                            val e = it as ApiException
                            if (e.statusCode == LocationSettingsStatusCodes.RESOLUTION_REQUIRED){
                                val resolvable = it as ResolvableApiException
                                resolvable.startResolutionForResult(this@GeoLocationActivity,CHECK_REQ)
                            }
                        }
                    }
                    Toast.makeText(this,"Please login again", Toast.LENGTH_LONG).show()
                    logout()
                }.create()
            alertDialog.setCancelable(false)
            alertDialog.show()
        }

        val locationRequest = LocationRequest().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val request = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .build()
        val client = LocationServices.getSettingsClient(this)

        client.checkLocationSettings(request).apply {
            addOnSuccessListener {
                Log.d("PUI","success")
                checkAndStartLocationUpdates()
            }
            addOnFailureListener{
                Log.d("PUI", "$it")
                val e = it as ApiException
                if (e.statusCode == LocationSettingsStatusCodes.RESOLUTION_REQUIRED){
                    val resolvable = it as ResolvableApiException
                    resolvable.startResolutionForResult(this@GeoLocationActivity,CHECK_REQ)
                }
            }
            addOnCanceledListener {
                Log.d("PUI", "CANCELED")
            }
            addOnCompleteListener {
                Log.d("PUI", "COMPLETE ${it}")
            }
        }
    }

    //this code will be executed as per users command on allowing or not the location access
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == LOCATION_REQ){
            for(i in grantResults.indices){
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,"You are logged out for not allowing permission", Toast.LENGTH_LONG).show()
                    logout()
                    return
                }
            }
            startLocationUpdates()
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val enabledProvider =
            when {
                locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> LocationManager.NETWORK_PROVIDER
                locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER) -> LocationManager.GPS_PROVIDER
                else -> "nill"
            }
        locationManager?.requestLocationUpdates(
            enabledProvider,
            1000,
            0f,
            this
        )
    }

    //function to be executed when user destroys the app
    override fun onDestroy() {
        locationManager?.removeUpdates(this)
        super.onDestroy()
    }

    //function to be executed for logout purpose
    private fun logout() {
        if(auth.currentUser!=null) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.putExtra("source", "auth")
            startActivity(intent)
            auth.signOut()
            finish()
            return
        }

        if(type == "login") {
            val intent = Intent(this, LoginActivity::class.java)
            intent.putExtra("source", "login")
            startActivity(intent)
            finish()
            return
        }
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {}

    override fun onProviderEnabled(p0: String?) {}

    override fun onProviderDisabled(p0: String?) {}
}
