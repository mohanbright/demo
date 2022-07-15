package com.journalmetro.app.ui.common.fragment

import android.util.Log
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.AdRequest
import com.google.android.material.tabs.TabLayout
import com.journalmetro.app.R
import com.journalmetro.app.common.util.getSafeSting
import com.journalmetro.app.common.util.isSafelyTrue
import com.journalmetro.app.post.model.ContentRendered
import com.journalmetro.app.post.model.Post
import com.journalmetro.app.ui.MainActivity

/*
fun <T> createItemSelectionCallback(block: (T) -> Unit): ItemSelectionCallback<T> {
    return object : ItemSelectionCallback<T> {
        override fun onItemSelected(item: T) {
            block.invoke(item)
        }
    }
}*/

fun BaseFragment<*, *>.actionBarTitle(title: String) {
    (activity as? MainActivity)?.setActionBarTitle(title)
}
fun BaseFragment<*, *>.hideTitleBar() {
    (activity as? MainActivity)?.hideTitleBar()
}

fun Fragment.hideActionBar() {
    (activity as? MainActivity)?.hideActionBar()
}

fun Fragment.showActionBar() {
    (activity as? MainActivity)?.showActionBar()
}

fun Fragment.homeAsUpEnabled(enabled: Boolean) {
    (activity as? MainActivity)?.homeAsUpEnabled(enabled)
}

fun Fragment.getSupportBar():ActionBar {
  return (activity as? MainActivity)?.getSupportBar()!!
}

fun Fragment.getAdRequest(): AdRequest {
    return (activity as? MainActivity)?.getAdRequest()!!
}

fun Fragment.sendEmail(message: String = "") {
    (activity as? MainActivity)?.sendEmail(message)
}

fun Fragment.hideBottomNavigationView() {
    (activity as? MainActivity)?.hideBottomNavigationView()
}

fun Fragment.showBottomNavigationView() {
    (activity as? MainActivity)?.showBottomNavigationView()
}

fun Fragment.showShareDialog(text: String) {
    (activity as? MainActivity)?.showSharingDialog(text)
}

fun Fragment.showSoftKeyboard(view: View) {
    (activity as? MainActivity)?.showSoftKeyboard(view)
}

fun Fragment.setupListenerMessageLoadingError(onClickLoadingErrorTryAgainButton: () -> Unit) {
    (activity as? MainActivity)?.setupListenerMessageLoadingError(onClickLoadingErrorTryAgainButton)
}

fun Fragment.callGeneralLoadingErrorMessageShow() {
    (activity as? MainActivity)?.showMessageLoadingError()
}

fun BaseFragment<*, *>.actionBarCustom(restId: Int) {
    (activity as? MainActivity)?.setActionBarCustom(restId)
}

fun BaseFragment<*, *>.actionBarCustom(view: View) {
    (activity as? MainActivity)?.setActionBarCustom(view)
}

fun Fragment.isClickSaveButton(view: View): Boolean {
    return view.id == R.id.item_save || view.id == R.id.heading_item_save || view.id == R.id.heading_item_save2 // Remember to use same id in items for save button. heading_item_save2 for big item saved icon
}

fun createTabSelectedListener(tabSelectedBlock: (position: Int?) -> Unit): TabLayout.OnTabSelectedListener {
    return object : TabLayout.OnTabSelectedListener {
        override fun onTabReselected(tab: TabLayout.Tab?) {
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {
        }

        override fun onTabSelected(tab: TabLayout.Tab?) {
            tabSelectedBlock(tab?.position)
        }

    }
}

fun Fragment.navigateTo(directions: NavDirections) {
    try {
        findNavController().navigate(directions)

        this.callGeneralLoadingErrorMessageHide() // Remove loading error message when user change.
    }
    catch (e: IllegalArgumentException) {
        Log.e("Fragment","Catching potential duplicate navigation event")
    }
}

// This is using to remove loading error message when users travel between pages and success answer.
fun Fragment.callGeneralLoadingErrorMessageHide() {
    (activity as? MainActivity)?.removeMessageLoadingError()
}

// Send section id to google analytics.
fun Fragment.sendLogsToGoogleSectionId(sectionId: Long) {
    (activity as? MainActivity)?.sendLogsToGoogleSectionId(sectionId)
}

// Send post id to google analytics.
fun Fragment.sendLogsToGooglePostData(post: Post) {
    (activity as? MainActivity)?.sendLogsToGooglePostData(post)
}

fun Fragment.isAdsActive(): Boolean {
    return (activity as? MainActivity)?.isAdsAvailable().isSafelyTrue()
}

fun Fragment.getGeneralAdsUnitId(): String {
    return (activity as? MainActivity)?.getAdsUnitId().getSafeSting()
}

fun Fragment.openGalleryExpandViewBottomSheetFragmentDialog(post: Post, list : List<ContentRendered>) {
    return (activity as? MainActivity)!!.openGalleryExpandViewBottomSheetFragmentDialog(post, list)
}