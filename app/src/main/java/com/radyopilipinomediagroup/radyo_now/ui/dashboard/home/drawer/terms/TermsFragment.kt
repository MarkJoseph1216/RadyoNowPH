package com.radyopilipinomediagroup.radyo_now.ui.dashboard.home.drawer.terms

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.radyopilipinomediagroup.radyo_now.R
import com.radyopilipinomediagroup.radyo_now.ui.AbstractPresenter
import com.radyopilipinomediagroup.radyo_now.utils.Services


class TermsFragment : Fragment(), AbstractPresenter.ContextView<FragmentActivity>, TermsPresenter.View {

    private var termsView : View? = null
    private var termsWebView: WebView? = null
    private var presenter: TermsPresenter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        termsView = inflater.inflate(R.layout.fragment_terms, container, false)
        initInitialize()
        initComponents()
        return termsView
    }

    private fun initInitialize() {
        termsWebView = termsView?.findViewById(R.id.termsWebView)
        presenter = TermsPresenter(this)
        presenter?.getToolbarText?.text = "Terms & Conditions"
    }

    private fun initComponents() {
        presenter?.setToolbarSetup()
        termsWebView?.loadDataWithBaseURL(null, Services.getTermsHTML(), "text/html", "UTF-8", null)

        termsWebView!!.webViewClient = object : WebViewClient() {
            override fun onLoadResource(view: WebView, url: String) {
                super.onLoadResource(view, url)
            }
        }
    }

    override fun activity(): FragmentActivity = activity!!

    override fun context(): Context = context!!

    override fun applicationContext(): Context = activity().applicationContext!!

}