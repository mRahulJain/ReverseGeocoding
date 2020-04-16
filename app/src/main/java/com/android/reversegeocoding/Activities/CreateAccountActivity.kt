package com.android.reversegeocoding.Activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.edit
import androidx.viewpager.widget.ViewPager
import com.android.reversegeocoding.Adapter.TabPagerAdapter
import com.android.reversegeocoding.R
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_create_account.*

class CreateAccountActivity : AppCompatActivity() {

    val RC_SIGN_IN = 1
    lateinit var mAuth : FirebaseAuth
    lateinit var mGoogleApiClient : GoogleApiClient
    lateinit var mAuthListener : FirebaseAuth.AuthStateListener

    val KEY_GOOGLE_OPEN = "app_open"
    var googleCount = 0
    var type = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        //creating a shared preference to save small instances
        val prefs = getPreferences(Context.MODE_PRIVATE)
        googleCount = prefs.getInt(KEY_GOOGLE_OPEN, 0)

        mAuth = FirebaseAuth.getInstance()

        //Set the tabLayout Adapter
        val profilePagerAdapter = TabPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager1)
        viewPager.adapter = profilePagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs1)
        tabs.setupWithViewPager(viewPager)

        //googleSignIn
        if(googleCount == 0) {
            mAuthListener = FirebaseAuth.AuthStateListener {
                if(mAuth.currentUser != null) {
                    val intent = Intent(this, GeoLocationActivity::class.java)
                    googleCount++
                    prefs.edit {
                        putInt(KEY_GOOGLE_OPEN, googleCount)
                    }
                    startActivity(intent)
                    finish()
                }
            }

            mAuth.addAuthStateListener(mAuthListener)
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleApiClient = GoogleApiClient.Builder(this)
            .enableAutoManage(this, GoogleApiClient.OnConnectionFailedListener {
                Toast.makeText(this,
                    "Something's wrong",
                    Toast.LENGTH_SHORT).show()
            })
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()

        reqGoogle.setOnClickListener {
            type = "google"
            if(googleCount != 0) {
                mAuthListener = FirebaseAuth.AuthStateListener {
                    if(mAuth.currentUser != null) {
                        val intent = Intent(this, GeoLocationActivity::class.java)
                        intent.putExtra("password", "")
                        googleCount++
                        prefs.edit {
                            putInt(KEY_GOOGLE_OPEN, googleCount)
                        }
                        startActivity(intent)
                        finish()
                    } else {
                        signIn()
                    }
                }
                mAuth.addAuthStateListener(mAuthListener)
            } else {
                signIn()
            }
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(type == "google") {
            if (requestCode == RC_SIGN_IN) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)
                    firebaseAuthWithGoogle(account!!)
                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                    Log.d("myCHECK", "Google sign in failed", e)
                    // ...
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        Log.d("myCHECK", "firebaseAuthWithGoogle:" + account.id!!)

        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("myCHECK", "signInWithCredential:success")
                    val user = mAuth.currentUser
//                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.d("myCHECK", "signInWithCredential:failure", task.exception)
                    Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_SHORT).show()
//                    updateUI(null)
                }
                // ...
            }
    }

    private fun signIn() {
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
}
