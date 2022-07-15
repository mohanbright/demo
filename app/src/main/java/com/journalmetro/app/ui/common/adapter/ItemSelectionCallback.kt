package com.journalmetro.app.ui.common.adapter

/**
 * T: Item is to understand data.
 * V: View is to understand view.
 */
interface ItemSelectionCallback<T, V> {
    fun onItemSelected(item: T, view: V)
}