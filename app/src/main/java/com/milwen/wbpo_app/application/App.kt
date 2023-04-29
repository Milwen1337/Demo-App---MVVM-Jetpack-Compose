package com.milwen.wbpo_app.application

import android.app.*
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.*
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.milwen.wbpo_app.BuildConfig

class App : Application(){

    @Suppress("unused")
    companion object {
        private const val LOG_TAG = "WBPO-App"
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
        log("StabilityCheck: App.onCreate() - start")
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        connMgr = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        checkNetworkConnected()
        initNetworkConnectivityCheck()
        log("StabilityCheck: App.onCreate() - end")
    }
}

