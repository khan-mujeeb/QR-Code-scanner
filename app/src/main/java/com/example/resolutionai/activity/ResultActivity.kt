package com.example.resolutionai.activity

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.webkit.URLUtil
import android.content.ClipboardManager
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.resolutionai.database.data.QrCodeEntity
import com.example.resolutionai.database.viewmodel.DBViewModle
import com.example.resolutionai.R
import com.example.resolutionai.databinding.ActivityResultBinding


class ResultActivity : AppCompatActivity() {
    lateinit var viewMole: DBViewModle
    lateinit var binding: ActivityResultBinding
    var cat = ""
    var result = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    override fun onStart() {
        super.onStart()
        variableInit()
        subscribeUi()
        addToDb()
        subscribeClickEvents()
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

            } else {
                copyTextToClipboard(this@ResultActivity, result)
                Toast.makeText(this, getString(R.string.copied), Toast.LENGTH_SHORT).show()

            }
        }

        binding.share.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
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
        binding.logo.setImageDrawable(resources.getDrawable(R.drawable.plain_text))
        binding.category.text = getString(R.string.qr_code)
        binding.btn.text = getString(R.string.copy_text)
    }

    private fun setUpUrlData() {
        binding.logo.setImageDrawable(resources.getDrawable(R.drawable.browser_new))
        binding.category.text = getString(R.string.website)
        binding.btn.text = getString(R.string.go_to_website)
    }

    private fun variableInit() {

        result = intent.getStringExtra("result")!!
        cat = isPlainTextOrUrl(result)

        viewMole = ViewModelProvider(this)[DBViewModle::class.java]

    }

    fun isPlainTextOrUrl(input: String): String {
        if (URLUtil.isValidUrl(input)) return "url"
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