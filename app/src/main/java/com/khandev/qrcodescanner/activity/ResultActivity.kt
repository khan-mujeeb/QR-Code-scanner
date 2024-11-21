package com.khandev.qrcodescanner.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentProviderOperation
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.khandev.qrcodescanner.R
import com.khandev.qrcodescanner.adapter.VCardDataAdapter
import com.khandev.qrcodescanner.adapter.WifiDataAdapter
import com.khandev.qrcodescanner.data.Permission
import com.khandev.qrcodescanner.data.UpiData
import com.khandev.qrcodescanner.data.WifiConfig
import com.khandev.qrcodescanner.database.data.QrCodeEntity
import com.khandev.qrcodescanner.database.viewmodel.DBViewModle
import com.khandev.qrcodescanner.databinding.ActivityResultBinding
import com.khandev.qrcodescanner.utlis.Categories
import com.khandev.qrcodescanner.utlis.PermissionHandler
import com.khandev.qrcodescanner.utlis.QrCodeParser
import com.khandev.qrcodescanner.utlis.QrCodeParser.parseVCard
import com.khandev.qrcodescanner.utlis.ScannerUtils
import com.khandev.qrcodescanner.utlis.ScannerUtils.copyTextToClipboard
import com.khandev.qrcodescanner.utlis.ScannerUtils.getCateogory


class ResultActivity : AppCompatActivity() {
    private lateinit var viewMole: DBViewModle
    private lateinit var binding: ActivityResultBinding
    private var vcardResult: Map<String, String>? = null
    private var cat = ""
    private var result = ""
    private var count = 0
    private var phoneNumber = ""
    private var wifiData: WifiConfig? = null
    private var upiData: UpiData? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        variableInit()

    }

    override fun onStart() {
        super.onStart()
        subscribeUi()
        subscribeClickEvents()
        if (count == 0) {
            addToDb()
            count++
        }
    }

    private fun addToDb() {
        viewMole.insert(
            QrCodeEntity(
                qrcodeData = result,
                category = cat
            )
        )
    }

    @SuppressLint("SuspiciousIndentation")
    private fun subscribeClickEvents() {

        binding.btn.setOnClickListener {
            when (cat) {
                Categories.URL -> {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(result))
                    startActivity(intent)

                }
                Categories.CONTACT -> {
                    makeAPhoneCall(result)
                }

                Categories.WIFI -> {
                    ScannerUtils.copyTextToClipboard(this, wifiData!!.password!!)
                }

                Categories.PAYMENT -> {
                    ScannerUtils.copyTextToClipboard(this, upiData!!.upiId!!)
                }

                else -> {
                    copyTextToClipboard(this@ResultActivity, result)
                    Toast.makeText(this, getString(R.string.copied), Toast.LENGTH_SHORT).show()

                }
            }
        }

        binding.share.setOnClickListener {
            when (cat) {
                Categories.CONTACT -> {
                    saveContact(vcardResult!!)
                }

                Categories.PAYMENT -> {
                    ScannerUtils.handleUpiPayment(this, upiData!!)
                }

                Categories.WIFI -> {
                    if (wifiData != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            ScannerUtils.connectToWifi(this, wifiData!!)
                        } else {
                            Toast.makeText(this, "This feature is only available on Android 10 and above", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "Invalid Wi-Fi QR code", Toast.LENGTH_SHORT).show()
                    }
                }
                else -> {
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.type = "text/plain"
                    intent.putExtra(Intent.EXTRA_TEXT, result)
                    startActivity(Intent.createChooser(intent, "Share via"))
                }
            }

        }

        binding.result.setOnLongClickListener {
            copyTextToClipboard(this, result)
            Toast.makeText(this, getString(R.string.copied), Toast.LENGTH_SHORT).show()
            true
        }
    }

    private fun makeAPhoneCall(result: String) {
        val dialIntent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }
        startActivity(dialIntent)

    }


    @SuppressLint("SetTextI18n")
    private fun subscribeUi() {
        backPressed()
        displayBody()
        if(result != Categories.CONTACT) {
            binding.result.text = result
        } else {
            binding.ResultRc.adapter = VCardDataAdapter(vcardResult!!)
        }
    }


    // *********************************************************************************************
    //                   This function is used to handle the back button press
    // *********************************************************************************************
    private fun backPressed() {

        val toolbar = binding.toolbar

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    // *********************************************************************************************
    //                                   Display info according to category
    // *********************************************************************************************
    private fun displayBody() {
        when (cat) {
            Categories.CONTACT -> {
                setUpContactData()
                if (phoneNumber.isEmpty()) binding.btn.isClickable = false
            }
            Categories.URL -> {
                setUpUrlData()
            }

            Categories.WIFI -> {
                setUpWifiData()
            }

            Categories.PAYMENT -> {
                setUpUpiData()
            }

            else -> {
                setUpPlainTextData()
            }
        }
    }

    // *********************************************************************************************
    //                                   SetUP Data According to Category
    // *********************************************************************************************


    private fun setUpUpiData() {
        binding.apply {
            logo.setImageDrawable(resources.getDrawable(R.drawable.upi_icon))
            category.text = getString(R.string.upi_payment)

            btn.text = getString(R.string.copy_text)
            share.text = getString(R.string.make_payment)

            ResultRc.visibility = android.view.View.VISIBLE
            result.visibility = android.view.View.GONE

            val upiMap = mutableMapOf<String, String>()
            upiMap["UPI ID"] = upiData!!.upiId.toString()
            upiMap["Name"] = upiData!!.payeeName.toString()
            upiMap["Amount"] = upiData!!.amount.toString()
            upiMap["Currency"] = upiData!!.currency.toString()
            upiMap["Note"] = upiData!!.transactionNote.toString()
            upiMap["Transaction ID"] = upiData!!.transactionRefId.toString()
            upiMap["Merchant Code"] = upiData!!.merchantCode.toString()
            upiMap["url"] = upiData!!.url.toString()

            ResultRc.adapter = WifiDataAdapter(upiMap.toList())

        }
    }

    private fun setUpWifiData() {
        binding.apply {
            logo.setImageDrawable(resources.getDrawable(R.drawable.wifi_logo))
            category.text = getString(R.string.wifi)

            btn.text = getString(R.string.copy_text)
            share.text = "Connect"

            ResultRc.visibility = android.view.View.VISIBLE
            result.visibility = android.view.View.GONE



            val wifiMap = mutableMapOf<String, String>()

            wifiMap["SSID"] = wifiData!!.ssid.toString()
            wifiMap["Password"] = wifiData!!.password!!.toString()
            wifiMap["Security"] = wifiData!!.encryption!!.toString()
            wifiMap["Hidden"] = wifiData!!.hidden.toString()





            ResultRc.adapter = WifiDataAdapter(wifiMap.toList())
        }
    }
    private fun setUpContactData() {
        binding.apply {
            logo.setImageDrawable(resources.getDrawable(R.drawable.baseline_contact_phone_24))
            category.text = getString(R.string.contact)
            btn.text = getString(R.string.copy_text)
            result.visibility = android.view.View.GONE
            ResultRc.visibility = android.view.View.VISIBLE

            // setting up the contact data
            ResultRc.adapter = VCardDataAdapter(vcardResult!!)

            share.text = getString(R.string.save_contact)
            btn.text = getString(R.string.call)
        }
    }

    private fun setUpPlainTextData() {
        binding.apply {
            logo.setImageDrawable(resources.getDrawable(R.drawable.plain_text))
            category.text = getString(R.string.qr_code)
            btn.text = getString(R.string.copy_text)
            result.setTextColor(ContextCompat.getColor(this@ResultActivity, R.color.black))
            result.visibility = android.view.View.VISIBLE
            ResultRc.visibility = android.view.View.GONE
        }


    }

    private fun setUpUrlData() {
        binding.apply {
            logo.setImageDrawable(resources.getDrawable(R.drawable.browser_new))
            category.text = getString(R.string.website)
            btn.text = getString(R.string.go_to_website)
            result.setTextColor(ContextCompat.getColor(this@ResultActivity, R.color.midnight_blue))
            result.visibility = android.view.View.VISIBLE
            ResultRc.visibility = android.view.View.GONE
        }
    }


    // *********************************************************************************************
    //                                   Variable Initialization
    // *********************************************************************************************
    private fun variableInit() {

        result = intent.getStringExtra("result")!!
        viewMole = ViewModelProvider(this)[DBViewModle::class.java]


        cat = getCateogory(result)

        if (cat == Categories.CONTACT) {
            vcardResult = parseVCard(result)
            phoneNumber = vcardResult!!["Phone Number"] ?: vcardResult!!["Mobile Number"] ?: ""
        } else if(cat == Categories.WIFI) {
            wifiData = QrCodeParser.parseWifiQRCode(result)
        } else if(cat == Categories.PAYMENT) {
            upiData = QrCodeParser.parseUpiQrCode(result)

        }

    }




    // *********************************************************************************************
    //                                   Save Contact
    // *********************************************************************************************

    private fun saveContact(vCardData: Map<String, String>) {

        val permissionsList = listOf(
            Permission(Manifest.permission.WRITE_CONTACTS, 101) {

            },
        )

        PermissionHandler.checkAndRequestPermissions(this, permissionsList)

        val contentResolver = contentResolver
        val operations = ArrayList<ContentProviderOperation>()

        // Insert a raw contact (required step)
        operations.add(
            ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build()
        )

        // Insert Name
        vCardData["Full Name"]?.let { fullName ->
            operations.add(
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, fullName)
                    .build()
            )
        }

        // Insert Phone Number
        vCardData["Mobile No."]?.let { mobile ->
            operations.add(
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, mobile)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                    .build()
            )
        }

        // Insert Email
        vCardData["Email"]?.let { email ->
            operations.add(
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Email.ADDRESS, email)
                    .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                    .build()
            )
        }

        // Insert Address
        vCardData["Address"]?.let { address ->
            operations.add(
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS, address)
                    .build()
            )
        }

        // Insert Organization
        vCardData["Organization"]?.let { organization ->
            operations.add(
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Organization.COMPANY, organization)
                    .build()
            )
        }

        // Insert Website
        vCardData["website"]?.let { website ->
            operations.add(
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Website.URL, website)
                    .build()
            )
        }

        try {
            // Apply the batch insert
            contentResolver.applyBatch(ContactsContract.AUTHORITY, operations)
            Toast.makeText(this, "Contact saved successfully", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to save contact", Toast.LENGTH_SHORT).show()
        }
    }


    // *********************************************************************************************
    // Override onRequestPermissionsResult to pass it to PermissionHandler
    // *********************************************************************************************
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionHandler.onRequestPermissionsResult(this, requestCode, permissions, grantResults)
    }

}