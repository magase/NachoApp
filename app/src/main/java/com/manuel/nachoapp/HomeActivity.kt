package com.manuel.nachoapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_home.*

enum class ProviderType{
    BASIC,
    GOOGLE
}

class HomeActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        //SetUp
        val bundle = intent.extras
        val email = bundle?.getString("email")
        val provider = bundle?.getString("provider")
        setUp(email ?: " ", provider ?: " ")
        Log.d("UserHome", email + " - " + provider)

        // Guardar Datos
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("provider", provider)
        prefs.apply()

    }

    override fun onRestart() {
        super.onRestart()
        if(FirebaseAuth.getInstance().currentUser == null){
            val homeIntent = Intent(this, AuthActivity::class.java)
            startActivity(homeIntent)
            onDestroy()
        }
    }

    private fun setUp(email: String, provider: String) {
        title = "Inicio"
        emailTextView.text=email
        providerTextView.text=provider

        logOutButton.setOnClickListener {

            //Borrado de datos
            val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()

            //onBackPressed()
            val homeIntent = Intent(this, AuthActivity::class.java)
            startActivity(homeIntent)
            FirebaseAuth.getInstance().signOut()
            FirebaseAuth.getInstance().currentUser
            Log.d("Sesion2", FirebaseAuth.getInstance().signOut().toString())
            Log.d("Sesion2", FirebaseAuth.getInstance().currentUser.toString())

        }

        saveButton.setOnClickListener {
            db.collection("users").document(email).set(
                hashMapOf("provider" to provider,
                    "address" to addressTextView.text.toString(),
                    "phone" to phoneTextView.text.toString()
                )
            )
        }

        getButton.setOnClickListener {
            db.collection("users").document(email).get().addOnSuccessListener {
                addressTextView.setText(it.get("address") as String?)
                phoneTextView.setText(it.get("phone") as String?)
            }
        }

        deleteButton.setOnClickListener {
            db.collection("users").document(email).delete()
        }

    }
}