package com.example.waorsms

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/**
 * Single-screen "shortcut" app.
 *
 * Flow:
 *  1. Copy a phone number to the clipboard anywhere in Android.
 *  2. Tap this app's icon on the home screen.
 *  3. The number is read from the clipboard and shown here, along with
 *     three editable message fields (Telugu / Hindi / English).
 *  4. Tap the button for whichever language applies. That fires the
 *     WhatsApp deep link first, then immediately opens the SMS compose
 *     screen for the same number and message - so one tap queues up both
 *     sends, one after the other.
 *
 * There is no reliable public API to check "does this number have
 * WhatsApp" before messaging it, so both channels are sent every time
 * rather than trying to auto-detect which one is needed.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var numberField: EditText
    private var clipboardChecked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        numberField = findViewById(R.id.numberField)
        val teluguField = findViewById<EditText>(R.id.teluguField)
        val hindiField = findViewById<EditText>(R.id.hindiField)
        val englishField = findViewById<EditText>(R.id.englishField)

        teluguField.setText(getString(R.string.message_telugu))
        hindiField.setText(getString(R.string.message_hindi))
        englishField.setText(getString(R.string.message_english))

        findViewById<Button>(R.id.teluguButton).setOnClickListener {
            sendBoth(numberField.text.toString(), teluguField.text.toString())
        }
        findViewById<Button>(R.id.hindiButton).setOnClickListener {
            sendBoth(numberField.text.toString(), hindiField.text.toString())
        }
        findViewById<Button>(R.id.englishButton).setOnClickListener {
            sendBoth(numberField.text.toString(), englishField.text.toString())
        }
        findViewById<Button>(R.id.closeButton).setOnClickListener { finish() }
    }

    /**
     * Since Android 10, an app can only read the clipboard once its window has
     * actually gained focus - reading it in onCreate()/onResume() can be a moment
     * too early and silently return nothing. onWindowFocusChanged(true) is the
     * reliable point to do it.
     */
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus && !clipboardChecked) {
            clipboardChecked = true
            numberField.setText(readNumberFromClipboard())
        }
    }

    /**
     * Reads the current clipboard text and normalizes it into a phone number:
     * strips everything but digits (and a leading "+"), and - for numbers with
     * no "+" that start with a leading 0 (e.g. "063024 79054") - drops that
     * leading 0 and prefixes "91" instead (e.g. "916302479054").
     */
    private fun readNumberFromClipboard(): String {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        if (!clipboard.hasPrimaryClip()) return ""
        val clip = clipboard.primaryClip ?: return ""
        if (clip.itemCount == 0) return ""
        val raw = clip.getItemAt(0).coerceToText(this).toString().trim()
        val hasPlus = raw.startsWith("+")
        val digits = raw.filter { it.isDigit() }
        return when {
            hasPlus -> "+$digits"
            digits.startsWith("0") -> "91${digits.removePrefix("0")}"
            else -> digits
        }
    }

    /** Fires the WhatsApp send intent, then the SMS compose intent, one after the other. */
    private fun sendBoth(number: String, message: String) {
        if (number.isBlank()) {
            Toast.makeText(this, "No phone number found", Toast.LENGTH_SHORT).show()
            return
        }
        sendWhatsApp(number, message)
        sendSms(number, message)
    }

    /**
     * Opens WhatsApp directly to a chat with [number], with [message] pre-filled.
     * Uses the standard wa.me deep link, which requires the number in international
     * format with no "+" or leading zeros (e.g. 919876543210). If the number has no
     * WhatsApp account, WhatsApp itself shows that error - there's no way for this
     * app to detect that in advance.
     */
    private fun sendWhatsApp(number: String, message: String) {
        val clean = number.filter { it.isDigit() }
        val uri = Uri.parse("https://wa.me/$clean?text=${Uri.encode(message)}")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        try {
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Could not open WhatsApp", Toast.LENGTH_SHORT).show()
        }
    }

    /** Opens the default SMS app with [number] and [message] pre-filled for the user to send. */
    private fun sendSms(number: String, message: String) {
        val uri = Uri.parse("smsto:$number")
        val intent = Intent(Intent.ACTION_SENDTO, uri).apply {
            putExtra("sms_body", message)
        }
        try {
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "No SMS app found", Toast.LENGTH_SHORT).show()
        }
    }
}
