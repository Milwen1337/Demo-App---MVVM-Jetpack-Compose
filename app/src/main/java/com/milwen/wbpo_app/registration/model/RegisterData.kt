package com.milwen.wbpo_app.registration.model

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable

class RegisterData : BaseObservable() {
    @get:Bindable
    var email: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.email)
        }

    @get:Bindable
    var password: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.password)
        }
}
