package com.manuel.nachoapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_auth.*
import java.lang.Exception


class AuthActivity : AppCompatActivity() {

    private val GOOGLE_SIGN_IN = 100
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        Thread.sleep(200)
        setTheme(R.style.Theme_NachoApp)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        emailEditText.text.clear()
        passwordEditText.text.clear()

        val analytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("message", "Intregracion con Firebase completa")
        analytics.logEvent("InitScreen", bundle)
        setUp()

        session()
    }

    override fun onStart() {
        super.onStart()
        authLayout.visibility = View.VISIBLE
    }

    private fun session() {
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)
        val provider = prefs.getString("provider", null)

        if (email != null && provider != null) {
            authLayout.visibility = View.INVISIBLE
            showHome(email, ProviderType.valueOf(provider))
        }
    }

    private fun setUp() {
        title = "Autenticación"
        signUpButton.setOnClickListener {
            if (emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    emailEditText.text.toString(),
                    passwordEditText.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                    } else {
                        showAlert()
                    }
                }
            }
        }

        loginButton.setOnClickListener {
            if (emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(
                    emailEditText.text.toString(),
                    passwordEditText.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                    } else {
                        showAlert()
                    }
                }
            }
        }

        googleButton.setOnClickListener {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("852068756770-gvr0gl60089i4aa3jldtpp4okb40a02b.apps.googleusercontent.com")
                .requestEmail()
                .build()

            googleSignInClient = GoogleSignIn.getClient(this, gso)
            val signInIntent = googleSignInClient.signInIntent
            googleSignInClient.signOut()
            startActivityForResult(signInIntent, GOOGLE_SIGN_IN)
        }

        twitterButton.setOnClickListener {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("En proceso")
                builder.setMessage("Todavia no esta implementado")
                builder.setPositiveButton("Aceptar", null)
                val dialog: AlertDialog = builder.create()
                dialog.show()
        }


    }

    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al usuarig")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showHome(email: String, provider: ProviderType) {
        val homeIntent = Intent(this, MenuActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        Log.d("UserAuth", email + " - " + provider)
        startActivity(homeIntent)
        Log.d("Sesion1", FirebaseAuth.getInstance().toString())
        Log.d("Sesion1", FirebaseAuth.getInstance().currentUser.toString())


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)

                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener {
                    if (it.isSuccessful) {
                        showHome(account.email ?: "", ProviderType.GOOGLE)
                    } else {
                        showAlert()
                    }
                }
            } catch (e: Exception) {
                e.localizedMessage
                e.stackTrace
                showAlert()
            }

        }
    }
}