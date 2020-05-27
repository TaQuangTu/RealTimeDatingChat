package com.taquangtu.forus.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class BaseViewModel: ViewModel() {
    open var mLiveMessage = MutableLiveData<String>() //for message or error
}