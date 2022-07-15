package com.journalmetro.app.common.util

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import com.journalmetro.app.R

fun hideKeyboard(activity: Activity) {
    val inputMethodManager =
        activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    // Check if no view has focus
    val currentFocusedView = activity.currentFocus
    currentFocusedView?.let {
        inputMethodManager.hideSoftInputFromWindow(
            currentFocusedView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS
        )
    }
}


fun Fragment.hideTheKeyboard() {
    view?.let { activity?.hideTheKeyboard(it) }
}

fun Fragment.showTheKeyboard() {
    view?.let { activity?.showTheKeyboard(it) }
}

fun Activity.hideTheKeyboard() {
    hideTheKeyboard(currentFocus ?: View(this))
}

fun Activity.showTheKeyboard() {
    showTheKeyboard(currentFocus ?: View(this))
}

fun Context.hideTheKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Context.showTheKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
}

//Extension on ImageView to set Tint
fun ImageView.setTint(@ColorRes colorRes: Int) {
    ImageViewCompat.setImageTintList(
        this, ColorStateList.valueOf(
            ContextCompat.getColor(
                context,
                colorRes
            )
        )
    )
}


fun isValidEmail(text: String): Boolean {
    val emailRegEx = "([a-zA-Z0-9_\\.-]{1,64})@[([a-zA-Z0-9_\\.-])\\.([a-zA-Z\\.])]{4,64}"
    val pattern = Regex(emailRegEx)
    return pattern.matches(text)
}

fun ByteArray.toHex(): String {
    return joinToString("") { "%02x".format(it) }
}

fun Any?.castToLong(): Long {
    return if (this is Double) {
        this.toLong()
    } else {
        this as Long
    }
}

fun Any?.castToDouble(): Double {
    return if (this is Long) {
        this.toDouble()
    } else {
        this as Double
    }
}

/**
 * Creates and add a progress bar to view layout.
 */
fun ViewGroup.createAndAddProgressBar(
    params: ViewGroup.LayoutParams,
    tagName: String
): ProgressBar {


    return ProgressBar(context).also {
        it.tag = tagName

        it.elevation =
            context.resources.getDimension(R.dimen.size_top_bar_detail_margin)

        it.isIndeterminate = true
        it.layoutParams = params

        this.apply {
            findViewWithTag<View>(tagName)?.let { view ->
                removeView(view)
            }
            addView(it)
        }
    }

}

