package com.journalmetro.app.ui.common.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.viewpager.widget.PagerAdapter
import com.journalmetro.app.common.util.*
import com.journalmetro.app.databinding.LayoutGalleryImagesBinding
import com.journalmetro.app.post.model.ContentRendered

class GalleryPagerAdapter(
    var context: Context,
    var contentRenderList: List<ContentRendered>,
    var callback: ImageSelectionCallback?,
    val scaleImageWidth :Boolean = false
) : PagerAdapter() {

    override fun getCount(): Int = contentRenderList.size

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as LinearLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val binding = LayoutGalleryImagesBinding.inflate(LayoutInflater.from(context), container, false)

        val contentRenderItem: ContentRendered = contentRenderList[position]

        //set content in image
        bindContent(contentRenderItem, binding)
        container.addView(binding.root)

        binding.postImageview.setOnClickListener {
            if (callback == null)
                return@setOnClickListener
            callback?.onImageSelected(position)
        }

        return binding.root
    }

    private fun bindContent(item: ContentRendered, binding: LayoutGalleryImagesBinding) {
        // Use this as option for caption.
        fun findAlternateCaption() {
            if (binding.caption.isNullOrEmpty()) {
                binding.imageUrl?.let {
                    URLUtil.guessFileName(it, null, null).substringBefore(".")
                }?.let { caption ->
                    binding.caption = caption
                }
            }
        }
        when {
            item.taxonomy.isTaxonomyOfImage() -> {
                if (!item.content.isNullOrEmpty()) {
                    binding.imageUrl = item.content.getSafeSting()
                    findAlternateCaption()
                } else if (!item.child.isNullOrEmpty()) {
                    for (child in item.child) {
                        when {
                            child.taxonomy.isTaxonomyOfCaption() -> binding.caption =
                                child.content.getSafeSting()
                            child.taxonomy.isTaxonomyOfSrc() -> {
                                binding.imageUrl =
                                    child.content.getSafeSting(); findAlternateCaption()
                            }
                        }
                    }
                }
            }

            item.taxonomy.isTaxonomyOfSrc() -> {
                binding.imageUrl = item.content.getSafeSting(); findAlternateCaption()
            }
            item.taxonomy.isTaxonomyOfCaption() -> binding.caption = item.content.getSafeSting()
            else -> {
                //NoView is caption or image
            }
        }
        if (scaleImageWidth)
            binding.postImageview.scaleType = ImageView.ScaleType.CENTER_CROP
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)
    }

    interface ImageSelectionCallback {
        fun onImageSelected(position : Int)
    }
}