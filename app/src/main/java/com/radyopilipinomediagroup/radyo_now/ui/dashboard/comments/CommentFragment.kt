package com.radyopilipinomediagroup.radyo_now.ui.dashboard.comments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mikhaellopez.circularimageview.CircularImageView
import com.radyopilipinomediagroup.radyo_now.R
import com.radyopilipinomediagroup.radyo_now.model.profile.ProfileDetailsResponse
import com.radyopilipinomediagroup.radyo_now.ui.AbstractPresenter

class CommentFragment : Fragment(), AbstractPresenter.ContextView<FragmentActivity>,
    CommentPresenter.View, View.OnClickListener{

    var recyclerComments: RecyclerView? = null
    private var getView : View? = null
    var edtComment : EditText? = null
    private var noContent: RelativeLayout? = null
    private var imgProfile : CircularImageView? = null
    private var btnSend : Button? = null
    private var loadingComments: ProgressBar? = null
    private var presenter : CommentPresenter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        getView = inflater.inflate(R.layout.fragment_comment, container, false)
        initInitialize()
        initListener()
        return getView
    }

    private fun initInitialize() {
        recyclerComments = getView?.findViewById(R.id.recyclerComments)
        loadingComments = getView?.findViewById(R.id.loadingComments)
        imgProfile = getView?.findViewById(R.id.imgProfile)
        btnSend = getView?.findViewById(R.id.btnSend)
        noContent = getView?.findViewById(R.id.noContent)
        edtComment = getView?.findViewById(R.id.edtComment)
        presenter = CommentPresenter(this)
    }

    private fun initListener(){
        btnSend?.setOnClickListener(this::onClick)
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
            R.id.btnSend -> presenter?.addToComments(edtComment?.text.toString())
        }
    }

    override fun setNoContentVisible(visible: Int, loading: Int) {
        noContent?.visibility = visible
        loadingComments?.visibility = loading
    }

    override fun setProfileDetails(details: ProfileDetailsResponse?) {
        try {
            Glide.with(context())
                .load(details?.data?.avatar)
                .placeholder(R.drawable.ic_radyo_icon)
                .into(imgProfile!!)
        } catch (e: Exception) {}
    }
}