package com.radyopilipinomediagroup.radyo_now.ui.dashboard.home.drawer.faq

import androidx.recyclerview.widget.LinearLayoutManager
import com.radyopilipinomediagroup.radyo_now.model.faq.FAQModel
import com.radyopilipinomediagroup.radyo_now.ui.AbstractPresenter

class FAQPresenter(var view: FAQFragment): AbstractPresenter<FAQFragment>(view) {

    private var faqList : List<FAQModel>? = getRepositories?.getFaqData()
    private var adapter = FAQAdapter(view.context(), faqList)
    private var LLM = LinearLayoutManager(view.context())

    init {
        initToolbar()
        displayFAQ()
    }

    private fun initToolbar(){
        getToolbarDrawerIcon?.visibility = android.view.View.VISIBLE
        getToolbarBack?.visibility = android.view.View.GONE
        getToolbarLogo?.visibility = android.view.View.VISIBLE
        getToolbarText?.text = "FAQ"
    }

    private fun displayFAQ(){
        view.recyclerFaqs?.adapter = adapter
        view.recyclerFaqs?.layoutManager = LLM
    }

    interface View : AbstractPresenter.AbstractView { }
}