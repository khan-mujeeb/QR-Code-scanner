package com.example.resolutionai.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.resolutionai.account.LoginActivity
import com.example.resolutionai.account.QRCodeCameraActivity
import com.example.resolutionai.databinding.ActivityMainBinding
import com.example.resolutionai.utils.FirebaseUtils

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        goToSignup()
        subscribeClickEvents()

    }

    private fun subscribeClickEvents() {
        binding.scanQr.setOnClickListener {
            startActivity(Intent(this, QRCodeCameraActivity::class.java))
        }
    }

    fun goToSignup() {
        if (FirebaseUtils.firebaseUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}