package com.nyxcorp.nyxpet

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.KeyEvent
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.webkit.WebViewAssetLoader

/**
 * NyxPet itself is a web page bundled in assets/www (HTML, CSS, JavaScript).
 * This activity shows it full screen and gives it a small bridge ("NyxNative")
 * for button buzzes, the Back button, and "Nyx needs you" reminders.
 * Nyx's save lives in the web view's storage, so it survives app updates.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var web: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val assets = WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(this))
            .build()

        web = WebView(this).apply {
            setBackgroundColor(Color.parseColor("#15121D"))
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true            // Nyx's save
            settings.mediaPlaybackRequiresUserGesture = false
            settings.allowFileAccess = false
            settings.textZoom = 100
            isHapticFeedbackEnabled = true
            addJavascriptInterface(Bridge(), "NyxNative")
            webViewClient = object : WebViewClient() {
                override fun shouldInterceptRequest(view: WebView, request: WebResourceRequest): WebResourceResponse? =
                    assets.shouldInterceptRequest(request.url)
            }
        }
        setContentView(web)
        hideSystemBars()
        web.loadUrl("https://appassets.androidplatform.net/assets/www/index.html")
        askForNotificationsOnce()
    }

    private fun askForNotificationsOnce() {
        if (Build.VERSION.SDK_INT < 33) return
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) return
        val prefs = getSharedPreferences(Reminders.PREFS, MODE_PRIVATE)
        if (prefs.getBoolean("askedNotify", false)) return
        prefs.edit().putBoolean("askedNotify", true).apply()
        requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 7)
    }

    private fun js(code: String) = web.evaluateJavascript("window.nyx&&$code", null)

    private fun hideSystemBars() {
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemBars()
    }

    /** Android's Back button / gesture acts like the toy's C button. */
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_BACK) {
            if (event.action == KeyEvent.ACTION_UP) js("nyx.back()")
            return true
        }
        return super.dispatchKeyEvent(event)
    }

    override fun onResume() {
        super.onResume()
        hideSystemBars()
        web.onResume()
        web.resumeTimers()
        js("nyx.resume()")      // catches Nyx up on the time you were away, clears the pending reminder
    }

    override fun onPause() {
        js("nyx.pause()")       // saves Nyx and schedules the next "Nyx needs you" reminder
        web.onPause()
        web.pauseTimers()
        super.onPause()
    }

    /** Methods the web page can call. */
    inner class Bridge {
        @JavascriptInterface
        fun haptic() {
            runOnUiThread { web.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY) }
        }

        @JavascriptInterface
        fun remind(at: Double, title: String, text: String) {
            Reminders.schedule(applicationContext, at.toLong(), title, text)
        }

        @JavascriptInterface
        fun cancelReminder() {
            Reminders.cancel(applicationContext)
        }

        @JavascriptInterface
        fun exit() {
            runOnUiThread { moveTaskToBack(true) }
        }
    }
}
