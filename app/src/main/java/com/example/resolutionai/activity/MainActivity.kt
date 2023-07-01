package com.example.resolutionai.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.resolutionai.adapter.ResultAdapter
import com.example.resolutionai.database.viewmodel.DBViewModle
import com.example.resolutionai.databinding.ActivityMainBinding
import com.example.resolutionai.utils.DialogUtils.buildLoadingDialog
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

class MainActivity : AppCompatActivity() {

    lateinit var viewMole: DBViewModle
    lateinit var binding: ActivityMainBinding
    lateinit var alertDialog: AlertDialog
    lateinit var options: GmsBarcodeScannerOptions
    lateinit var scanner: GmsBarcodeScanner


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        variableInit()
        subscribeClickEvents()
//        subscribeUi()

    }

    private fun subscribeUi() {
        alertDialog.show()
        viewMole.scannedQr.observe(this) { task ->
            alertDialog.dismiss()
            if (task.isNotEmpty()) {
                binding.noDataTextView.visibility = View.GONE
                binding.rc.visibility = View.VISIBLE
                binding.rc.adapter = ResultAdapter(this@MainActivity, task)
            } else {
                binding.noDataTextView.visibility = View.VISIBLE
                binding.rc.visibility = View.GONE
            }
        }
    }

    private fun subscribeClickEvents() {
        binding.scanQr.setOnClickListener {
            scanner.startScan()
                .addOnSuccessListener {  barcode ->
                    val intent = Intent(this, ResultActivity::class.java)
                    intent.putExtra("result", barcode.rawValue)
                    Toast.makeText(this, "scanned", Toast.LENGTH_SHORT).show()
                    startActivity(intent)
                }
                .addOnCanceledListener {
                    Toast.makeText(this, "canceled", Toast.LENGTH_SHORT).show()

                }
                .addOnFailureListener {
                    Toast.makeText(this, "failure", Toast.LENGTH_SHORT).show()

                }
        }
    }

    private fun variableInit() {

        viewMole = ViewModelProvider(this)[DBViewModle::class.java]

        options = GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_QR_CODE,
                Barcode.FORMAT_AZTEC
            ).build()

        scanner = GmsBarcodeScanning.getClient(this, options)

        alertDialog = buildLoadingDialog(this)

    }
    override fun onDestroy() {
        super.onDestroy()
        alertDialog.dismiss()
    }

}