package com.taquangtu.forus.contexts

import java.util.*

class AppContext {
    companion object{
        lateinit var userId: String
        lateinit var roomId: String
        var appIsVisible: Boolean = false
    }
}