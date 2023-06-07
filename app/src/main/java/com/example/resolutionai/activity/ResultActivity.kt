package com.example.resolutionai.activity

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.resolutionai.databinding.ActivityResultBinding
import android.webkit.URLUtil
import com.example.resolutionai.R
import android.content.ClipboardManager
import android.content.Intent
import android.net.Uri
import android.widget.Toast


class ResultActivity : AppCompatActivity() {
    lateinit var binding: ActivityResultBinding
    var result = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        variableInit()
        subscribeUi()
        subscribeClickEvents()

    }

    private fun subscribeClickEvents() {
        binding.btn.setOnClickListener {
            if (isPlainTextOrUrl(result)) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(result))
                    startActivity(intent)

            } else {
                copyTextToClipboard(this@ResultActivity, result)
            }
        }
    }

    fun copyTextToClipboard(context: Context, text: String) {
        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("Copied Text", text)
        clipboardManager.setPrimaryClip(clipData)
        Toast.makeText(this, "copied", Toast.LENGTH_SHORT).show()
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

        if (isPlainTextOrUrl(result)) {
            setUpUrlData()
        } else {
            setUpPlainTextData()
        }
    }

    private fun setUpPlainTextData() {
        binding.logo.setImageDrawable(resources.getDrawable(R.drawable.baseline_format_color_text_24))
        binding.category.text = getString(R.string.qr_code)
        binding.btn.text = getString(R.string.copy_text)
    }

    private fun setUpUrlData() {
        binding.logo.setImageDrawable(resources.getDrawable(R.drawable.browser))
        binding.category.text = getString(R.string.website)
        binding.btn.text = getString(R.string.go_to_website)
    }

    private fun variableInit() {
        result = intent.getStringExtra("result")!!
    }

    fun isPlainTextOrUrl(input: String): Boolean {
        return URLUtil.isValidUrl(input)
    }
}