package com.journalmetro.app.ui.expandView

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.journalmetro.app.databinding.LayoutExpandGallaryViewBinding
import com.journalmetro.app.post.model.ContentRendered
import com.journalmetro.app.post.model.Post
import com.journalmetro.app.ui.common.adapter.GalleryPagerAdapter


class ExpandGalleryViewBottomSheetDialog : BottomSheetDialogFragment() {

    private lateinit var binding: LayoutExpandGallaryViewBinding
    companion object {
        const val TAG : String = "ExpandGalleryViewBottomSheetDialog"
        var list : List<ContentRendered> = emptyList()
        lateinit var post : Post
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutExpandGallaryViewBinding.inflate(LayoutInflater.from(context), container, false)
        binding.pagerAdapter = GalleryPagerAdapter(requireContext(), list, null)
        binding.post = post

        binding.topButtonClose.setOnClickListener { this.dismiss() }

        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), theme)

        //Set full screen behaviour
        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            parentLayout?.let { it ->
                val behaviour = BottomSheetBehavior.from(it)
                behaviour.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        
                    }
                    override fun onSlide(bottomSheet: View, slideOffset: Float) {

                    }
                })

                setupFullHeight(it)
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        return dialog
    }

    private fun setupFullHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        bottomSheet.layoutParams = layoutParams
    }

}