package com.radyopilipinomediagroup.radyo_now.ui.dashboard.home.drawer.app.settings

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import com.radyopilipinomediagroup.radyo_now.R
import com.radyopilipinomediagroup.radyo_now.ui.AbstractPresenter
import com.suke.widget.SwitchButton


class AppSettingsFragment : Fragment(), AppSettingsPresenter.View, AbstractPresenter.ContextView<FragmentActivity> {

    var mainView: View? = null
    var presenter: AppSettingsPresenter? = null
    var switchNotification: SwitchButton? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mainView = inflater.inflate(R.layout.fragment_app_settings, container, false)
        initComponents()
        return mainView
    }

    private fun initComponents() {
        switchNotification = mainView?.findViewById(R.id.switchNotification)
        presenter = AppSettingsPresenter(this)

        switchNotification?.isChecked = presenter?.notifSettings!!
        initListener()
    }

    private fun initListener() {
        switchNotification?.setOnCheckedChangeListener { _, isChecked ->
            presenter?.changeNotifSettings(isChecked)
        }
    }

    override fun activity(): FragmentActivity = activity!!
    override fun context(): Context = context!!
    override fun applicationContext(): Context = activity?.applicationContext!!


}