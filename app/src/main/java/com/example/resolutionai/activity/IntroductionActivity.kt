package com.example.resolutionai.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.button_morphing.customview.MorphButton
import com.example.resolutionai.R
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
        subscribeUi()
        subscribeOnClickEvents()


    }

    private fun subscribeOnClickEvents() {
        binding.btn.setOnClickListener {
            setUpScanner()
            binding.btn.setUIState(MorphButton.UIState.Loading)



            Handler().postDelayed({
                startActivity(Intent(this@IntroductionActivity, MainActivity::class.java))
                finish()
            }, 2000)
        }
    }

    private fun subscribeUi() {
        binding.btn.text = "Setup Secure Environment"
        binding.btn.toBgColor = getColor(R.color.sea_green)
        binding.btn.fromBgColor = getColor(R.color.sea_green)
        binding.btn.toTextColor = getColor(R.color.white)
        binding.btn.fromTextColor = getColor(R.color.white)



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