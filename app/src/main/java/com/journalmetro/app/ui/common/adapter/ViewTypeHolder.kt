package com.journalmetro.app.ui.common.adapter

data class ViewTypeHolder<T, Callback>(
    val viewData: T,
    val layoutResId: Int,
    val callback: Callback? = null,
    val isBigImageItem: Boolean = false,
) {
    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        with(other as ViewTypeHolder<*, *>) {
            return (this@ViewTypeHolder.viewData == this.viewData
                    && this@ViewTypeHolder.isBigImageItem == this.isBigImageItem
                    && this@ViewTypeHolder.callback == this.callback)
        }
    }

    override fun hashCode(): Int {
        return viewData.hashCode()
    }
}