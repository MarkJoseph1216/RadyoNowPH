package com.radyopilipinomediagroup.radyo_now.ui.dashboard.programs.search

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

class ProgramSearchFragment : Fragment(), AbstractPresenter.ContextView<FragmentActivity>,
    ProgramSearchPresenter.View, ProgramSearchPresenter.ProgramInterface, View.OnClickListener {

    private var programsView: View? = null
    private var presenter: ProgramSearchPresenter? = null
    var programLoading: ProgressBar? = null
    var noPrograms: TextView? = null
    var txtClear: TextView? = null
    var recentSearchRecycler: RecyclerView? = null
    var programsRecycler: RecyclerView? = null
    var recentSearchLayout: LinearLayout? = null
    var programsRecyclerContainer: LinearLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        programsView = inflater.inflate(R.layout.fragment_programs_search, container, false)
        initInitialize()
        initListeners()
        return programsView
    }

    private fun initInitialize() {
        programLoading = programsView?.findViewById(R.id.programLoading)
        txtClear = programsView?.findViewById(R.id.txtClear)
        recentSearchRecycler = programsView?.findViewById(R.id.recentSearchRecycler)
        programsRecycler = programsView?.findViewById(R.id.programsRecycler)
        noPrograms = programsView?.findViewById(R.id.noPrograms)
        recentSearchLayout = programsView?.findViewById(R.id.recentSearchLayout)
        programsRecyclerContainer = programsView?.findViewById(R.id.programsRecyclerContainer)
        presenter = ProgramSearchPresenter(this)
        presenter?.displayPrograms()
    }

    private fun initListeners(){
        txtClear?.setOnClickListener(this::onClick)
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

    override fun setRecentLayoutVisible(visible: Int) {
        recentSearchLayout?.visibility = visible
    }

    override fun setLoadingVisibility(visible: Int, programsVisible: Int) {
        programLoading?.visibility = visible
        programsRecyclerContainer?.visibility = programsVisible
    }

    override fun getRecentRecycler(): RecyclerView? {
        return recentSearchRecycler
    }

    override fun getRecyclerView(): RecyclerView? {
        return programsRecycler
    }

    override fun getRecyclerContainer(): LinearLayout? = programsRecyclerContainer

    override fun onDestroyView() {
        super.onDestroyView()
        presenter?.getToolbarLayoutSearch?.visibility = View.GONE
    }

    override fun searchProgram(searchKey: String) {
        presenter?.searchPrograms(searchKey)
    }

    //Interface
    override fun deleteRecentSearch(id: Int) {
        presenter?.deleteRecentSearch(id)
    }

    override fun onClick(view: View?) {
        when(view?.id) {
            R.id.txtClear -> presenter?.clearAllRecentSearch()
        }
    }
}