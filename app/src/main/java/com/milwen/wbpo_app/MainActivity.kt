package com.milwen.wbpo_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.milwen.wbpo_app.application.App
import com.milwen.wbpo_app.registration.view.RegistrationFragment
import com.milwen.wbpo_app.ui.main.BaseFragment
import com.milwen.wbpo_app.ui.main.MainFragment
import java.util.ArrayList
import java.util.HashMap

class MainActivity : AppCompatActivity() {

    var isFragmentStarting = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            startFragment(RegistrationFragment())
        }
    }

    private var fragmentResultReceivers = HashMap<BaseFragment, BaseFragment.FragmentResult<*>>()
    private fun getTopFragment(): BaseFragment? = getFragments().lastOrNull()

    private fun makeFragmentTransaction(body: (FragmentTransaction) -> Unit) {
        with(supportFragmentManager.beginTransaction()) {
            body(this)
            commitAllowingStateLoss()
        }
    }

    fun startFragmentForResult(f: BaseFragment, receiver: BaseFragment.FragmentResult<*>, hidePrevFragment: Boolean = true) {
        fragmentResultReceivers[f] = receiver
        startFragment(f, hidePrevFragment = hidePrevFragment)
    }

    fun <T> returnFragmentResult(f: BaseFragment, result: T) {
        val rf = fragmentResultReceivers.remove(f)
        removeFragment(f) {
            rf?.let {
                @Suppress("UNCHECKED_CAST")
                (it as BaseFragment.FragmentResult<T>).onFragmentResult(result)
            } ?: App.warn("No result receiver")
        }
    }

    fun startFragment(
        f: BaseFragment,
        allowGoBack: Boolean = true,
        hidePrevFragment: Boolean = true) {
        App.log("MainActivity: startFragment: ${f.title}")
        App.log("FragmentArray: ${getFragments().size}")
        makeFragmentTransaction { ft ->
            ft.setCustomAnimations(f.animIn, f.animOut)
            val tag = f.javaClass.simpleName
            if (allowGoBack && f.canGoBack) {
                if (hidePrevFragment){
                    // hide previous fragment
                    getTopFragment()?.let {
                        ft.hide(it)
                        it.onHide()
                    }
                }
                ft.add(R.id.container, f, tag)
            } else {
                fragmentResultReceivers.values.forEach { it.onFragmentResultCanceled() }
                fragmentResultReceivers.clear()
                ft.replace(R.id.container, f, tag)
            }
            ft.runOnCommit {
                updateActivityUI()
            }
        }
    }

    private fun getFragments(): List<BaseFragment> {
        return ArrayList<BaseFragment>().apply {
            for (e in supportFragmentManager.fragments) {
                if (e is BaseFragment) add(e)
            }
        }
    }

    fun removeFragment(f: BaseFragment, onCommit: (() -> Unit)? = null) {
        makeFragmentTransaction { ft ->
            ft.setCustomAnimations(f.animIn, f.animOut)
            ft.remove(f)
            val frgs = getFragments()
            val fI = frgs.indexOf(f)
            if (fI == frgs.lastIndex && fI > 0) {
                // show previous fragment again - only if removed fragment is last one
                val af = frgs[fI - 1]
                af.onShowAgain()
                ft.show(af)
            }
            ft.setReorderingAllowed(false)
            ft.runOnCommit {
                afterFragmentRemoval(f)
                onCommit?.invoke()
            }
        }
    }

    private fun afterFragmentRemoval(f: Fragment) {
        fragmentResultReceivers.remove(f)?.onFragmentResultCanceled()
        updateActivityUI()
    }

    private fun updateActivityUI() {
        supportActionBar?.let { ab ->
            getTopFragment()?.let {
                title = it.title
                ab.setDisplayHomeAsUpEnabled(it.showBackButton)
            }?: let {
                title = null
                ab.setDisplayHomeAsUpEnabled(false)
            }
        }
    }
}