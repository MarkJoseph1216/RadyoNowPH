package com.radyopilipinomediagroup.radyo_now.ui.dashboard.home.drawer.notification

import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.radyopilipinomediagroup.radyo_now.model.notification.NotificationModel
import com.radyopilipinomediagroup.radyo_now.model.notification.NotificationResponseModel
import com.radyopilipinomediagroup.radyo_now.model.realm.NotificationLocal
import com.radyopilipinomediagroup.radyo_now.repositories.RetrofitService
import com.radyopilipinomediagroup.radyo_now.ui.AbstractPresenter
import com.radyopilipinomediagroup.radyo_now.utils.RealmService

class NotificationPresenter(var view: NotificationFragment):
    AbstractPresenter<NotificationFragment>(view) {

    var context = view.context!!
    private var notifications = mutableListOf<NotificationLocal>()
    var adapter = NotificationAdapter(context,notifications)
    var llm = LinearLayoutManager(context)
    var pageCount = 0
    var currentPage = 0
    private var realm = RealmService
    private var realmListener = object: RealmService.RealmHandler{
        override fun onNotificationUpdate(
            count: Int,
            notificationList: List<NotificationLocal>,
            newCount: Int
        ) {
            notifications.clear()
            notifications.addAll(notificationList)
            notifications.sortByDescending { it.getDate() }
            adapter.notifyDataSetChanged()
        }
    }

    init {
        setToolbar()
        setupRecycler()
        getNotifications(1)
        realm.setOnNotificationUpdateListener(realmListener)
//        getNotification()
    }

    private fun getNotifications(page: Int) {
        getRepositories?.getNotifications(
            page,15,
            getSessionManager?.getToken()!!,
            object: RetrofitService.ResultHandler<NotificationResponseModel>{
                override fun onSuccess(data: NotificationResponseModel?) {
                    println("metaNotification: ${Gson().toJson(data?.meta)}")
                    pageCount = data?.meta?.pagination?.totalPages!!
                    currentPage = data?.meta?.pagination?.currentPage!!
                    data?.data?.forEach {
                        realm.validateInsert(it!!)
                    }
                }
                override fun onError(error: NotificationResponseModel?) { }
                override fun onFailed(message: String) { }
            })
    }

    private fun setToolbar() {
        getToolbarDrawerIcon?.visibility = android.view.View.VISIBLE
        getToolbarBack?.visibility = android.view.View.GONE
        getToolbarLogo?.visibility = android.view.View.VISIBLE
        getToolbarText?.text = "Notifications"
    }

    private fun setupRecycler() {
        view.notificationRecycler?.adapter = adapter
        view.notificationRecycler?.layoutManager = llm
    }

    fun updateNotificationRead(notification: NotificationLocal) {
        println("updateNotificationRead: start")
        val notif = Gson().fromJson(Gson().toJson(notification), NotificationModel::class.java)
        realm.updateNotificationRead(notif)
    }

    fun getNextPage() {
        currentPage++
        getNotifications(currentPage)
    }

//    private fun getNotification() {
//        notifications.clear()
//        try{
//            val data = realm.getNotificationList()
//            notifications.addAll(realm.realm?.copyFromRealm(data!!)!!)
//        }catch (e: Exception){}
//        adapter.notifyDataSetChanged()
//    }

    interface View: AbstractPresenter.AbstractView{
        fun updateNotificationRead(notification: NotificationLocal)
    }
}