package com.milwen.wbpo_app

import android.os.SystemClock
import android.view.View
import com.milwen.wbpo_app.application.App
import java.util.regex.Pattern

private var lastClicked = 0L
fun View.onDoubleTouchProtectClick(body: ()->Unit){
    setOnClickListener {
        if (SystemClock.elapsedRealtime() - lastClicked >= 1000){
            lastClicked = SystemClock.elapsedRealtime()
            body()
        }
    }
}

fun isEmailValid(email: String): Boolean {
    val emailValidationRegex = Pattern.compile("^(((([a-zA-Z]|\\d|[!#\\$%&'\\*\\+\\-\\/=\\?\\^_`{\\|}~]|[\\x{00A0}-\\x{D7FF}\\x{F900}-\\x{FDCF}\\x{FDF0}-\\x{FFEF}])+(\\.([a-zA-Z]|\\d|[!#\\$%&'\\*\\+\\-\\/=\\?\\^_`{\\|}~]|[\\x{00A0}-\\x{D7FF}\\x{F900}-\\x{FDCF}\\x{FDF0}-\\x{FFEF}])+)*)|((\\x22)((((\\x20|\\x09)*(\\x0d\\x0a))?(\\x20|\\x09)+)?(([\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x7f]|\\x21|[\\x23-\\x5b]|[\\x5d-\\x7e]|[\\x{00A0}-\\x{D7FF}\\x{F900}-\\x{FDCF}\\x{FDF0}-\\x{FFEF}])|(\\([\\x01-\\x09\\x0b\\x0c\\x0d-\\x7f]|[\\x{00A0}-\\x{D7FF}\\x{F900}-\\x{FDCF}\\x{FDF0}-\\x{FFEF}]))))*(((\\x20|\\x09)*(\\x0d\\x0a))?(\\x20|\\x09)+)?(\\x22)))@((([a-zA-Z]|\\d|[\\x{00A0}-\\x{D7FF}\\x{F900}-\\x{FDCF}\\x{FDF0}-\\x{FFEF}])|(([a-zA-Z]|\\d|[\\x{00A0}-\\x{D7FF}\\x{F900}-\\x{FDCF}\\x{FDF0}-\\x{FFEF}])([a-zA-Z]|\\d|-|\\.|_|~|[\\x{00A0}-\\x{D7FF}\\x{F900}-\\x{FDCF}\\x{FDF0}-\\x{FFEF}])*([a-zA-Z]|\\d|[\\x{00A0}-\\x{D7FF}\\x{F900}-\\x{FDCF}\\x{FDF0}-\\x{FFEF}])))\\.)+(([a-zA-Z]|[\\x{00A0}-\\x{D7FF}\\x{F900}-\\x{FDCF}\\x{FDF0}-\\x{FFEF}])|(([a-zA-Z]|[\\x{00A0}-\\x{D7FF}\\x{F900}-\\x{FDCF}\\x{FDF0}-\\x{FFEF}])([a-zA-Z]|\\d|-|_|~|[\\x{00A0}-\\x{D7FF}\\x{F900}-\\x{FDCF}\\x{FDF0}-\\x{FFEF}])*([a-zA-Z]|[\\x{00A0}-\\x{D7FF}\\x{F900}-\\x{FDCF}\\x{FDF0}-\\x{FFEF}]{2,})))\\.?$")
    return emailValidationRegex.matcher(email).matches()
}

fun View.setVisible(){ visibility = View.VISIBLE }
fun View.setVisible(on: Boolean){ visibility = if(on) View.VISIBLE else View.INVISIBLE }
fun View.setInvisible(){ visibility = View.INVISIBLE }
fun View.setGone(){ visibility = View.GONE }
fun View.setVisibleNotGone(on: Boolean, useAnim: Boolean = false){
    if (useAnim){
        animate().alpha(if (on) 1.0f else 0.0f).run {
            duration = 400L
            withEndAction {
                visibility = if(on) View.VISIBLE else View.GONE
            }
        }
    } else {
        visibility = if(on) View.VISIBLE else View.GONE
    }
}

fun post(delay: Int = 0, body: () -> Unit){
    App.mainHandler.postDelayed(body, delay.toLong())
}

fun post(delay: Int, runnable: Runnable){
    App.mainHandler.postDelayed(runnable, delay.toLong())
}