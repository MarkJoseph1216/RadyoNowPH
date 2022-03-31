package com.radyopilipinomediagroup.radyo_now.ui.dashboard.programs.search

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.radyopilipinomediagroup.radyo_now.model.programs.ProgramsModel
import com.radyopilipinomediagroup.radyo_now.model.realm.RecentSearch
import com.radyopilipinomediagroup.radyo_now.repositories.RetrofitService
import com.radyopilipinomediagroup.radyo_now.ui.AbstractPresenter
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.programs.ProgramListAdapter
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.programs.ProgramRecentAdapter
import io.realm.Realm
import io.realm.RealmResults


class ProgramSearchPresenter(var view: ProgramSearchFragment) : AbstractPresenter<ProgramSearchFragment>(
    view){

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
        getAllRecentSearch()
        initToolbar()
    }

    private fun initToolbar(){
        getToolbarText?.text = "Programs"
        getToolbarBack?.visibility = android.view.View.GONE
        getToolbarLogo?.visibility = android.view.View.GONE
        getToolbarBack?.visibility = android.view.View.VISIBLE
        getToolbarClose?.setOnClickListener { getToolbarSearch?.text?.clear() }

        getToolbarSearch?.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(text: CharSequence, start: Int, before: Int, count: Int) =
                if (text.isNotEmpty()) getToolbarClose?.visibility = android.view.View.VISIBLE
                else getToolbarClose?.visibility = android.view.View.GONE
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {}
        })

        //Set the onClickListener to null to disable onClick function only of the searchBox.
        getToolbarLayoutSearch?.visibility = android.view.View.VISIBLE
        getToolbarSearch?.text?.clear()
        getToolbarSearch?.isFocusableInTouchMode = true
        getToolbarSearch?.clearFocus()
        getToolbarSearch?.setOnClickListener(null)
        getToolbarSearch?.setOnEditorActionListener { editText, actionId, _ ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_DONE
                || actionId == EditorInfo.IME_ACTION_UNSPECIFIED
                || actionId == EditorInfo.IME_NULL) {
                searchPrograms(editText.text.toString())
                handled = true
            }
            handled
        }
    }

    private fun displayRecentSearch(data: RealmResults<RecentSearch>){
        view.getRecentRecycler()?.adapter = ProgramRecentAdapter(view.context(), data)
        view.getRecentRecycler()?.layoutManager = recentllm
    }

    fun displayPrograms(){
        view.getRecyclerView()?.adapter = adapter
        view.getRecyclerView()?.layoutManager = llm
    }

    fun searchPrograms(searchKey: String) {
        //Adding and checking to the database if the key is exists (checkIfDataExists)
        view.setLoadingVisibility(0, 8)
        view.noPrograms?.visibility = android.view.View.GONE
        programs.clear()
        checkIfDataExists(searchKey)
        deleteFirstRowSearch()
        getRepositories?.searchProgramList(
            getSessionManager?.getToken()!!,
            searchKey,
            object : RetrofitService.ResultHandler<ProgramsModel> {
                override fun onSuccess(data: ProgramsModel?) {
                    when (data?.data?.size) {
                        0 -> displayNoPrograms()
                        else -> {
                            programs.addAll(data?.data!!)
                            programs.sortBy { it.name }
                            adapter.notifyDataSetChanged()
                            view.setLoadingVisibility(8, 0)
                        }
                    }
                }

                override fun onError(error: ProgramsModel?) = displayNoPrograms()
                override fun onFailed(message: String) = displayNoPrograms()
            })
    }

    private fun displayNoPrograms(){
        view.setLoadingVisibility(8, 8)
        view.noPrograms?.visibility = android.view.View.VISIBLE
    }

    private fun getAllRecentSearch(){
        val data = realm.where<RecentSearch>(RecentSearch::class.java).findAll()
        displayRecentSearch(data)

        when(data?.size) {
            0 -> view.setRecentLayoutVisible(8)
            else -> view.setRecentLayoutVisible(0)
        }
    }

    private fun checkIfDataExists(searchKey: String) {
        //Checking if the data is already exists on database.
        val model: RecentSearch? = realm.where(RecentSearch::class.java)
            .equalTo("programSearch", searchKey)
            .findFirst()
        if (model == null) {
            addToDatabase(searchKey)
        }
    }

    private fun addToDatabase(programName: String){
        try {
            realm.beginTransaction()
            val primaryId: Number? = realm.where(RecentSearch::class.java).max("id")
            var nextId: Int? = null
            when(primaryId) {
                null -> nextId = 1
                else -> primaryId.toInt() + 1
            }
            model = RecentSearch(nextId, programName)
            realm.insertOrUpdate(model)
            realm.commitTransaction()
        } catch (e: Exception) {}
    }

    private fun deleteFirstRowSearch(){
        try {
            realm.executeTransaction { realm ->
                val data = realm.where<RecentSearch>(RecentSearch::class.java).findAll()
                val result = realm.where(RecentSearch::class.java).findAll().first()
                if (data.size > 5) result?.deleteFromRealm()
            }
            getAllRecentSearch()
        } catch (e: Exception) {
            Log.d("GetDeleteError: ", e.message.toString())
        }
    }

    fun deleteRecentSearch(id: Int){
        try {
            realm.executeTransaction { realm ->
                val result: RealmResults<RecentSearch> = realm.where(RecentSearch::class.java).findAll()
                result.deleteFromRealm(id)
            }
            getAllRecentSearch()
        } catch (e: Exception) {
            Log.d("GetDeleteError: ", e.message.toString())
        }
    }

    fun clearAllRecentSearch() = try {
        programs.clear()
        getToolbarSearch?.text?.clear()
        getToolbarSearch?.clearFocus()
        realm.executeTransaction(Realm::deleteAll)
        getAllRecentSearch()
        adapter.notifyDataSetChanged()
    } catch (e: Exception) { }

    interface View : AbstractView {
        fun setRecentLayoutVisible(visible: Int)
        fun setLoadingVisibility(visible: Int, programsVisible: Int)
        fun getRecentRecycler() : RecyclerView?
        fun getRecyclerView() : RecyclerView?
        fun getRecyclerContainer() : LinearLayout?
    }

    interface ProgramInterface {
        fun searchProgram(searchKey: String)
        fun deleteRecentSearch(id: Int)
    }
}