package com.milwen.wbpo_app.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.milwen.wbpo_app.MainActivity
import com.milwen.wbpo_app.R
import com.milwen.wbpo_app.application.App
import kotlinx.coroutines.*

/**
 * Base for any fragment.
 */
abstract class BaseFragment : Fragment(), CoroutineScope by MainScope() {
    protected lateinit var app: App

    open val titleId: Int = 0 // title of the fragment
    open val title: String = ""
    abstract val debugTitle: String
    open val canGoBack = false // if set, activity shows back button, and this fragment is added on top of previous
    open val showBackButton get() = canGoBack
    open val isToolbarVisible = true

    open val animIn = R.animator.fade_in
    open val animOut = R.animator.fade_out

    val mainActivity: MainActivity? get() = activity as? MainActivity

    override fun onCreate(si: Bundle?) {
        super.onCreate(si)
        app = requireActivity().application as App
    }

    open fun onBack(){}

    protected fun removeThis(onCommit: (()->Unit)? = null){
        mainActivity?.removeFragment(this, onCommit = onCommit)
    }

    protected fun isNetworkConnected(): Boolean = app.isNetworkConnected

    abstract class FragmentResult<in T>{
        abstract fun onFragmentResult(result: T)
        open fun onFragmentResultCanceled(){}
    }

    private var fragmentStarting = false

    open fun onHide(){}
    open fun onShowAgain(){
        App.log("BaseFragment: onShowAgain: fragmentStarting: $fragmentStarting")
        mainActivity?.isFragmentStarting = false
        fragmentStarting = false
    }

    override fun onResume() {
        super.onResume()
        App.log("BaseFragment: onResume: fragmentStarting: $fragmentStarting")
        mainActivity?.isFragmentStarting = false
        fragmentStarting = false
    }

    fun startFragment(
        f: BaseFragment,
        hidePrevFragment: Boolean = true){
        App.log("BaseFragment: startFragment: fragmentStarting: $fragmentStarting")
        val isStarting = mainActivity?.isFragmentStarting?:false
        if (!isStarting) {
            mainActivity?.isFragmentStarting = true
            fragmentStarting = true
            App.log("BaseFragment: startFragment: fragmentStarting(set true): $fragmentStarting")
            mainActivity?.startFragment(f, hidePrevFragment = hidePrevFragment)
        }
    }

    fun startFragmentForResult(f: BaseFragment, receiver: FragmentResult<*>, hidePrevFragment: Boolean = true){
        val isStarting = mainActivity?.isFragmentStarting?:false
        if (!isStarting) {
            mainActivity?.isFragmentStarting = true
            fragmentStarting = true
            mainActivity?.startFragmentForResult(f, receiver, hidePrevFragment = hidePrevFragment)
        }
    }

    fun <T> returnFragmentResult(result: T){
        mainActivity?.returnFragmentResult(this, result)
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineContext.cancel()
    }
}