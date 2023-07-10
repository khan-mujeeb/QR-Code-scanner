package com.example.resolutionai.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Vibrator
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.resolutionai.R
import com.example.resolutionai.adapter.ResultAdapter
import com.example.resolutionai.database.viewmodel.DBViewModle
import com.example.resolutionai.databinding.ActivityMainBinding
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

class MainActivity : AppCompatActivity() {

    lateinit var viewMole: DBViewModle
    lateinit var binding: ActivityMainBinding
    lateinit var options: GmsBarcodeScannerOptions
    lateinit var scanner: GmsBarcodeScanner
    lateinit var vibrator: Vibrator
    lateinit var adapter: ResultAdapter
    private val PREF_NAME = "qr_code_pref"
    private val FIRST_TIME_VISTOR = "first_timer"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()


        checkForFirstTimeUser()
        variableInit()
        subscribeClickEvents()
        subscribeUi()

    }

    private fun checkForFirstTimeUser() {
        val sharedPref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        if (isFirstTimeVistor(sharedPref)) {
            sharedPref.edit().putBoolean(FIRST_TIME_VISTOR, false).apply()
            startActivity(Intent(this, IntroductionActivity::class.java))
        }
    }

    private fun variableInit() {

        options = GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_QR_CODE,
                Barcode.FORMAT_AZTEC
            ).build()

        scanner = GmsBarcodeScanning.getClient(this@MainActivity, options)
        viewMole = ViewModelProvider(this)[DBViewModle::class.java]
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    }



    private fun subscribeUi() {
        loadingScreenAndData()
        var itemTouchHelperCallbacks = object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val postion = viewHolder.adapterPosition
                val qrCode = adapter.getQrCodeAt(postion)
                viewMole.deleteEntery(qrCode)

                Toast.makeText(this@MainActivity, getString(R.string.deleted), Toast.LENGTH_SHORT)
                    .show()

            }

        }

        ItemTouchHelper(itemTouchHelperCallbacks).apply {
            attachToRecyclerView(binding.rc)
        }
    }

    private fun loadingScreenAndData() {
        binding.loading.visibility = View.VISIBLE
        viewMole.scannedQr.observe(this) { task ->
            binding.loading.visibility = View.INVISIBLE
            adapter = ResultAdapter(this@MainActivity, task)
            if (task.isNotEmpty()) {
                binding.clearAll.visibility = View.VISIBLE
                binding.noDataTextView.visibility = View.GONE
                binding.rc.visibility = View.VISIBLE
                binding.rc.adapter = adapter
            } else {
                binding.clearAll.visibility = View.GONE
                binding.noDataTextView.visibility = View.VISIBLE
                binding.rc.visibility = View.GONE
            }
        }
    }

    private fun subscribeClickEvents() {

        // scan qr code
        binding.scanQr.setOnClickListener {
            scan()
        }

        // delete all entries
        binding.clearAll.setOnClickListener {
            viewMole.deleteAllEntries()
            Toast.makeText(this, "all data deleted", Toast.LENGTH_SHORT).show()

        }


    }

    private fun scan() {
        scanner.startScan()
            .addOnSuccessListener { barcode ->

                if (vibrator.hasVibrator()) {
                    val milliseconds = 250L
                    vibrator.vibrate(milliseconds)
                }

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
                startActivity(Intent(this, ErrorActivity::class.java))
            }
    }

    private fun isFirstTimeVistor(sharedPref: SharedPreferences?): Boolean {
        return sharedPref!!.getBoolean(FIRST_TIME_VISTOR, true)
    }

}