package com.radyopilipinomediagroup.radyo_now.ui.dashboard

import android.annotation.SuppressLint
import android.content.*
import android.content.DialogInterface.BUTTON_NEGATIVE
import android.content.DialogInterface.BUTTON_POSITIVE
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.radyopilipinomediagroup.radyo_now.R
import com.radyopilipinomediagroup.radyo_now.model.ads.AdsModel
import com.radyopilipinomediagroup.radyo_now.model.deeplink.DeepLinkResponse
import com.radyopilipinomediagroup.radyo_now.model.notification.NotificationModel
import com.radyopilipinomediagroup.radyo_now.ui.AbstractInterface
import com.radyopilipinomediagroup.radyo_now.ui.AbstractPresenter
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.home.HomeFragment
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.home.drawer.app.settings.AppSettingsFragment
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.home.drawer.faq.FAQFragment
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.home.drawer.terms.TermsFragment
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.home.playlist.streams.AudioStreamFragment
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.stations.featuredstreaming.FeaturedStreamFragment
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.stations.featuredstreaming.FeaturedStreamInterface
import com.radyopilipinomediagroup.radyo_now.utils.Constants
import com.radyopilipinomediagroup.radyo_now.utils.NotificationService
import com.radyopilipinomediagroup.radyo_now.utils.Services
import com.radyopilipinomediagroup.radyo_now.utils.Services.Companion.openPermissionSettings
import com.radyopilipinomediagroup.radyo_now.utils.Services.Companion.showWarningDialog
import com.radyopilipinomediagroup.radyo_now.utils.toast
import io.branch.referral.Branch


class DashboardActivity : AppCompatActivity(), AbstractPresenter.ContextView<DashboardActivity>,
    DashboardPresenter.View, AbstractInterface.DataHandler<Int>,
    FeaturedStreamInterface, AbstractInterface.BottomSheetHandler, AbstractInterface.PlaylistHandler,
    AbstractInterface.AdsInterface {

    var bottomNavigation : BottomNavigationView? = null
    private var toolbar : androidx.appcompat.widget.Toolbar? = null
    private var fragmentManager : FragmentManager? = null
    private var presenter : DashboardPresenter? = null
    private var coordinatorLayout : CoordinatorLayout? = null
    private var notificationCount : TextView? = null
    private var chosenContent: Int? = 0
    private var drawerLayout : DrawerLayout? = null
    private var actionBarDrawerToggle: ActionBarDrawerToggle? = null
    private var playlistRecycler : RecyclerView? = null
    private var notificationService : NotificationService? = null
    private var fragmentManagers: FragmentManager? = null
    private var notifDetails : String? = ""
    var drawerSignOut : TextView? = null

    var drawerAppSettings : TextView? = null
    var drawerReviews : TextView? = null
    var drawerProfile : TextView? = null
    var drawerNotification : ViewGroup? = null
    var drawerNotificationTxt : TextView? = null

    private var videoListener : AbstractInterface.OrientationHandler? = null

    private var bHandler = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if(intent == null) return
            println("newIntent")
            presenter?.setNotifications()
        }
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(bHandler)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocalBroadcastManager.getInstance(this).registerReceiver(bHandler, IntentFilter("notification_broadcast"))
        setContentView(R.layout.activity_dashboard)
        initInitialize()
        initComponents()
    }

    private fun initInitialize() {
        drawerAppSettings = findViewById(R.id.drawerAppSettings)
        drawerReviews = findViewById(R.id.drawerReviews)
        drawerProfile = findViewById(R.id.drawerProfile)
        drawerNotification = findViewById(R.id.drawerNotification)
        drawerNotificationTxt = findViewById(R.id.drawerNotificationTxt)
        drawerSignOut = findViewById(R.id.drawerSignOut)
        notificationCount = findViewById(R.id.notificationCount)
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        toolbar = findViewById(R.id.toolbar)
        bottomNavigation = findViewById(R.id.bottomNavigation)
        fragmentManagers = activity().supportFragmentManager
        presenter = DashboardPresenter(activity())
        notificationService = NotificationService()
    }

    private fun initComponents() {
        initListener()
        presenter?.initToolbar()
        bottomNavigation?.selectedItemId = R.id.home
        drawerLayout = findViewById(R.id.main_drawer_layout)
        actionBarDrawerToggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.menu_drawer_open, R.string.menu_drawer_close
        )
        drawerLayout!!.addDrawerListener(actionBarDrawerToggle!!)
        drawerLayout?.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        presenter?.getAds()
        presenter?.setNotifications()
    }

    fun handleNotification(intent: Intent){
        notifDetails = intent.extras?.getString("notification")
        val notification = Gson().fromJson(notifDetails, NotificationModel::class.java)
        if(notifDetails != null) {
            if (notifDetails?.isNotEmpty()!!) {
                presenter?.openNotifContent(notification)
            }
        }
    }



    private fun initListener() {
        fragmentManager = this.supportFragmentManager
        bottomNavigation?.setOnNavigationItemSelectedListener(presenter?.onNavigationItemSelectedListener)
    }

    fun onDashboardClicked(v: View?) {
        when(v?.id){
            R.id.imgDrawerIcon -> closeDrawer()
            R.id.drawerHome -> presenter?.openFragmentPage(0, HomeFragment(), "HomeFragment")
            R.id.drawerProfile -> presenter?.checkRestriction(R.id.drawerProfile)
            R.id.drawerTermsConditions -> presenter?.openFragmentPage(
                0,
                TermsFragment(),
                "TermsFragment"
            )
            R.id.drawerReviews -> presenter?.openPlayStore("com.radyopilipinomediagroup.radyo_now")
            R.id.drawerFAQs -> presenter?.openFragmentPage(0, FAQFragment(), "FAQFragment")
            R.id.drawerAppSettings -> presenter?.openFragmentPage(
                0,
                AppSettingsFragment(),
                "AppSettingsFragment"
            )
            R.id.drawerSignOut -> presenter?.signOutUser()
            R.id.drawerNotification -> presenter?.checkRestriction(R.id.drawerNotification)
        }
        closeDrawer()
    }



    private fun closeDrawer(){
        if (drawerLayout!!.isDrawerOpen(GravityCompat.START))
            drawerLayout!!.closeDrawer(
                GravityCompat.START
            )
    }

    override fun activity(): DashboardActivity {
        return this
    }

    override fun context(): Context {
        return this
    }

    override fun applicationContext(): Context {
        return applicationContext
    }

    override fun getToolbar(): androidx.appcompat.widget.Toolbar {
       return toolbar!!
    }

    override fun getPlaylistRecycler(): RecyclerView {
        return playlistRecycler!!
    }

    override fun getCoordinator(): CoordinatorLayout {
        return coordinatorLayout!!
    }

    override fun updateNotificationCount(count: Int) {
        if(count == 0) notificationCount?.visibility = View.GONE
        else {
            notificationCount?.visibility = View.VISIBLE
            notificationCount?.text = count.toString()
        }
    }

    override fun passData(data: Int) {
        chosenContent = data
        presenter?.checkRestriction(Constants.ADD_PLAYLIST)
    }

    override fun getBackStacked() {
        onBackPressed()
    }

    override fun openDrawer(drawerLayout: DrawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START)
    }

    override fun setBottomNav(id: Int) {
        bottomNavigation?.menu?.findItem(id)?.isChecked = true
    }

    override fun onDone() {
        presenter?.addContentToPlaylist(chosenContent!!)
    }

    override fun onCreatePlaylist() {
        presenter?.createPlaylist()
    }

    override fun playlistRecycler(recycler: RecyclerView) {
        playlistRecycler = recycler
    }

    override fun updatePlaylistData() {
        presenter?.updatePlaylistData()
    }

    private fun onLandscape() {
        bottomNavigation?.visibility= View.GONE
        toolbar?.visibility= View.GONE
    }

    private fun onPortrait() {
        bottomNavigation?.visibility= View.VISIBLE
        toolbar?.visibility= View.VISIBLE
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onBackPressed() {
        if(this.resources.configuration.orientation == 2){
            onPortrait()
            this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            return
        }
        if(presenter?.isOnMainMenu() == true){
            bottomNavigation?.selectedItemId = R.id.home
            return
        }
        if(presenter?.isOnHome() == true){
            when(presenter?.counter){
                0 -> {
                    presenter?.counter = presenter?.counter!! + 1
                    toast("Press again to exit.")
                }
                1 -> finish()
            }
            return
        }
        super.onBackPressed()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if(newConfig.orientation == 2){
            onLandscape()
        }else{
            onPortrait()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Terminate the notification
        notificationService?.destroyNotification(this)
    }

    override fun onPopupReady(data: AdsModel.Data) {
        Services.popUpAds(this, data)
    }

    override fun onSliderReady(data: AdsModel.Data) {
        print("onSliderReady ${Gson().toJson(data)}")
    }

    override fun onBannerReady(data: AdsModel.Data) {
        print("onBannerReady ${Gson().toJson(data)}")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
        } else {
            showWarningDialog(
            context = this,
            title = "Permission Denied",
            message = "You must enable the permission on the settings of the app to proceed.",
            callback = DialogInterface.OnClickListener { dialog, which ->
                when(which) {
                    BUTTON_POSITIVE -> {
                        startActivity(openPermissionSettings(packageName))
                    }
                }
                dialog.dismiss()
            })
        }
    }

    override fun onStart() {
        super.onStart()
        // Branch init
        Branch.getInstance().initSession({ referringParams, error ->
            if (error == null) {
                val result = Gson().fromJson(Gson().toJson(referringParams), DeepLinkResponse::class.java)
                println("Branch.io_onStart: ${Gson().toJson(referringParams)}")
                if (result.data?.clickedBranchLink == true) {
                    if(presenter?.checkRestriction(Constants.BRANCH_LINK_ACCESS) == true){
                        if (result.data?.contentType.equals("audio")) setFragmentDetails(
                            AudioStreamFragment(), result.data?.deeplinkPath,
                            result.data?.deeplinkPath, result.data?.title, result.data?.contentType, "AudioStreamFragment")
                        else setFragmentDetails(
                            FeaturedStreamFragment(), result.data?.deeplinkPath, result.data?.deeplinkPath,
                            result.data?.title, result.data?.contentType, "FeaturedStreamFragment")
                    }
                }
            } else Log.d("Branch-IO-Error: ", error.message)
        }, this.intent.data, this)
    }

    private fun setFragmentDetails(fragment: Fragment, stationId: String?, contentId: String?,
                                   stationName: String?, stationType: String?, tag: String?) {
        Services.changeFragment(
            (activity() as FragmentActivity).supportFragmentManager,
            fragment,
            tag!!,
            stationId!!,
            contentId!!,
            stationName!!,
            stationType!!
        )
    }



    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        this.intent = intent
        handleNotification(this.intent)
    }

    fun homeCountReset() {
        presenter?.counter = 0
        presenter?.setHome()
    }
}