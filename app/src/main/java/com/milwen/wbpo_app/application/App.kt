package com.milwen.wbpo_app.application

import android.app.*
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.*
import android.util.Log
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import com.milwen.wbpo_app.BuildConfig
import com.milwen.wbpo_app.MainActivity
import com.milwen.wbpo_app.MainViewModel
import com.milwen.wbpo_app.database.AppDatabase
import dagger.BindsInstance
import dagger.Component
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Singleton


@HiltAndroidApp
class App : Application(){

    @Suppress("unused")
    companion object {
        private const val LOG_TAG = "WBPO-App"
        lateinit var instance: App
            private set
        val mainHandler: Handler by lazy {
            HandlerThread("MyHandlerThread").let {
                it.start()
                Handler(it.looper)
            }
        }

        fun log(s: String) = Log.i(LOG_TAG, s)
        fun debug(s: String){ if(BuildConfig.DEBUG) Log.d(LOG_TAG, s) }
        fun warn(s: String){ if(BuildConfig.DEBUG) Log.w(LOG_TAG, s) }
        fun logLongString(s: String){
            val maxLogSize = 1000
            s.chunked(maxLogSize).forEach { log(it) }
        }

        fun showToast(c: Context, @StringRes id: Int, long: Boolean = false) = showToast(c, c.getText(id), long)
        fun showToast(c: Context, s: CharSequence, long: Boolean = false) {
            Toast.makeText(c, s, Toast.LENGTH_LONG).show()
        }
    }

    private lateinit var connMgr: ConnectivityManager
    private var _isNetworkConnected = false
    val isNetworkConnected: Boolean get(){
        checkNetworkConnected()
        return _isNetworkConnected
    }

    private fun checkNetworkConnected(){
        @Suppress("DEPRECATION")
        _isNetworkConnected = connMgr.activeNetworkInfo?.isConnected?:false
    }

    private fun initNetworkConnectivityCheck(){
        val b = NetworkRequest.Builder()
        b.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        val nr = b.build()
        val cb = object : ConnectivityManager.NetworkCallback(){
            fun checkNetworkConnectedAndNotify(){
                if(isNetworkConnected) { }
            }

            override fun onAvailable(network: Network) {
                checkNetworkConnectedAndNotify()
            }
            override fun onLost(network: Network) {
                checkNetworkConnectedAndNotify()
            }
        }
        connMgr.registerNetworkCallback(nr, cb)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        log("StabilityCheck: App.onCreate() - start")
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        connMgr = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        checkNetworkConnected()
        initNetworkConnectivityCheck()
        log("StabilityCheck: App.onCreate() - end")
    }
}

