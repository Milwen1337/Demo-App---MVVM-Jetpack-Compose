package com.milwen.wbpo_app.application


fun post(delay: Int = 0, body: () -> Unit){
    App.mainHandler.postDelayed(body, delay.toLong())
}

fun post(delay: Int, runnable: Runnable){
    App.mainHandler.postDelayed(runnable, delay.toLong())
}