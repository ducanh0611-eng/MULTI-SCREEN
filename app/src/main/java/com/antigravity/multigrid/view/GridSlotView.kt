package com.antigravity.multigrid.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.webkit.*
import android.widget.*
import com.antigravity.multigrid.R
import com.antigravity.multigrid.dialog.AppPickerDialog
import com.antigravity.multigrid.model.GridSlotData

@SuppressLint("SetJavaScriptEnabled", "ViewConstructor")
class GridSlotView(
    context: Context,
    val slotData: GridSlotData,
    private val onStateChanged: (GridSlotData) -> Unit,
    private val onToggleMaximize: (GridSlotView) -> Unit
) : FrameLayout(context) {

    private val tvTitle: TextView
    private val btnBack: ImageButton
    private val btnRefresh: ImageButton
    private val btnMute: ImageButton
    private val btnChangeApp: ImageButton
    private val btnMaximize: ImageButton
    private val progressBar: ProgressBar
    private val webViewContainer: FrameLayout
    val webView: WebView

    var isMaximized: Boolean = false
        set(value) {
            field = value
            btnMaximize.setImageResource(if (value) R.drawable.ic_expand else R.drawable.ic_expand)
        }

    init {
        LayoutInflater.from(context).inflate(R.layout.view_grid_slot, this, true)

        tvTitle = findViewById(R.id.tvSlotTitle)
        btnBack = findViewById(R.id.btnSlotBack)
        btnRefresh = findViewById(R.id.btnSlotRefresh)
        btnMute = findViewById(R.id.btnSlotMute)
        btnChangeApp = findViewById(R.id.btnSlotChangeApp)
        btnMaximize = findViewById(R.id.btnSlotMaximize)
        progressBar = findViewById(R.id.slotProgressBar)
        webViewContainer = findViewById(R.id.slotWebViewContainer)

        tvTitle.text = slotData.title

        // Create and configure WebView
        webView = WebView(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            setupWebViewSettings(this)
        }
        webViewContainer.addView(webView)

        setupEventListeners()
        updateMuteState(slotData.isMuted)

        // Load saved URL
        if (slotData.url.isNotEmpty()) {
            webView.loadUrl(slotData.url)
        }
    }

    private fun setupWebViewSettings(wv: WebView) {
        wv.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            databaseEnabled = true
            allowFileAccess = true
            useWideViewPort = true
            loadWithOverviewMode = true
            builtInZoomControls = true
            displayZoomControls = false
            mediaPlaybackRequiresUserGesture = false
            cacheMode = WebSettings.LOAD_DEFAULT
            userAgentString = "Mozilla/5.0 (Linux; Android 14; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Mobile Safari/537.36"
        }

        CookieManager.getInstance().apply {
            setAcceptCookie(true)
            setAcceptThirdPartyCookies(wv, true)
        }

        wv.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                progressBar.visibility = View.VISIBLE
                if (url != null && url.startsWith("http")) {
                    slotData.url = url
                    onStateChanged(slotData)
                }
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                progressBar.visibility = View.GONE
                if (slotData.isMuted) {
                    applyMuteToMedia(true)
                }
                if (url != null && url.startsWith("http")) {
                    slotData.url = url
                    onStateChanged(slotData)
                }
            }
        }

        wv.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                progressBar.progress = newProgress
                if (newProgress >= 100) {
                    progressBar.visibility = View.GONE
                }
            }

            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
                if (!title.isNullOrEmpty() && !title.startsWith("http")) {
                    tvTitle.text = "${slotData.slotIndex + 1}. $title"
                    slotData.title = title
                    onStateChanged(slotData)
                }
            }
        }
    }

    private fun setupEventListeners() {
        btnBack.setOnClickListener {
            if (webView.canGoBack()) {
                webView.goBack()
            }
        }

        btnRefresh.setOnClickListener {
            webView.reload()
        }

        btnMute.setOnClickListener {
            val newMute = !slotData.isMuted
            updateMuteState(newMute)
            slotData.isMuted = newMute
            onStateChanged(slotData)
        }

        btnChangeApp.setOnClickListener {
            AppPickerDialog(context, "Ngăn ${slotData.slotIndex + 1}") { newUrl, appName ->
                loadUrl(newUrl, appName)
            }.show()
        }

        btnMaximize.setOnClickListener {
            onToggleMaximize(this)
        }
    }

    fun loadUrl(url: String, appName: String) {
        slotData.url = url
        slotData.title = appName
        tvTitle.text = "${slotData.slotIndex + 1}. $appName"
        webView.loadUrl(url)
        onStateChanged(slotData)
    }

    private fun updateMuteState(muted: Boolean) {
        btnMute.setImageResource(if (muted) R.drawable.ic_volume_off else R.drawable.ic_volume_up)
        applyMuteToMedia(muted)
    }

    private fun applyMuteToMedia(muted: Boolean) {
        val js = """
            (function() {
                var media = document.querySelectorAll('video, audio');
                for (var i = 0; i < media.length; i++) {
                    media[i].muted = $muted;
                }
            })();
        """.trimIndent()
        webView.evaluateJavascript(js, null)
    }

    fun reload() {
        webView.reload()
    }

    fun destroySlot() {
        webView.stopLoading()
        webViewContainer.removeView(webView)
        webView.destroy()
    }
}
