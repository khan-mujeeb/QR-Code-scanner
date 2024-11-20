package com.khandev.qrcodescanner.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentProviderOperation
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.webkit.URLUtil
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.khandev.qrcodescanner.R
import com.khandev.qrcodescanner.adapter.VCardDataAdapter
import com.khandev.qrcodescanner.data.Permission
import com.khandev.qrcodescanner.database.data.QrCodeEntity
import com.khandev.qrcodescanner.database.viewmodel.DBViewModle
import com.khandev.qrcodescanner.databinding.ActivityResultBinding
import com.khandev.qrcodescanner.utlis.Categories
import com.khandev.qrcodescanner.utlis.PermissionHandler
import com.khandev.qrcodescanner.utlis.ScannerUtils.copyTextToClipboard


class ResultActivity : AppCompatActivity() {
    private lateinit var viewMole: DBViewModle
    private lateinit var binding: ActivityResultBinding
    private var vcardResult: Map<String, String>? = null
    private var cat = ""
    private var result = ""
    private var count = -1
    private var phoneNumber = ""
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


    // This function is used to handle the back button press
    private fun backPressed() {

        val toolbar = binding.toolbar

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    //
    private fun displayBody() {
        when (cat) {
            Categories.CONTACT -> {
                setUpContactData()
                if (phoneNumber.isEmpty()) binding.btn.isClickable = false
            }
            Categories.URL -> {
                setUpUrlData()
            }
            else -> {
                setUpPlainTextData()
            }
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

    private fun variableInit() {
        count = intent.getIntExtra("count", -1)!!
        result = intent.getStringExtra("result")!!
        cat = getCateogory(result)
        viewMole = ViewModelProvider(this)[DBViewModle::class.java]

        if (cat == "contact") {
            vcardResult = parseVCard(result)
        }

    }

    // function to get the category of the input
     private fun getCateogory(input: String): String {

         val temp = input.toLowerCase()

         return if (temp.contains("vcard")) {
             Categories.CONTACT
         } else {
             if(URLUtil.isValidUrl(input)) "url" else "text"
         }
    }

    // function to extract contact info from vcard string and return as a map
    private fun parseVCard(vCardString: String): Map<String, String> {

        val lines = vCardString.split("\n")
        val vCardDataMap = mutableMapOf<String, String>()

        for (line in lines) {
            when {
                line.startsWith("FN:") -> vCardDataMap["Full Name"] = line.substringAfter("FN:").trim()
                line.startsWith("ORG:") -> vCardDataMap["Organization"] = line.substringAfter("ORG:").trim()
                line.startsWith("TITLE:") -> vCardDataMap["Title"] = line.substringAfter("TITLE:").trim()
                line.startsWith("TEL;WORK;VOICE:") -> vCardDataMap["Phone No."] = line.substringAfter("TEL;WORK;VOICE:").trim()
                line.startsWith("TEL;CELL:") -> vCardDataMap["Mobile No."] = line.substringAfter("TEL;CELL:").trim()
                line.startsWith("EMAIL;WORK;INTERNET:") -> vCardDataMap["Email"] = line.substringAfter("EMAIL;WORK;INTERNET:").trim()
                line.startsWith("URL:") -> vCardDataMap["website"] = line.substringAfter("URL:").trim()
//                line.startsWith("ADR:") -> vCardDataMap["Address"] = line.substringAfter("ADR:").trim()
                line.startsWith("TEL;FAX:") -> vCardDataMap["Fax"] = line.substringAfter("TEL;FAX:").trim()

                // Handle the ADR (Address) field
                line.startsWith("ADR:") -> {
                    val addressParts = line.substringAfter("ADR:").split(";").map { it.trim() }
                    val addressMap = mutableMapOf(
                        "Street" to (addressParts.getOrNull(2) ?: ""),
                        "City" to (addressParts.getOrNull(3) ?: ""),
                        "State" to (addressParts.getOrNull(4) ?: ""),
                        "Postal Code" to (addressParts.getOrNull(5) ?: ""),
                        "Country" to (addressParts.getOrNull(6) ?: "")
                    )
                    // Combine address parts into a human-readable format
                    vCardDataMap["Address"] = addressMap.entries
                        .filter { it.value.isNotBlank() } // Exclude empty values
                        .joinToString(", ") { it.value }
                }


            }
        }


        phoneNumber = if (vCardDataMap["Phone No."]!!.isNotEmpty()) {
            vCardDataMap["Phone No."]!!
        } else {
            vCardDataMap["Mobile No."]!!
        }

        return vCardDataMap
    }

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

    // Override onRequestPermissionsResult to pass it to PermissionHandler
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionHandler.onRequestPermissionsResult(this, requestCode, permissions, grantResults)
    }

}