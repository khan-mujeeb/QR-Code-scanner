package com.khandev.qrcodescanner.utlis

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

object ScannerUtils {

    // function to copt text to clipboard
    fun copyTextToClipboard(context: Context, text: String) {
        val clipboardManager =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("Copied Text", text)
        clipboardManager.setPrimaryClip(clipData)
    }

}