package com.radyopilipinomediagroup.radyo_now.ui.dashboard.programs

import android.annotation.SuppressLint
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.radyopilipinomediagroup.radyo_now.model.programs.ProgramsModel
import com.radyopilipinomediagroup.radyo_now.model.realm.RecentSearch
import com.radyopilipinomediagroup.radyo_now.repositories.RetrofitService
import com.radyopilipinomediagroup.radyo_now.ui.AbstractPresenter
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.programs.search.ProgramSearchFragment
import com.radyopilipinomediagroup.radyo_now.utils.Services
import com.radyopilipinomediagroup.radyo_now.utils.Services.Companion.checkIfAuthenticated
import com.radyopilipinomediagroup.radyo_now.utils.Services.Companion.signOutExpired
import io.realm.Realm


class ProgramsPresenter(var view: ProgramsFragment) : AbstractPresenter<ProgramsFragment>(view){

    private var programs = ArrayList<ProgramsModel.Data>()
    private var adapter = ProgramListAdapter(view.context(), programs)
    private var recentllm = GridLayoutManager(view.activity(), 3, GridLayoutManager.VERTICAL, false)
    private var llm = GridLayoutManager(view.activity(), 3, GridLayoutManager.VERTICAL, false)

    //Realm Database
    private lateinit var realm: Realm
    private var model = RecentSearch()

    init {
        initHandlers()
    }

    private fun initHandlers(){
        realm = Realm.getDefaultInstance()
        getPrograms()
        initToolbar()
    }

    @SuppressLint("NewApi")
    private fun initToolbar(){
        getToolbarText?.text = "Programs"
        getToolbarText?.visibility = android.view.View.VISIBLE
        getToolbarLayoutSearch?.visibility = android.view.View.VISIBLE
        getToolbarBack?.visibility = android.view.View.GONE
        getToolbarLogo?.visibility = android.view.View.GONE
        getToolbarSearch?.text?.clear()
        getToolbarSearch?.isFocusableInTouchMode = false
        getToolbarSearch?.setOnClickListener {
            setFragmentDetails(ProgramSearchFragment(), "ProgramSearchFragment")
        }
    }

    private fun setFragmentDetails(
        fragment: Fragment, tag: String) {
        Services.changeFragment((view.context() as FragmentActivity).supportFragmentManager, fragment, tag)
    }

    fun displayPrograms(){
        view.setLoadingVisibility(0, 8)
        view.getRecyclerView()?.adapter = adapter
        view.getRecyclerView()?.layoutManager = llm
    }

    private fun getPrograms() {
        programs.clear()
        view.setLoadingVisibility(0, 8)
        getRepositories?.getProgramsList(
            getSessionManager?.getToken()!!,
            object : RetrofitService.ResultHandler<ProgramsModel> {
                override fun onSuccess(data: ProgramsModel?) {
                    when (data?.data?.size) {
                        0 -> displayNoPrograms(data.message.toString())
                        else -> {
                            programs.addAll(data?.data!!)
                            programs.sortBy { it.name }
                            adapter.notifyDataSetChanged()
                            view.setLoadingVisibility(8, 0)
                            view.noPrograms?.visibility = android.view.View.GONE
                        }
                    }
                }

                override fun onError(error: ProgramsModel?) = displayNoPrograms(error?.message.toString())
                override fun onFailed(message: String) = displayNoPrograms(message)
            })
    }

    private fun displayNoPrograms(message: String){
        view.setLoadingVisibility(8, 0)
        view.noPrograms?.visibility = android.view.View.VISIBLE
        view.getRecyclerContainer()?.visibility = android.view.View.GONE

        if (!checkIfAuthenticated(message)) {
            signOutExpired(view.context(), getSessionManager)
            view.activity().finish()
        }
    }

    interface View : AbstractView {
        fun setLoadingVisibility(visible: Int, programsVisible: Int)
        fun getRecyclerView() : RecyclerView?
        fun getRecyclerContainer() : LinearLayout?
    }
}