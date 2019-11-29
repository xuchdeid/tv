package com.koalcat.tv

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Message
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.ProgressBar
import androidx.core.app.ActivityOptionsCompat
import androidx.leanback.app.BrandedFragment

class WebViewFragment : BrandedFragment() {

    private var mSelectedMovie: Movie? = null

    private lateinit var webView: WebView
    private lateinit var progress: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_webview, container, false)
        this.webView = view.findViewById(R.id.webview)
        this.progress = view.findViewById(R.id.progress)
        webView.settings?.cacheMode = WebSettings.LOAD_DEFAULT
        webView.settings?.javaScriptEnabled = true
        webView.settings?.domStorageEnabled = true
        webView.settings?.databaseEnabled = true
        webView.settings?.allowFileAccess = true
        webView.settings?.setAppCacheEnabled(true)
        webView.settings?.setSupportMultipleWindows(true)

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                val movie = Movie()
                movie.videoUrl = request?.url.toString()
                val intent = Intent(activity, DetailsActivity::class.java)
                intent.putExtra(DetailsActivity.MOVIE, movie)
                val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    activity,
                    webView,
                    DetailsActivity.SHARED_ELEMENT_NAME
                )
                    .toBundle()
                activity.startActivity(intent, bundle)
                return true
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                progress.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                progress.visibility = View.GONE
            }
        }

        webView.webChromeClient = object: WebChromeClient() {
            override fun onCreateWindow(
                view: WebView?,
                isDialog: Boolean,
                isUserGesture: Boolean,
                resultMsg: Message?
            ): Boolean {
                if (isDialog) return false
                val result = view?.hitTestResult
                val data = result?.extra
                val intent = Intent(activity, DetailsActivity::class.java)
                val movie = Movie()
                movie.videoUrl = data
                intent.putExtra(DetailsActivity.MOVIE, movie)

                val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    activity,
                    webView,
                    DetailsActivity.SHARED_ELEMENT_NAME
                )
                    .toBundle()
                activity.startActivity(intent, bundle)
                return true
            }
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setDesktopMode(true)
        view?.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (webView!!.canGoBack()) {
                    webView!!.goBack()
                    true
                } else {
                    false
                }
            } else {
                false
            }
        }
        mSelectedMovie = activity?.intent?.getSerializableExtra(DetailsActivity.MOVIE) as Movie
        if (mSelectedMovie != null) {
            webView?.loadUrl(mSelectedMovie?.videoUrl)
        } else {
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
        }
    }

    fun setDesktopMode(enabled: Boolean) {
        val webSettings: WebSettings = webView.settings
        val newUserAgent: String
        newUserAgent = if (enabled) {
            webSettings.userAgentString.replace("Mobile", "eliboM")
                .replace("Android", "diordnA")
        } else {
            webSettings.userAgentString.replace("eliboM", "Mobile")
                .replace("diordnA", "Android")
        }
        webSettings.userAgentString = newUserAgent
        webSettings.useWideViewPort = enabled
        webSettings.loadWithOverviewMode = enabled
        webSettings.setSupportZoom(enabled)
        webSettings.builtInZoomControls = enabled
    }

}