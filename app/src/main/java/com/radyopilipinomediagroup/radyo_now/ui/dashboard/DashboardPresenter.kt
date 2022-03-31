package com.radyopilipinomediagroup.radyo_now.ui.dashboard

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.util.Log
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.FacebookSdk
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.radyopilipinomediagroup.radyo_now.R
import com.radyopilipinomediagroup.radyo_now.local.SessionManager
import com.radyopilipinomediagroup.radyo_now.model.GeneralResultModel
import com.radyopilipinomediagroup.radyo_now.model.ads.AdsModel
import com.radyopilipinomediagroup.radyo_now.model.notification.NotificationModel
import com.radyopilipinomediagroup.radyo_now.model.notification.NotificationResponseModel
import com.radyopilipinomediagroup.radyo_now.model.playlist.PlaylistListResultModel
import com.radyopilipinomediagroup.radyo_now.repositories.RetrofitService
import com.radyopilipinomediagroup.radyo_now.ui.AbstractPresenter
import com.radyopilipinomediagroup.radyo_now.ui.account.login.LoginActivity
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.favorites.FavoritesFragment
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.home.HomeFragment
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.home.playlist.PlaylistFragment
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.home.playlist.streams.AudioStreamFragment
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.news.NewsFragment
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.programs.ProgramsFragment
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.stations.StationsFragment
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.stations.featuredstreaming.FeaturedStreamFragment
import com.radyopilipinomediagroup.radyo_now.model.realm.NotificationLocal
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.home.drawer.notification.NotificationFragment
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.home.drawer.profile.ProfileFragment
import com.radyopilipinomediagroup.radyo_now.utils.*

class DashboardPresenter(var view: DashboardActivity) : AbstractPresenter<DashboardActivity>(view) {

    var counter: Int = 0
    private var supportActionBar = getToolbarText
    private var fragmentManager = view.activity().supportFragmentManager
    private var playlists : MutableList<PlaylistListResultModel.Data> = mutableListOf()
    private var checkboxPlaylistAdapter : CheckBoxListAdapter? = null
    var realm = RealmService
    private var realmListener = object: RealmService.RealmHandler{
        override fun onNotificationUpdate(
            count: Int,
            notificationList: List<NotificationLocal>,
            newCount: Int
        ) {
            if(!getSessionManager?.isGuest()!!) view.updateNotificationCount(newCount)
        }
    }

    init {
        FacebookSdk.sdkInitialize(view.activity())
        realm.setOnNotificationUpdateListener(realmListener)
        setNotifications()
        setGuetDrawer()
    }

    private fun setGuetDrawer() {
        val isGuest = getSessionManager?.getLoginType() == "guest"
        view.drawerSignOut?.text = if(isGuest) {
            val grayColor = ContextCompat.getColor(view.context(), R.color.gray)
            view.drawerNotificationTxt?.setTextColor(grayColor)
            view.drawerAppSettings?.setTextColor(grayColor)
            view.drawerReviews?.setTextColor(grayColor)
            view.drawerProfile?.setTextColor(grayColor)

            view.drawerAppSettings?.isEnabled = false
            view.drawerReviews?.isEnabled = false
//            view.drawerProfile?.isEnabled = false
//            view.drawerNotification?.isEnabled = false
            "Sign In"
        } else "Sign Out"
    }

    fun setNotifications() {
        println("setNotifications")
        try{
            getRepositories?.getNotifications(1,1,getSessionManager?.getToken()!!, object: RetrofitService.ResultHandler<NotificationResponseModel>{
                override fun onSuccess(data: NotificationResponseModel?) {
                    data?.data?.forEach {
                        realm.validateInsert(it)
                    }
                }
                override fun onError(error: NotificationResponseModel?) { }
                override fun onFailed(message: String) { }
            })
        }catch(e: Exception){
            println("setNotifications: ${e.localizedMessage}")
        }
    }

    fun signOutUser(){
        facebookGmailSignOut()
        getSessionManager?.setData(SessionManager.SESSION_STATUS, "logged_out")
        getSessionManager?.setLoginType("")
        Services.nextIntent(view.activity(), LoginActivity::class.java)
        view.activity().finish()
    }

    private fun facebookGmailSignOut(){
        try {
            //Facebook Sign Out
            LoginManager.getInstance().logOut()

            //Google Sign Out
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
            val googleSignInClient = GoogleSignIn.getClient(view.context(), gso)
            googleSignInClient.signOut()
        } catch (e : Exception) {}
    }

    private fun hideDrawerIcon() {
        getToolbarDrawerIcon!!.visibility = android.view.View.GONE
    }

    var onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener {
        when(it.itemId){
            R.id.home -> {
                supportActionBar?.text = ""
                Services.changeFragment(fragmentManager, HomeFragment(), "HomeFragment")
                return@OnNavigationItemSelectedListener updateBottomSelected(true)
            }
            R.id.programs -> {
                counter = 0
                supportActionBar?.text = "Programs"
                Services.changeFragment(fragmentManager, ProgramsFragment(), "ProgramsFragment")
                hideDrawerIcon()
                return@OnNavigationItemSelectedListener updateBottomSelected(true)
            }
            R.id.favorites -> {
                counter = 0
                return@OnNavigationItemSelectedListener updateBottomSelected(checkRestriction(R.id.favorites))
            }
            R.id.station -> {
                counter = 0
                supportActionBar?.text = "Channels"
                Services.changeFragment(fragmentManager, StationsFragment(), "StationsFragment")
                hideDrawerIcon()
                return@OnNavigationItemSelectedListener updateBottomSelected(true)
            }
            R.id.news -> {
                view.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://news.radyopilipino.com/")))
//                supportActionBar?.text = "News"
//
//                fragmentManager.beginTransaction()
//                    .replace(R.id.dashboardFrame, NewsFragment(), "NewsFragment").commit()
//                hideDrawerIcon()
                return@OnNavigationItemSelectedListener updateBottomSelected(false)
            }
            else ->  return@OnNavigationItemSelectedListener updateBottomSelected(false)
        }
    }

    fun updateBottomSelected(boolean: Boolean): Boolean{
        val menu = view.bottomNavigation?.menu
        menu?.forEach {
            println("homeCountReset ${it.itemId} - ${R.id.home} - ${(it.itemId == R.id.home)}")
            it.isChecked = (it.itemId == R.id.home)
        }
        return boolean
    }

    fun openPlayStore(url: String){
        try {
            view.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$url")))
        } catch (e: ActivityNotFoundException) {
            view.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$url")))
        }
    }

    fun openFragmentPage(visible: Int, fragment: Fragment, fragmentTag: String){
        view.bottomNavigation?.visibility = visible
        Services.changeFragment(fragmentManager, fragment, fragmentTag)
    }

    fun initToolbar(){
        view.activity().setSupportActionBar(view.getToolbar())
    }

    fun setFragmentDetails(fragment: Fragment, stationId: Int, contentId: Int,
                                   stationName: String, stationType: String, tag: String) {
        Services.changeFragment(
            (view.context() as FragmentActivity).supportFragmentManager,
            fragment,
            tag,
            stationId.toString(),
            contentId.toString(),
            stationName,
            stationType
        )
    }

    fun createPlaylist() {
        val dialog = Dialog(view.context(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.setContentView(R.layout.dialog_create_playlist)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val cancelCreatePlaylist = dialog.findViewById<ImageView>(R.id.cancelCreatePlaylist)
        val playlistName = dialog.findViewById<EditText>(R.id.playlistName)
        val createBtn = dialog.findViewById<TextView>(R.id.createBtn)

        cancelCreatePlaylist.setOnClickListener{
            dialog.dismiss()}
        createBtn.setOnClickListener{
            if(playlistName.text.toString().isEmpty() || playlistName.text.toString().isBlank()) it.context.toast("Please input a name.")
            else{
                playlistCreate(playlistName.text.toString())
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun playlistCreate(playlistName: String) {
        getRepositories?.createPlaylist(
            playlistName,
            getSessionManager?.getToken()!!,
            object : RetrofitService.ResultHandler<GeneralResultModel> {
                override fun onSuccess(data: GeneralResultModel?) {
                    Toast.makeText(
                        view.context(),
                        "Playlist successfully created!",
                        Toast.LENGTH_SHORT
                    ).show()
                    try {
                        val homeFragment = view.activity().supportFragmentManager.findFragmentByTag(
                            "HomeFragment"
                        )
                        val playlistFragment = view.activity().supportFragmentManager.findFragmentByTag(
                            "PlaylistFragment"
                        )
                        (playlistFragment as PlaylistFragment).updatePlaylistData()
                        (homeFragment as HomeFragment).updatePlaylistData()
                    } catch (e: Exception) {
                    }
                    (view.context() as DashboardActivity).updatePlaylistData()
                }

                override fun onError(error: GeneralResultModel?) {
                    Toast.makeText(view.context(), error?.message, Toast.LENGTH_SHORT).show()
                }

                override fun onFailed(message: String) {
                    Toast.makeText(view.context(), message, Toast.LENGTH_SHORT).show()
                }
            })
    }

    fun setupPlaylists() {
        updatePlaylistData()
        checkboxPlaylistAdapter = CheckBoxListAdapter(
            view.context(),
            playlists
        )
        view.getPlaylistRecycler().adapter = checkboxPlaylistAdapter
        view.getPlaylistRecycler().layoutManager = LinearLayoutManager(view.context())
    }

    fun updatePlaylistData(){
        playlists.clear()
        getRepositories?.getPlaylistList(getSessionManager?.getToken()!!,
            object : RetrofitService.ResultHandler<PlaylistListResultModel> {
                override fun onSuccess(data: PlaylistListResultModel?) {
                    try {
                        playlists.addAll(data?.data!!)
                        checkboxPlaylistAdapter?.notifyDataSetChanged()
                    } catch (e: Exception) {
                    }
                }

                override fun onError(error: PlaylistListResultModel?) {
                    Toast.makeText(view.context(), error?.message, Toast.LENGTH_SHORT).show()
                }

                override fun onFailed(message: String) {
                    Toast.makeText(view.context(), message, Toast.LENGTH_SHORT).show()
                }
            })
    }

    fun addContentToPlaylist(chosenContent: Int) {
            checkboxPlaylistAdapter?.getPlaylistId()?.forEach {
                Log.d("ContentPlaylistID", "$chosenContent - $it")
                getRepositories?.addContentToPlaylist(
                    it,
                    chosenContent,
                    getSessionManager?.getToken()!!,
                    object : RetrofitService.ResultHandler<GeneralResultModel> {
                        override fun onSuccess(data: GeneralResultModel?) {
                            Log.d("SuccessAdd", "${data?.message}")
                            Toast.makeText(
                                view.activity(),
                                "Successfully added to your playlist!",
                                Toast.LENGTH_SHORT
                            ).show()
                            try {
                                val homeFragment =
                                    view.activity().supportFragmentManager.findFragmentByTag(
                                        "HomeFragment"
                                    )
                                (homeFragment as HomeFragment).updatePlaylistData()
                            } catch (e: Exception) {
                            }
                        }

                        override fun onError(error: GeneralResultModel?) {
                            println(Gson().toJson(error))
                        }

                        override fun onFailed(message: String) {
                            println(message)
                        }
                    })
            }
    }

    fun playlistDialog() {
        Services.openPlaylistOptionsDialog(view.context())
    }

    fun getAds() {
        getRepositories?.getAds(getSessionManager?.getToken()!!,
            "homepage",
            object : RetrofitService.ResultHandler<AdsModel> {
                override fun onSuccess(data: AdsModel?) {
                    println("getAds: ${Gson().toJson(data)}")
                    data?.data?.forEach { ads ->
                        if (ads.active!!) {
                            if (ads.type == "popup") {
                                view.onPopupReady(ads)
                            } else if (ads.section == "homepage" && ads.type == "slider") view.onSliderReady(
                                ads
                            )
                            else if (ads.section == "homepage" && ads.type == "banner") view.onBannerReady(
                                ads
                            )
                        }
                    }
                }

                override fun onError(error: AdsModel?) {
                    println("getAdsError: ${Gson().toJson(error)}")
                }

                override fun onFailed(message: String) {
                    println("getAdsFailed: $message")
                }
            })
    }

    fun openNotifContent( notification : NotificationModel) {
        realm.updateNotificationRead(notification)
        when (notification.format) {
            "youtube",
            "video" -> {
                setFragmentDetails(
                    FeaturedStreamFragment(), notification.contentId?.toInt()!!, notification.contentId?.toInt()!!,
                    "", notification.format!!,"FeaturedStreamFragment")
            }
            "audio" -> {
                setFragmentDetails(
                    AudioStreamFragment(), notification.contentId?.toInt()!!, notification.contentId?.toInt()!!,
                    "", notification.format!!, "AudioStreamFragment")
            }
            else -> {
                view.toast("Content not found.")
            }
        }
    }

    fun checkRestriction(buttonId: Int) : Boolean{
        if(getSessionManager?.isGuest() == true) {
            view.context().guestWarningDialog()
            return false
        }
        when(buttonId){
            R.id.drawerNotification -> toNotification()
            R.id.drawerProfile -> toProfile()
            R.id.favorites -> toFavorites()
            Constants.ADD_PLAYLIST -> {
                playlistDialog()
                setupPlaylists()
            }
        }
        return true
    }
    fun checkRestrictionSilent(buttonId: Int) : Boolean {
        if (getSessionManager?.isGuest() == true) {
            return false
        }
        when(buttonId){}
        return true
    }

    private fun toFavorites() {
        supportActionBar?.text = "Favorites"
        Services.changeFragment(fragmentManager, FavoritesFragment(), "FavoritesFragment")
        hideDrawerIcon()
    }

    fun toNotification() {
        setNotifications()
        openFragmentPage(
            0,
            NotificationFragment(),
            "NotificationFragment"
        )
    }

    fun toProfile() {
        openFragmentPage(
            0,
            ProfileFragment(),
            "ProfileFragment"
        )
    }

    fun isOnMainMenu(): Boolean {
        val channels = view.supportFragmentManager.findFragmentByTag("ProgramsFragment")
        val programs = view.supportFragmentManager.findFragmentByTag("StationsFragment")
        val favorites = view.supportFragmentManager.findFragmentByTag("FavoritesFragment")

        return ((channels != null && channels.isVisible)
                || (programs != null && programs.isVisible)
                || (favorites != null && favorites.isVisible))
    }

    fun isOnHome(): Boolean {
        val home = view.supportFragmentManager.findFragmentByTag("HomeFragment")
        return (home != null && home.isVisible)
    }

    fun setHome(){
        val menuItem = view.bottomNavigation?.menu?.findItem(R.id.home)
        menuItem?.isChecked = true
    }

    interface View : AbstractView{
        fun getToolbar() : Toolbar
        fun getPlaylistRecycler() : RecyclerView
        fun getCoordinator() : CoordinatorLayout
        fun updateNotificationCount(count: Int)
    }
}