package com.radyopilipinomediagroup.radyo_now.ui.dashboard.home.drawer.reviews

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.radyopilipinomediagroup.radyo_now.R
import com.radyopilipinomediagroup.radyo_now.ui.AbstractPresenter

class ReviewsFragment : Fragment(), AbstractPresenter.ContextView<FragmentActivity>, ReviewsPresenter.View {

    private var presenter : ReviewsPresenter? = null
    private var reviewsView : View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        reviewsView = inflater.inflate(R.layout.fragment_reviews, container, false)
        initComponents()
        return reviewsView
    }

    fun initComponents() {
        presenter = ReviewsPresenter(this)
        presenter?.setToolbarSettings()
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
}