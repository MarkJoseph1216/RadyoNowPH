package com.radyopilipinomediagroup.radyo_now.ui.dashboard.home.drawer.faq

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.radyopilipinomediagroup.radyo_now.R
import com.radyopilipinomediagroup.radyo_now.ui.AbstractPresenter

class FAQFragment : Fragment(), AbstractPresenter.ContextView<FragmentActivity>
    ,FAQPresenter.View, View.OnClickListener {

    private var faqView : View? = null
    private var presenter : FAQPresenter? = null
    var recyclerFaqs : RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        faqView = inflater.inflate(R.layout.fragment_faq, container, false)
        initComponents()
        initDeclaration()
        return faqView
    }

    private fun initComponents() {
        recyclerFaqs = faqView?.findViewById(R.id.recyclerFaqs)
    }

    private fun initDeclaration(){
        presenter = FAQPresenter(this)
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

    override fun onClick(view: View?) {
        when(view?.id) {

        }
    }

}