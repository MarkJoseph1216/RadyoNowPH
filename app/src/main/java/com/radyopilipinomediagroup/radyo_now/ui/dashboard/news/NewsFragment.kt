package com.radyopilipinomediagroup.radyo_now.ui.dashboard.news

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.radyopilipinomediagroup.radyo_now.R
import com.radyopilipinomediagroup.radyo_now.ui.AbstractPresenter


class NewsFragment : Fragment(), AbstractPresenter.ContextView<FragmentActivity> , NewsPresenter.View{

    var newsView : View? = null
    var presenter: NewsPresenter? = null
    var newsWebView: WebView? = null
    var noInternet: TextView? = null
    var webClient = object: WebViewClient() {
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            noInternet?.visibility = View.GONE
        }
        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError?
        ) {
            super.onReceivedError(view, request, error)
            noInternet?.visibility = View.VISIBLE
            newsWebView?.visibility = View.GONE
        }
        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            return false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        newsView = inflater.inflate(R.layout.fragment_news, container, false)
        initInitialize()
        initComponents()
        return newsView
    }

    private fun initInitialize() {
        noInternet = newsView?.findViewById(R.id.noInternet)
        newsWebView = newsView?.findViewById(R.id.newsWebView)
        newsWebView?.settings?.loadWithOverviewMode = true
        newsWebView?.settings?.allowContentAccess = true
        newsWebView?.settings?.loadsImagesAutomatically = true
        newsWebView?.settings?.useWideViewPort = true
        newsWebView?.settings?.javaScriptEnabled = true
        newsWebView?.settings?.domStorageEnabled = true
        newsWebView?.settings?.loadsImagesAutomatically = true
        newsWebView?.webViewClient = webClient
        presenter = NewsPresenter(this)
    }

    private fun initComponents() {
        newsWebView?.loadUrl("https://news.radyopilipino.com/")
        presenter?.appBarHide()
    }

    override fun activity(): FragmentActivity {
        return activity!!
    }

    override fun context(): Context {
        return context!!
    }

    override fun applicationContext(): Context {
       return activity().applicationContext
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.appBarShow()
    }

    override fun onPause() {
        super.onPause()
        presenter?.appBarShow()
    }

    override fun onResume() {
        super.onResume()
        presenter?.appBarHide()
    }

    override fun onStop() {
        super.onStop()
        presenter?.appBarShow()
    }

}