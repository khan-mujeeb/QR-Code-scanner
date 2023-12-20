package com.khandev.qrcodescanner.presentation.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import com.khandev.qrcodescanner.databinding.ActivityIntroductionBinding

class IntroductionActivity : AppCompatActivity() {
    lateinit var binding: ActivityIntroductionBinding
    lateinit var options: GmsBarcodeScannerOptions
    lateinit var scanner: GmsBarcodeScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroductionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        variableInit()

        subscribeUi()
        subscribeOnClickEvents()

    }

    private fun subscribeOnClickEvents() {
        binding.btn.setOnClickListener {

            startActivity(Intent(this@IntroductionActivity, MainActivity::class.java))
            finish()
            setUpScanner()

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