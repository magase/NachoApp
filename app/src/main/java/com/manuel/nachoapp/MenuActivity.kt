package com.manuel.nachoapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_menu.*

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)


        val bundle = intent.extras
        val email = bundle?.getString("email")
        val provider = bundle?.getString("provider")
        setUp(email ?: " ", provider ?: " ")
    }

    private fun setUp(email: String, provider: String){

        settingsButton.setOnClickListener{
            val homeIntent = Intent(this, HomeActivity::class.java).apply {
                putExtra("email", email)
                putExtra("provider", provider)
            }
            Log.d("UserAuth", email + " - " + provider)
            startActivity(homeIntent)
        }
    }


}