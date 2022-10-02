package com.mrkazofficial.mdiskdownloader.activities

import android.app.Activity
import android.content.*
import android.os.*
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.mrkazofficial.mdiskdownloader.R
import com.mrkazofficial.mdiskdownloader.fragments.DashboardFragment
import com.mrkazofficial.mdiskdownloader.fragments.DownloadsFragment
import com.mrkazofficial.mdiskdownloader.fragments.SettingsFragment
import com.mrkazofficial.mdiskdownloader.services.DownloaderService
import com.mrkazofficial.mdiskdownloader.utils.Constants.DOWNLOAD_NOTIFICATION_ACTION_CANCEL_ARG
import com.mrkazofficial.mdiskdownloader.utils.Constants.DOWNLOAD_NOTIFICATION_TASK_ID_ARG
import com.mrkazofficial.mdiskdownloader.utils.Constants.LOCAL_BROADCAST_SERVICE_WORKER
import com.mrkazofficial.mdiskdownloader.utils.Constants.MESSENGER_TASK
import com.mrkazofficial.mdiskdownloader.utils.Constants.SAT_CONNECTION_ARG
import com.mrkazofficial.mdiskdownloader.utils.Constants.SERVICE_ACTION_SHUTDOWN_ARG
import com.mrkazofficial.mdiskdownloader.utils.PermissionUtils.grantPermissions
import com.mrkazofficial.mdiskdownloader.utils.Utils
import com.yausername.youtubedl_android.YoutubeDL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nl.joery.animatedbottombar.AnimatedBottomBar
import kotlin.system.exitProcess

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private var dashboardFragment = DashboardFragment()
    private var downloadsFragment = DownloadsFragment()
    private var settingsFragment = SettingsFragment()

    private var sMessenger: Messenger? = null
    private var aMessenger: Messenger = Messenger(IncomingServiceHandler(this))

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

        runCatching {
            lifecycleScope.launchWhenStarted {
                val result = withContext(Dispatchers.IO) {
                    YoutubeDL.getInstance().updateYoutubeDL(this@MainActivity)
                }
                Log.d(TAG, "YT-DLP UPDATE RESULTS -> ${result.name}")
            }
        }
    }

    private fun initializeFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayout, fragment)
        transaction.commit()
    }

    private fun checkAndStartDownService() = lifecycleScope.launch {
        if (!Utils.checkIfServiceIsRunning(
                context = applicationContext,
                DownloaderService::class
            )
        ) {
            Intent(this@MainActivity, DownloaderService::class.java).also {
                it.putExtra(SAT_CONNECTION_ARG, aMessenger)
                startService(it)
                bindService(it, DownloaderServiceConnection(), 0)
            }
        }
    }


    private class IncomingServiceHandler(private var mainActivity: MainActivity) :
        Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.obj as String) {
                "" -> {}
            }
        }
    }

    /** Service connection through activity and the downloader service */
    private inner class DownloaderServiceConnection : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            sMessenger = Messenger(service)
            Log.d(TAG, "Downloader Service Connection Connected!")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            sMessenger = null
            unbindService(this)
            Log.d(TAG, "Downloader Service Connection Disconnected!")
        }
    }

    private var downloaderServiceReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let { it ->
                if (it.action == LOCAL_BROADCAST_SERVICE_WORKER) {
                    when (it.getStringExtra(MESSENGER_TASK)) {
                        DOWNLOAD_NOTIFICATION_ACTION_CANCEL_ARG -> {
                            val taskId = it.getStringExtra(DOWNLOAD_NOTIFICATION_TASK_ID_ARG)
                            if (taskId != null) {
                                Log.e(TAG, "CANCEL RECEIVED -> $taskId")
                                cancelDownload(taskId = taskId)
                            }
                        }
                        SERVICE_ACTION_SHUTDOWN_ARG -> {
                            Log.e(TAG, "APP SHUTDOWN RECEIVED")
                            shutdown()
                        }
                    }
                }
            }
        }
    }

    private fun cancelDownload(taskId: String) {
        sMessenger?.send(Message().apply {
            data.putString(MESSENGER_TASK, DOWNLOAD_NOTIFICATION_ACTION_CANCEL_ARG)
            data.putString(DOWNLOAD_NOTIFICATION_TASK_ID_ARG, taskId)
        })
    }

    private fun shutdown(){
        sMessenger?.send(Message().apply {
            data.putString(MESSENGER_TASK, SERVICE_ACTION_SHUTDOWN_ARG)
        })
        runCatching {
            finishAffinity()
            exitProcess(0)
        }
    }

    private fun Activity.registerReceivers() {
        with(LocalBroadcastManager.getInstance(this@registerReceivers)) {
            registerReceiver(
                downloaderServiceReceiver,
                IntentFilter(LOCAL_BROADCAST_SERVICE_WORKER)
            )
        }
    }

    private fun Activity.unRegisterReceivers() {
        with(LocalBroadcastManager.getInstance(this@unRegisterReceivers)) {
            unregisterReceiver(downloaderServiceReceiver)
        }
    }

    override fun onStart() {
        super.onStart()
        checkAndStartDownService()
        registerReceivers()
    }

    override fun onDestroy() {
        super.onDestroy()
        unRegisterReceivers()
    }
}
