package com.journalmetro.app.ui.common.adapter
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import androidx.databinding.library.baseAdapters.BR

class OmniDataBindingViewHolder(val binding: ViewDataBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(viewTypeHolder: ViewTypeHolder<*, *>) {
        with(viewTypeHolder) {
            binding.setVariable(BR.isBigImageItem, isBigImageItem)
            binding.setVariable(BR.item, viewData)
            callback?.let { binding.setVariable(BR.callback, callback) }
            binding.executePendingBindings()
        }
    }
}