package com.taquangtu.forus.models

class Transaction(
    var date: Long,
    var name: String,
    var money: Int,
    var userId: String,
    val type: Int
) {
    var id: String = ""

    companion object {
        val TYPE_TRANSACTION = 0
        val TYPE_ADD_MONEY = 1
    }

    constructor() : this(0, "null", 0, "null", 0) {
        //for firebase parsing data
    }
}