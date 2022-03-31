package com.radyopilipinomediagroup.radyo_now.utils

import com.radyopilipinomediagroup.radyo_now.model.notification.NotificationModel
import com.radyopilipinomediagroup.radyo_now.model.notification.NotificationResponseModel
import com.radyopilipinomediagroup.radyo_now.model.realm.NotificationLocal
import io.realm.Realm
import io.realm.RealmResults

object RealmService {
    var realm : Realm? = null
    private var listener = mutableListOf<RealmHandler>()
    var model: NotificationLocal? = null

    init {
        if(realm == null) realm = Realm.getDefaultInstance()
    }

    fun setOnNotificationUpdateListener(handler: RealmHandler){
        listener.add(handler)
        updateListeners()
    }

    fun validateInsert(data: NotificationResponseModel.Data){
        val notification = realm?.where(NotificationLocal::class.java)
            ?.equalTo("id", data.id)
            ?.findFirst()
        if(notification == null) insertNotification(data)
    }

    fun updateNotificationRead(notif: NotificationModel){
        if(notif.isRead!!) {
            println("updateNotificationRead: ${notif.id} already read!")
            return
        }
        if(!realm?.isInTransaction!!) realm?.beginTransaction()
        val notification = realm?.where<NotificationLocal>(NotificationLocal::class.java)
            ?.equalTo("id", notif.id)
            ?.findFirst()

        if(notification != null) notification.isRead = true
        println("updateNotificationRead: ${notification?.id} ${notification?.isRead}")
        realm?.commitTransaction()
        updateListeners()

    }

    fun insertNotification(programName: NotificationResponseModel.Data){
        if(!realm?.isInTransaction!!) realm?.beginTransaction()

//                val primaryId: Number? = realm?.where(NotificationLocal::class.java)?.max("id")
//
//                val nextId: Int = when(primaryId) {
//                    null -> 1
//                    else -> programName.id!!
//                }

                model = NotificationLocal(
                    programName.id,
                    programName.content?.format,
                    programName.name,
                    programName.description,
                    programName.content?.id.toString(),
                    programName.triggerDatetime
                )

            realm?.insertOrUpdate(model!!)
            realm?.commitTransaction()
            updateListeners()
    }

    fun updateListeners(){
       try {
           listener.forEach {
               it.onNotificationUpdate(
                   getNotificationCount(),
                   realm?.copyFromRealm(getNotificationList()!!)!!,
                   getNewNotificationCount()
               )
           }
       } catch (e: Exception) {}
    }

    private fun getNewNotificationCount(): Int {
        return realm?.where<NotificationLocal>(NotificationLocal::class.java)?.equalTo("isRead", false)
            ?.count()
            ?.toInt()!!
    }

    private fun getNotificationCount(): Int {
        return realm?.where<NotificationLocal>(NotificationLocal::class.java)?.count()?.toInt()!!
    }

    private fun getNotificationList(): RealmResults<NotificationLocal>? {
        return realm?.where<NotificationLocal>(NotificationLocal::class.java)?.findAll()
    }

    interface RealmHandler {
        fun onNotificationUpdate(count: Int, notificationList: List<NotificationLocal>, newCount: Int)
    }
}