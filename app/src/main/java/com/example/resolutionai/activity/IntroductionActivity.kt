package com.example.resolutionai.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

import com.example.resolutionai.databinding.ActivityIntroductionBinding
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

class IntroductionActivity : AppCompatActivity() {
    lateinit var binding: ActivityIntroductionBinding
    lateinit var options: GmsBarcodeScannerOptions
    lateinit var scanner: GmsBarcodeScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroductionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        variableInit()
        setUpScanner()

        subscribeUi()
        subscribeOnClickEvents()

    }

    private fun subscribeOnClickEvents() {
        binding.btn.setOnClickListener {
            Handler().postDelayed({
                startActivity(Intent(this@IntroductionActivity, MainActivity::class.java))
                finish()
            }, 500)
        }
    }

    private fun subscribeUi() {
        binding.btn.text = "Setup Secure Environment"
    }

    private fun setUpScanner() {
        scanner.startScan()
            .addOnSuccessListener { barcode ->

            }
            .addOnCanceledListener {

            }
            .addOnFailureListener {

            }
    }


    private fun variableInit() {

        options = GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_QR_CODE,
                Barcode.FORMAT_AZTEC
            ).build()

        scanner = GmsBarcodeScanning.getClient(this, options)


    }
}