package com.example.resolutionai.account

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.resolutionai.R
import com.example.resolutionai.databinding.ActivityQrcodeCameraBinding
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult

private const val CAMERA_PERMISSION_REQUEST_CODE = 1001

class QRCodeCameraActivity : AppCompatActivity(), SurfaceHolder.Callback {

    lateinit var binding: ActivityQrcodeCameraBinding
    private lateinit var surfaceView: SurfaceView
    private lateinit var surfaceHolder: SurfaceHolder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrcodeCameraBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_qrcode_camera)


        variableInit()
        checkCameraPermisson()


    }

    private fun checkCameraPermisson() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Permission already granted, initialize the code scanner
            startCameraPreview()

        } else {
            // Request camera permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun variableInit() {
        surfaceView = binding.cameraPreview
        surfaceHolder = surfaceView.holder
        surfaceHolder.addCallback(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCameraPreview()
            } else {
                // Camera permission denied
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startCameraPreview() {
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
        surfaceHolder.setFixedSize(400, 400) // Set desired preview size
        surfaceHolder.setKeepScreenOn(true)

        IntentIntegrator(this).initiateScan() // Start the QR code scanning process
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        // Implement surfaceCreated() if needed
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        // Implement surfaceChanged() if needed
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        // Implement surfaceDestroyed() if needed
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result: IntentResult? =
            IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        if (result != null && result.contents != null) {
            // QR code scanned successfully
            val scannedResult: String = result.contents
            Toast.makeText(this,  "Scanned QR code: $scannedResult", Toast.LENGTH_SHORT).show()

            // Process the scanned result as per your requirement
        } else {
            // QR code scanning was canceled or failed
            Toast.makeText(this, "QR code scanning failed or canceled", Toast.LENGTH_SHORT).show()
        }

        // Start another scan
        IntentIntegrator(this).initiateScan()
    }

}



