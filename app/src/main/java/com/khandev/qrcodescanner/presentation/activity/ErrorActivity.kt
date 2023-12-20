package com.khandev.qrcodescanner.presentation.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import com.khandev.qrcodescanner.databinding.ActivityErrorBinding

class ErrorActivity : AppCompatActivity() {
    lateinit var binding: ActivityErrorBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityErrorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        subscribeOnClickListner()
    }

    private fun subscribeOnClickListner() {
        binding.goToSetting.setOnClickListener {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = android.net.Uri.parse("package:com.google.android.gms")
            startActivity(intent)
        }
    }
}