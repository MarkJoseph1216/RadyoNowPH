package com.radyopilipinomediagroup.radyo_now.ui.dashboard.programs.details

import android.annotation.SuppressLint
import android.util.Log
import android.view.ContextThemeWrapper
import android.widget.PopupMenu
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.radyopilipinomediagroup.radyo_now.R
import com.radyopilipinomediagroup.radyo_now.model.GeneralResultModel
import com.radyopilipinomediagroup.radyo_now.model.ads.AdsModel
import com.radyopilipinomediagroup.radyo_now.model.programs.ProgramDetailsModel
import com.radyopilipinomediagroup.radyo_now.model.stations.StationContentsResultModel
import com.radyopilipinomediagroup.radyo_now.repositories.RetrofitService
import com.radyopilipinomediagroup.radyo_now.ui.AbstractPresenter
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.DashboardActivity
import com.radyopilipinomediagroup.radyo_now.utils.PopupAdsStatus
import com.radyopilipinomediagroup.radyo_now.utils.Services
import com.radyopilipinomediagroup.radyo_now.utils.guestWarningDialog
import com.radyopilipinomediagroup.radyo_now.utils.toast
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class ProgramDetailsPresenter(var view: ProgramDetailsFragment)
    : AbstractPresenter<ProgramDetailsFragment>(view) {

    private var firebaseAnalytics: FirebaseAnalytics? = null
    val context = view.context!!
    private var programId = view.arguments!!.getString("stationId")
    private var programDetailsModel: ProgramDetailsModel? = null
    private var episodeCount = view.arguments!!.getString("contentId")
    private var llm = LinearLayoutManager(view.context())
    private var page = 1
    private var totalPage = 1
    private var programs = mutableListOf<StationContentsResultModel.Data>()
    private var programsAdapter = ProgramDetailsAdapter(
        view.context(), programs,
        programId!!, view.programsTitle?.text.toString()
    )

    val onProgramsScrollChange = android.view.View.OnScrollChangeListener{
        view,_,scrollY,_,_ ->
        val scrollView = view as NestedScrollView
        if (scrollY == scrollView.getChildAt(0).measuredHeight - scrollView.measuredHeight ) {
            if(totalPage > page) {
                page++
                getProgramContents(page)
//              view.context?.toast("$page")
            }
        }
    }

    init {
        programs.clear()
        initToolbar()
        setProgramsRecycler()
        getProgramDetails()
        getProgramContents()
        firebaseAnalytics = Firebase.analytics
        Log.d("GetProgramSinglePage: ", "$programId $episodeCount")
    }

    fun showPopUpMenu() {
        val wrapper = ContextThemeWrapper(
            view.context,
            R.style.popupMenu
        )
        val popup = PopupMenu(wrapper, view.imgOtherOption)
        popup.menuInflater?.inflate(R.menu.popup_menu_stream, popup.menu)
        setForceShowIcon(popup)
        popup.setOnMenuItemClickListener { menu ->
            when (menu.itemId) {
                R.id.addToPlaylist -> {
                    try {
                        (view.context as DashboardActivity).passData(programId!!.toInt())
                    } catch (e: Exception) {
                    }
                    return@setOnMenuItemClickListener true
                }
                R.id.shareContent -> {
                    shareContent()
                    return@setOnMenuItemClickListener true
                }
                else -> return@setOnMenuItemClickListener false
            }
        }
        popup.show()
    }

    private fun setForceShowIcon(popupMenu: PopupMenu) {
        try {
            val fields: Array<Field> = popupMenu.javaClass.declaredFields
            for (field in fields) {
                if ("mPopup" == field.name) {
                    field.isAccessible = true
                    val menuPopupHelper: Any = field.get(popupMenu)
                    val classPopupHelper = Class.forName(
                        menuPopupHelper
                            .javaClass.name
                    )
                    val setForceIcons: Method = classPopupHelper.getMethod(
                        "setForceShowIcon", Boolean::class.javaPrimitiveType
                    )
                    setForceIcons.invoke(menuPopupHelper, true)
                    break
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    private fun shareContent() {}

    private fun initToolbar(){
        getToolbarBack?.visibility = android.view.View.VISIBLE
        getToolbarLogo?.visibility = android.view.View.VISIBLE
        getToolbarText?.visibility = android.view.View.GONE
    }

    private fun getProgramDetails() {
        getRepositories?.getProgramDetails(
        programId!!,
        getSessionManager?.getToken()!!,
        object : RetrofitService.ResultHandler<ProgramDetailsModel> {
            override fun onSuccess(data: ProgramDetailsModel?) {
                programDetailsModel = data
                view.setProgramDetails(data!!, episodeCount.toString())
            }
            override fun onError(error: ProgramDetailsModel?) {}
            override fun onFailed(message: String) {
                Log.d("Error", message)
            }
        })
    }

    private fun setProgramsRecycler() {
        view.getRecyclerView().adapter = programsAdapter
        view.getRecyclerView().layoutManager = llm

    }

    private fun getProgramContents() {
        getProgramContents(1,15)
    }
    private fun getProgramContents(page: Int) {
        getProgramContents(page,15)
    }

    private fun getProgramContents(page: Int, perPage: Int) {
        getRepositories?.getProgramContents(
            programId!!,
            page,
            perPage,
            getSessionManager?.getToken()!!,
            object : RetrofitService.ResultHandler<StationContentsResultModel> {
                @SuppressLint("WrongConstant")
                override fun onSuccess(data: StationContentsResultModel?) {
                    when (data?.data?.size) {
                        0 -> displayNoPrograms()
                        else -> {
                            try {
                                programs.addAll(data?.data!!)
                                totalPage = data.meta.pagination.totalPages!!
                                println("getProgramContents: ${programs.size} - totalPage : $totalPage")
                                programsAdapter.notifyDataSetChanged()
                                view.loadingPrograms?.visibility = 8
//                                view.getRecyclerView().adapter = ProgramDetailsAdapter(
//                                    view.context(), data?.data!!,
//                                    programId!!, view.programsTitle?.text.toString()
//                                )
//                                view.getRecyclerView().layoutManager =
//                                    LinearLayoutManager(view.context())
                            } catch (e: Exception) { }
                        }
                    }
                }
                override fun onError(error: StationContentsResultModel?) = displayNoPrograms()
                override fun onFailed(message: String) = displayNoPrograms()
            })
    }

    @SuppressLint("WrongConstant")
    private fun displayNoPrograms(){
        view.loadingPrograms?.visibility = 8
        view.txtNoPrograms?.visibility = android.view.View.VISIBLE
        view.getRecyclerView().visibility = android.view.View.GONE
    }

    fun getAds() {
        getRepositories?.getAds(getSessionManager?.getToken()!!,
            "program", programId!!.toInt(),
            object : RetrofitService.ResultHandler<AdsModel> {
                @SuppressLint("SimpleDateFormat")
                override fun onSuccess(data: AdsModel?) {
                    println("getAds: ${Gson().toJson(data)}")
                    data?.data?.forEach { ads ->
                        if (ads.active!!) {
                            if (ads.section == "program" && ads.type == "popup" && PopupAdsStatus.programSinglePage) {
                                PopupAdsStatus.programSinglePage = false
                                view.onPopupReady(ads)
                            } else if (ads.section == "program" && ads.type == "slider") view.onSliderReady(
                                ads
                            )
                            else if (ads.section == "program" && ads.type == "banner") {
                                val strDateFrom = Services.convertDate(ads.durationFrom, "" +
                                        "yyyy-MM-dd", "yyyy/MM/dd")
                                val strDateEnd = Services.convertDate(ads.durationTo, "" +
                                "yyyy-MM-dd", "yyyy/MM/dd")

                                val dateFormat = SimpleDateFormat("yyyy/MM/dd")
                                try {
                                    val dateFrom: Date? = dateFormat.parse(strDateFrom)
                                    val dateEnd: Date? = dateFormat.parse(strDateEnd)
                                    if (Services.dateBetween(Services.getCurrentDate(), dateFrom, dateEnd)) {
                                        view.onBannerReady(ads)
                                    }
                                } catch (e: ParseException) {
                                    println("getAds: ${e.message}")
                                }
                            }
                        }
                    }
                }

                override fun onError(error: AdsModel?) {
                    println("getAdsError: ${Gson().toJson(error)}")
                    view.imgBannerAds?.visibility = android.view.View.GONE
                }

                override fun onFailed(message: String) {
                    println("getAdsFailed: $message")
                    view.imgBannerAds?.visibility = android.view.View.GONE
                }
            })
    }

    fun deleteFavorite(contentId: String){
        getRepositories?.deleteFavorite(
            contentId,
            getSessionManager?.getToken()!!,
            object: RetrofitService.ResultHandler<GeneralResultModel>{
                override fun onSuccess(data: GeneralResultModel?) {
                    view.context?.toast("${data?.message}")
                    refreshProgramContents(contentId, false)
                }
                override fun onError(error: GeneralResultModel?) {
                    view.context?.toast("${error?.message}")
                }
                override fun onFailed(message: String) {
                    view.context?.toast("Failed to load. Please try again!")
                }
            }
        )
    }

    fun addToFavorites(contentId: String) {
        getRepositories?.addToFavorites(
            contentId,
            getSessionManager?.getToken()!!,
            object: RetrofitService.ResultHandler<GeneralResultModel>{
                override fun onSuccess(data: GeneralResultModel?) {
                    view.context?.toast("${data?.message}")
                    refreshProgramContents(contentId, true)
                }
                override fun onError(error: GeneralResultModel?) {
                    view.context?.toast("${error?.message}")
                }
                override fun onFailed(message: String) {
                    view.context?.toast("Failed to load. Please try again!")
                }
            }
        )
    }

    fun refreshProgramContents(contentId: String, isFavorite: Boolean){
        programs.forEach{
            if(contentId == it.id.toString()){
                it.isAppFavorite = isFavorite
                programsAdapter.notifyDataSetChanged()
                return@forEach
            }
        }
    }

    fun processDataAnalytics(data: AdsModel.Data){
        Services.openBrowser(view.context!!, data.assets[0].link.toString())
        Services.setDataAnalytics(
            firebaseAnalytics,
            data.title,
            data.location,
            data.id.toString(),
            "Location",
            "select_ad")
    }

    fun checkRestriction(buttonId: Int): Boolean? {
        if (getSessionManager?.isGuest() == true){
            view.context().guestWarningDialog()
            return false
        }
        when(buttonId){ }
        return true
    }

    interface View : AbstractView {
        fun getRecyclerView() : RecyclerView?
        fun setProgramDetails(details: ProgramDetailsModel, episodeCount: String)
    }
}