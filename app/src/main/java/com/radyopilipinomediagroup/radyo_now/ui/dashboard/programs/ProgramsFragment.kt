package com.radyopilipinomediagroup.radyo_now.ui.dashboard.programs

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.radyopilipinomediagroup.radyo_now.R
import com.radyopilipinomediagroup.radyo_now.ui.AbstractPresenter

class ProgramsFragment : Fragment(), AbstractPresenter.ContextView<FragmentActivity>,
    ProgramsPresenter.View {

    private var programsView: View? = null
    private var presenter: ProgramsPresenter? = null
    var programLoading: ProgressBar? = null
    var noPrograms: TextView? = null
    var programsRecycler: RecyclerView? = null
    var programsRecyclerContainer: LinearLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        programsView = inflater.inflate(R.layout.fragment_programs, container, false)
        initInitialize()
        return programsView
    }

    private fun initInitialize() {
        programLoading = programsView?.findViewById(R.id.programLoading)
        programsRecycler = programsView?.findViewById(R.id.programsRecycler)
        noPrograms = programsView?.findViewById(R.id.noPrograms)
        programsRecyclerContainer = programsView?.findViewById(R.id.programsRecyclerContainer)
        presenter = ProgramsPresenter(this)
        presenter?.displayPrograms()
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

    override fun setLoadingVisibility(visible: Int, programsVisible: Int) {
        programLoading?.visibility = visible
        programsRecyclerContainer?.visibility = programsVisible
    }

    override fun getRecyclerView(): RecyclerView? {
        return programsRecycler
    }

    override fun getRecyclerContainer(): LinearLayout? = programsRecyclerContainer

    override fun onDestroyView() {
        super.onDestroyView()
        presenter?.getToolbarLayoutSearch?.visibility = View.GONE
    }
}