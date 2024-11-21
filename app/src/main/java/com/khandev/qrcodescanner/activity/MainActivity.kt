package com.khandev.qrcodescanner.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Vibrator
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.khandev.qrcodescanner.adapter.ResultAdapter
import com.khandev.qrcodescanner.database.viewmodel.DBViewModle
import com.google.android.gms.common.moduleinstall.ModuleInstall
import com.google.android.gms.common.moduleinstall.ModuleInstallClient
import com.google.android.gms.common.moduleinstall.ModuleInstallRequest
import com.google.android.gms.tflite.client.TfLiteClient
import com.google.android.gms.tflite.java.TfLite
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import com.khandev.qrcodescanner.R
import com.khandev.qrcodescanner.adapter.CategoryAdapter
import com.khandev.qrcodescanner.databinding.ActivityMainBinding
import com.khandev.qrcodescanner.utlis.Categories

class MainActivity : AppCompatActivity() {
    lateinit var viewMole: DBViewModle
    private lateinit var binding: ActivityMainBinding
    private lateinit var options: GmsBarcodeScannerOptions
    private lateinit var scanner: GmsBarcodeScanner
    private lateinit var vibrator: Vibrator
    private lateinit var adapter: ResultAdapter
    private val PREF_NAME = "qr_code_pref"
    private val FIRST_TIME_VISTOR = "first_timer"
    private lateinit var moduleInstallClient: ModuleInstallClient
    private lateinit var  optionalModuleApi: TfLiteClient
    private lateinit var moduleInstallRequest:  ModuleInstallRequest


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        checkForFirstTimeUser()

        variableInit()
        subscribeClickEvents()
        subscribeUi()
        checkForModule()

    }



    private fun checkForModule() {
        moduleInstallClient
            .areModulesAvailable(optionalModuleApi)
            .addOnSuccessListener {
                if (it.areModulesAvailable()) {
                    println("already available")

                } else {
                    sendModuleInstallRequest()
                }
            }
            .addOnFailureListener {
                // Handle failure...
            }
    }

    private fun sendModuleInstallRequest() {
        moduleInstallClient
            .installModules(moduleInstallRequest)
            .addOnSuccessListener {
                if (it.areModulesAlreadyInstalled()) {
                    println("already installed")
                }
            }
            .addOnFailureListener {
                println("installation failed")

            }
    }

    // function to check if user is first time visitor
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
                Barcode.FORMAT_AZTEC,
                Barcode.FORMAT_EAN_13,
                Barcode.FORMAT_EAN_8,
                Barcode.FORMAT_UPC_A
            ).build()


        optionalModuleApi = TfLite.getClient(this)
        moduleInstallRequest =
            ModuleInstallRequest.newBuilder()
                .addApi(optionalModuleApi)
                .build()

        moduleInstallClient = ModuleInstall.getClient(this)
        optionalModuleApi = TfLite.getClient(this)

        scanner = GmsBarcodeScanning.getClient(this@MainActivity, options)
        viewMole = ViewModelProvider(this)[DBViewModle::class.java]
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    }


    private fun subscribeUi() {

        // display categories
        binding.categoryRecyclerView.adapter = CategoryAdapter(Categories.categoriesList) { selectedCategory ->

            binding.loading.visibility = View.VISIBLE
            filterData(selectedCategory) // Call the filtering function
        }

        // Inside the subscribeUi() function
        val itemTouchHelperCallbacks = object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT
        ) {
            private val background = ColorDrawable(Color.RED) // Red background for delete
            private val deleteIcon: Drawable? = ContextCompat.getDrawable(this@MainActivity, R.drawable.baseline_delete_24) // Replace with your delete icon
            private val iconMargin = resources.getDimension(R.dimen.icon_margin).toInt() // Define margin for the icon in dimens.xml

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val qrCode = adapter.getQrCodeAt(position)
                viewMole.deleteEntery(qrCode)

                Toast.makeText(this@MainActivity, getString(R.string.deleted), Toast.LENGTH_SHORT).show()
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView

                // Reset background and icon if swipe gesture is not active
                if (dX == 0f && !isCurrentlyActive) {
                    background.setBounds(0, 0, 0, 0)
                    deleteIcon?.setBounds(0, 0, 0, 0)
                } else {
                    // Draw background
                    if (dX > 0) { // Swiping to the right
                        background.setBounds(itemView.left, itemView.top, itemView.left + dX.toInt(), itemView.bottom)
                    } else { // Swiping to the left
                        background.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
                    }
                    background.draw(c)

                    // Draw delete icon
                    deleteIcon?.let {
                        val iconWidth = it.intrinsicWidth
                        val iconHeight = it.intrinsicHeight
                        val iconTop = itemView.top + (itemView.height - iconHeight) / 2
                        val iconBottom = iconTop + iconHeight
                        val iconLeft: Int
                        val iconRight: Int

                        if (dX > 0) { // Swiping to the right
                            iconLeft = itemView.left + iconMargin
                            iconRight = iconLeft + iconWidth
                        } else { // Swiping to the left
                            iconRight = itemView.right - iconMargin
                            iconLeft = iconRight - iconWidth
                        }

                        it.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                        it.draw(c)
                    }
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }

        }

// Attach ItemTouchHelper to RecyclerView
        ItemTouchHelper(itemTouchHelperCallbacks).apply {
            attachToRecyclerView(binding.rc)
        }
    }


    private fun filterData(selectedCategory: String) {
        viewMole.scannedQr.observe(this) { task ->
            val filteredTask = if (selectedCategory == Categories.ALL) {
                viewMole.scannedQr.value // No filter show "all" category
            } else {
                task.filter { it.category == selectedCategory } // Adjust this based on your data structure
            }

            adapter = ResultAdapter(this@MainActivity, filteredTask!!)
            binding.rc.adapter = adapter

            if (filteredTask.isNotEmpty()) {
                binding.clearAll.visibility = View.VISIBLE
                binding.noDataTextView.visibility = View.GONE
                binding.rc.visibility = View.VISIBLE
            } else {
                binding.clearAll.visibility = View.GONE
                binding.noDataTextView.visibility = View.VISIBLE
                binding.rc.visibility = View.GONE
            }
            binding.loading.visibility = View.GONE
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

    // function to scan qr code
    private fun scan() {
        scanner.startScan()
            .addOnSuccessListener { barcode ->

                // vibrate when qr code is scanned (haptic feedback)
                if (vibrator.hasVibrator()) {
                    val milliseconds = 150L
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

                Toast.makeText(this, "failure", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, ErrorActivity::class.java))
            }
    }

    // function to check if user is first time visitor
    private fun isFirstTimeVistor(sharedPref: SharedPreferences?): Boolean {
        return sharedPref!!.getBoolean(FIRST_TIME_VISTOR, true)
    }



}