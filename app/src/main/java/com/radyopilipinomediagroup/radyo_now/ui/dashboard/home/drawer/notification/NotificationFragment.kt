package com.radyopilipinomediagroup.radyo_now.ui.dashboard.home.drawer.notification

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.radyopilipinomediagroup.radyo_now.R
import com.radyopilipinomediagroup.radyo_now.ui.AbstractPresenter
import com.radyopilipinomediagroup.radyo_now.model.realm.NotificationLocal

class NotificationFragment : Fragment(), NotificationPresenter.View, AbstractPresenter.ContextView<FragmentActivity> {

    var mainView: View? = null
    var presenter: NotificationPresenter? = null
    var notificationRecycler: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mainView = inflater.inflate(R.layout.fragment_notification, container, false)
        initComponents()
        return mainView
    }

    private fun initComponents() {
        notificationRecycler = mainView?.findViewById(R.id.notificationRecycler)
        presenter = NotificationPresenter(this)
    }

    override fun activity(): FragmentActivity = activity!!
    override fun context(): Context = context!!
    override fun applicationContext(): Context = activity?.applicationContext!!
    override fun updateNotificationRead(notification: NotificationLocal) {
        presenter?.updateNotificationRead(notification)
    }

    fun getNextPage() {
        presenter?.getNextPage()
    }

    fun getCurrentPage(): Int? = presenter?.currentPage
    fun getPageCount(): Int? = presenter?.pageCount


}