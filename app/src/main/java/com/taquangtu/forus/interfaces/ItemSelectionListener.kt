package com.taquangtu.forus.interfaces

interface ItemSelectionListener<T> {
    fun onItemSelected(item: T, position: Int)
}