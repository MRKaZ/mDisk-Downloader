package com.mrkazofficial.mdiskdownloader.activities

import android.os.Bundle
import android.os.StrictMode
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.mrkazofficial.mdiskdownloader.R
import com.mrkazofficial.mdiskdownloader.fragments.DashboardFragment
import com.mrkazofficial.mdiskdownloader.fragments.DownloadsFragment
import com.mrkazofficial.mdiskdownloader.fragments.SettingsFragment
import com.mrkazofficial.mdiskdownloader.utils.PermissionUtils.grantPermissions
import nl.joery.animatedbottombar.AnimatedBottomBar

class MainActivity : AppCompatActivity() {

    private var dashboardFragment = DashboardFragment()
    private var downloadsFragment = DownloadsFragment()
    private var settingsFragment = SettingsFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().permitAll().build())

        this.grantPermissions()

        val bottomBar = findViewById<AnimatedBottomBar>(R.id.bottomBar)
        initializeFragment(fragment = dashboardFragment)
        bottomBar.selectTabById(R.id.bottomDashboard, animate = true)
        bottomBar.onTabIntercepted = {
            when (it.id) {
                R.id.bottomDashboard -> initializeFragment(fragment = dashboardFragment)
                R.id.bottomDownloads -> initializeFragment(fragment = downloadsFragment)
                R.id.bottomSettings -> initializeFragment(fragment = settingsFragment)
            }
            true
        }
    }

    private fun initializeFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayout, fragment)
        transaction.commit()
    }
}