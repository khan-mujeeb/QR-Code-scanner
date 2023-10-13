package com.khandev.qrcodescanner.activity

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.URLUtil
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.khandev.qrcodescanner.R
import com.khandev.qrcodescanner.database.data.QrCodeEntity
import com.khandev.qrcodescanner.database.viewmodel.DBViewModle
import com.khandev.qrcodescanner.databinding.ActivityResultBinding
import com.khandev.qrcodescanner.helper.isValidEmail


class ResultActivity : AppCompatActivity() {
    lateinit var viewMole: DBViewModle
    lateinit var binding: ActivityResultBinding
    var cat = ""
    var result = ""
    var count = -1
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
            if (cat == "url") {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(result))
                startActivity(intent)

            } else if(cat =="email") {

                val intent = Intent(Intent.ACTION_SENDTO)
                intent.data = Uri.parse("mailto:") // only email apps should handle this

                intent.putExtra(Intent.EXTRA_EMAIL, result)
                intent.putExtra(Intent.EXTRA_SUBJECT, "Subject:")
                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                }


                copyTextToClipboard(this@ResultActivity, result)
                Toast.makeText(this, getString(R.string.copied), Toast.LENGTH_SHORT).show()
            }
            else{
                copyTextToClipboard(this@ResultActivity, result)
                Toast.makeText(this, getString(R.string.copied), Toast.LENGTH_SHORT).show()
            }
        }

        binding.share.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, result)
            startActivity(Intent.createChooser(intent, "Share via"))
        }

        binding.result.setOnLongClickListener {
            copyTextToClipboard(this, result)
            Toast.makeText(this, getString(R.string.copied), Toast.LENGTH_SHORT).show()
            true
        }
    }


    @SuppressLint("SetTextI18n")
    private fun subscribeUi() {
        backPressed()
        displayBody()
        binding.result.text = result
    }

    private fun backPressed() {

        val toolbar = binding.toolbar

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun displayBody() {

        if (cat == "url") {
            setUpUrlData()
        } else {
            setUpPlainTextData()
        }
    }

    private fun setUpPlainTextData() {
        binding.apply {
            logo.setImageDrawable(resources.getDrawable(R.drawable.plain_text))
            category.text = getString(R.string.qr_code)
            btn.text = getString(R.string.copy_text)
            result.setTextColor(ContextCompat.getColor(this@ResultActivity, R.color.black))
        }


    }

    private fun setUpUrlData() {
        binding.apply {
            logo.setImageDrawable(resources.getDrawable(R.drawable.browser_new))
            category.text = getString(R.string.website)
            btn.text = getString(R.string.go_to_website)
            result.setTextColor(ContextCompat.getColor(this@ResultActivity, R.color.midnight_blue))
        }
    }

    private fun variableInit() {
        count = intent.getIntExtra("count", -1)!!
        result = intent.getStringExtra("result")!!
        cat = isPlainTextOrCategory(result)

        viewMole = ViewModelProvider(this)[DBViewModle::class.java]

    }

    fun isPlainTextOrCategory(input: String): String {

        if (URLUtil.isValidUrl(input)) return "url"

        else if(input.isValidEmail()) return "email"

        else return "text"
    }




    companion object {
        fun copyTextToClipboard(context: Context, text: String) {
            val clipboardManager =
                context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("Copied Text", text)
            clipboardManager.setPrimaryClip(clipData)
        }
    }
}