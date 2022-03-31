package com.radyopilipinomediagroup.radyo_now.local

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.radyopilipinomediagroup.radyo_now.model.notification.PayloadNotificationModel


class SessionManager(val context: Context?) {

    var session = context?.getSharedPreferences(APP_SESSION, MODE_PRIVATE)
    var editor = session?.edit()

    companion object {
        const val APP_SESSION = "APP_SESSION"
        const val SESSION_STATUS = "SESSION_STATUS"
        const val SESSION_TOKEN = "SESSION_TOKEN"
        const val SESSION_SETTINGS = "SESSION_SETTINGS"
        const val NOTIFICATIONS = "NOTIFICATIONS"
        const val LOGIN_TYPE = "LOGIN_TYPE"
    }

    fun <T> setData(call : String, data : T){
        editor?.putString(call , Gson().toJson(data))
        editor?.commit()
    }
    fun <T> setData(call : String, data : List<T>){
        editor?.putString(call , Gson().toJson(data))
        editor?.commit()
    }
    fun setData(call : String, data : String?){
        editor?.putString(call ,data)
        editor?.commit()
    }
    fun setData(call : String, data : Boolean){
        editor?.putBoolean(call ,data)
        editor?.commit()
    }

    fun getSessionStatus(): String?{
        return session?.getString(SESSION_STATUS, "")
    }

    fun getLoginType(): String? = session?.getString(LOGIN_TYPE, "")
    fun setLoginType(type: String) = setData(LOGIN_TYPE,type)
    fun isGuest(): Boolean = getLoginType() == "guest"

    fun getToken(): String?{
       return session?.getString(SESSION_TOKEN, "")
    }

    fun getSessionSettings(): Boolean?{
        return session?.getBoolean(SESSION_SETTINGS, true)
    }

    fun setNotification(data : PayloadNotificationModel.Bundle){
        val newData = mutableListOf<PayloadNotificationModel.Bundle>()
        newData.clear()
        newData.addAll(getNotification())
        newData.add(data)
        editor?.putString(NOTIFICATIONS ,Gson().toJson(newData))
        editor?.commit()
    }

    fun getNotification(): List<PayloadNotificationModel.Bundle> {
        val data = session?.getString(NOTIFICATIONS, Gson().toJson(mutableListOf<PayloadNotificationModel.Bundle>())!!)
        val listType = object : TypeToken<List<PayloadNotificationModel.Bundle>>() {}.type
        return Gson().fromJson(data, listType)
    }

    fun clearNotification() {
        editor?.putString(NOTIFICATIONS , Gson().toJson(mutableListOf<PayloadNotificationModel.Bundle>()))
        editor?.commit()
    }
}