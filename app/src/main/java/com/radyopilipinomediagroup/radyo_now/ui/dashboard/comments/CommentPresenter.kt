package com.radyopilipinomediagroup.radyo_now.ui.dashboard.comments

import android.annotation.SuppressLint
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.radyopilipinomediagroup.radyo_now.model.GeneralResultModel
import com.radyopilipinomediagroup.radyo_now.model.comments.CommentsResponse
import com.radyopilipinomediagroup.radyo_now.model.profile.ProfileDetailsResponse
import com.radyopilipinomediagroup.radyo_now.repositories.RetrofitService
import com.radyopilipinomediagroup.radyo_now.ui.AbstractPresenter
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.DashboardActivity
import com.radyopilipinomediagroup.radyo_now.utils.hideKeyboard
import com.radyopilipinomediagroup.radyo_now.utils.toast

class CommentPresenter(var view : CommentFragment) : AbstractPresenter<CommentFragment>(view) {

    private var comments = mutableListOf<CommentsResponse.Data>()
    private var adapter = CommentAdapter(view.context(), comments)
    private var llm = LinearLayoutManager(view.context(), LinearLayoutManager.VERTICAL, false)
    private val contentId: String? = view.arguments!!.getString("contentId")

    init {
        initHandlers()
    }

    private fun initHandlers(){
        initToolbarSetup()
        displayComments()
        getCommentList()
        getProfileDetails()
    }

    private fun getProfileDetails() {
        getRepositories?.getProfileDetails(getSessionManager?.getToken()!!, object :
            RetrofitService.ResultHandler<ProfileDetailsResponse> {
            override fun onSuccess(data: ProfileDetailsResponse?) { view.setProfileDetails(data) }
            override fun onError(error: ProfileDetailsResponse?) { Log.d("Error", error?.message!!) }
            override fun onFailed(message: String) { Log.d("Failed", message) }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun initToolbarSetup(){
        getToolbarLogo?.visibility = android.view.View.GONE
        getToolbarDrawerIcon?.visibility = android.view.View.GONE
        getToolbarBack?.visibility = android.view.View.VISIBLE
        getToolbarText?.visibility = android.view.View.VISIBLE
        getToolbarText?.text = "Comments"
        getToolbarBack?.setOnClickListener {
            view.hideKeyboard()
            (it.context as DashboardActivity).getBackStacked()
        }
    }

    private fun getCommentList() {
        if (getSessionManager?.isGuest() == true) return
        comments.clear()
        getRepositories?.getCommentList(getSessionManager?.getToken()!!,
            contentId, object:RetrofitService.ResultHandler<CommentsResponse>{
            override fun onSuccess(data: CommentsResponse?) {
                comments.addAll(data?.data!!)
                adapter.notifyDataSetChanged()

                when(comments.size) {
                    0 -> view.setNoContentVisible(0, 8)
                    else -> view.setNoContentVisible(8, 8)
                }
            }
            override fun onError(error: CommentsResponse?) { view.setNoContentVisible(0, 8) }
            override fun onFailed(message: String) {
                view.setNoContentVisible(0, 8)
                view.context?.toast("Failed to load. Please try again!")
            }
        })
    }

    fun addToComments(comment: String){
        if (comment.isEmpty()) view.context?.toast("Comment has empty!")
        else processComments(comment)
    }

    private fun processComments(comment: String){
        getRepositories?.addToComments(
            contentId,
            comment,
            getSessionManager?.getToken()!!,
            object: RetrofitService.ResultHandler<GeneralResultModel>{
                override fun onSuccess(data: GeneralResultModel?) {
                    view.edtComment?.text?.clear()
                    view.context?.toast("Thank you for your comments. This has been submitted to the admin and subject for approval.")
                    getCommentList()
                }
                override fun onError(error: GeneralResultModel?) { view.context?.toast("${error?.message}") }
                override fun onFailed(message: String) { view.context?.toast("Failed to load. Please try again!") }
            }
        )
    }

    private fun displayComments(){
        view.recyclerComments?.adapter = adapter
        view.recyclerComments?.layoutManager = llm
    }

    interface View : AbstractPresenter.AbstractView{
        fun setNoContentVisible(visible: Int, loading: Int)
        fun setProfileDetails(details: ProfileDetailsResponse?)
    }
}