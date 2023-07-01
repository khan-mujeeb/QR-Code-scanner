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
import com.google.android.gms.common.moduleinstall.ModuleInstall
import com.google.android.gms.common.moduleinstall.ModuleInstallRequest
import com.google.android.gms.tflite.java.TfLite
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

        val moduleInstallClient = ModuleInstall.getClient(this)

        val optionalModuleApi = TfLite.getClient(this)
        moduleInstallClient
            .areModulesAvailable(optionalModuleApi)
            .addOnSuccessListener {
                if (it.areModulesAvailable()) {
                    Toast.makeText(this, "", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "we are setting up your app", Toast.LENGTH_SHORT).show()
                    val moduleInstallClient = ModuleInstall.getClient(this)
                    val optionalModuleApi = TfLite.getClient(this)
                    val moduleInstallRequest =
                        ModuleInstallRequest.newBuilder()
                            .addApi(optionalModuleApi)
                            .build()


                    moduleInstallClient
                        .installModules(moduleInstallRequest)
                        .addOnSuccessListener {
                            if (it.areModulesAlreadyInstalled()) {
                                // Modules are already installed when the request is sent.
                            }
                        }
                        .addOnFailureListener {
                            // Handle failure…
                        }

                }
            }
            .addOnFailureListener {

            }

        variableInit()
        subscribeClickEvents()
        subscribeUi()

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
                    println("mujeeb $it")
                    Toast.makeText(this, "failure", Toast.LENGTH_LONG).show()

                }
        }
    }

    private fun variableInit() {

        options = GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_QR_CODE,
                Barcode.FORMAT_AZTEC
            ).build()

        scanner = GmsBarcodeScanning.getClient(this, options)

        viewMole = ViewModelProvider(this)[DBViewModle::class.java]



        alertDialog = buildLoadingDialog(this)

    }
    override fun onDestroy() {
        super.onDestroy()
        alertDialog.dismiss()
    }

}