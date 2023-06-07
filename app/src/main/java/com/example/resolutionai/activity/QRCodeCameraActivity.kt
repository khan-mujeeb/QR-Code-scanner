package com.example.resolutionai.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.resolutionai.data.ScannerResult
import com.example.resolutionai.viewmodel.ViewModel

import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView


class QRCodeCameraActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {

    var scannerView: ZXingScannerView? = null
    private lateinit var viewModel: ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scannerView = ZXingScannerView(this)
        setContentView(scannerView)

        setPermission()
        variableInit()

    }

    private fun variableInit() {
        viewModel = ViewModelProvider(this)[ViewModel::class.java]

    }


    private fun setPermission() {
        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.CAMERA),
            1
        )
    }

    override fun handleResult(p0: Result?) {
        val data = p0!!.text.toString()
        val intent = Intent(this, ResultActivity::class.java)

        viewModel.addResult(
            ScannerResult(
                data
            )
        )

        intent.putExtra("result", data)
        startActivity(intent)
        finish()
    }

    override fun onResume() {
        super.onResume()

        scannerView?.setResultHandler(this)
        scannerView?.startCamera()
    }

    override fun onStop() {
        super.onStop()
        scannerView?.stopCamera()
        onBackPressed()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            1 -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(
                        applicationContext,
                        "You need camera permission",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    }
}