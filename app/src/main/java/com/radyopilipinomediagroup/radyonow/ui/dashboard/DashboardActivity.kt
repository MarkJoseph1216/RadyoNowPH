package com.radyopilipinomediagroup.radyonow.ui.dashboard

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.widget.Toolbar
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.radyopilipinomediagroup.radyonow.R
import com.radyopilipinomediagroup.radyonow.ui.AbstractPresenter
import com.radyopilipinomediagroup.radyonow.ui.dashboard.favorites.FavoritesFragment
import com.radyopilipinomediagroup.radyonow.ui.dashboard.home.HomeFragment
import com.radyopilipinomediagroup.radyonow.ui.dashboard.playlist.PlaylistFragment
import com.radyopilipinomediagroup.radyonow.ui.dashboard.profile.ProfileFragment

class DashboardActivity : AppCompatActivity(), AbstractPresenter.ContextView<DashboardActivity>, DashboardPresenter.View{

    private var bottomNavigation : BottomNavigationView? = null
    private var toolbar : androidx.appcompat.widget.Toolbar? = null
    private var fragmentManager : FragmentManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        initInitialize()
        initMain()
    }

    private fun initInitialize() {
        toolbar = findViewById(R.id.toolbar)
        bottomNavigation = findViewById(R.id.bottomNavigation)
    }

    private fun initMain() {
        setSupportActionBar(toolbar)
        initListener()
        bottomNavigation?.selectedItemId = R.id.home
    }

    private fun initListener() {
        fragmentManager = this.supportFragmentManager
        bottomNavigation?.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.home ->{
                    supportActionBar?.title = "Home"
                    fragmentManager?.beginTransaction()!!.replace(R.id.dashboardFrame, HomeFragment(), "HomeFragment").commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.favorites -> {
                    supportActionBar?.title = "Favourites"
                    fragmentManager?.beginTransaction()!!.replace(R.id.dashboardFrame, FavoritesFragment(), "FavoritesFragment").commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.playlists -> {
                    supportActionBar?.title = "Playlists"
                    fragmentManager?.beginTransaction()!!.replace(R.id.dashboardFrame, PlaylistFragment(), "PlaylistFragment").commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.profile -> {
                    supportActionBar?.title = "Profile"
                    fragmentManager?.beginTransaction()!!.replace(R.id.dashboardFrame, ProfileFragment(), "ProfileFragment").commit()
                    return@setOnNavigationItemSelectedListener true
                }
                else ->  return@setOnNavigationItemSelectedListener false
            }
        }
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
}