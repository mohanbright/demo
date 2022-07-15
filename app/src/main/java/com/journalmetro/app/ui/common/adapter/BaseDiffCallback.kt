package com.journalmetro.app.ui.common.adapter
import androidx.recyclerview.widget.DiffUtil

open class BaseDiffCallback<Model>(
    private var oldList: List<Model>,
    private var newList: List<Model>,
    private var areItemsTheSame: ((oldItem: Model, newItem: Model) -> Boolean),
    private var areContentTheSame: ((oldItem: Model, newItem: Model) -> Boolean)
) :
    DiffUtil.Callback(
    ) {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return areItemsTheSame(oldList[oldItemPosition], newList[newItemPosition])
    }

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return areContentTheSame(oldList[oldItemPosition], newList[newItemPosition])
    }

}