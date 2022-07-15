package com.journalmetro.app.homeSection.model

import android.os.Parcelable
import com.journalmetro.app.common.data.model.Model
import com.journalmetro.app.common.util.getReadableDateFR
import com.journalmetro.app.common.util.getReadableTextFromHtml
import com.journalmetro.app.common.util.getSafeSting
import com.journalmetro.app.common.util.isTaxonomyOfListGroupItem
import com.journalmetro.app.post.model.Category
import com.journalmetro.app.post.model.FeaturedMedia
import kotlinx.parcelize.Parcelize

/**
 * Created by App Developer on July/2021.
 */
@Parcelize
data class HomeSection (
    val id: Long,
    val taxonomy: String?,
    val position: Long?,
    val subtitle: String?,
    val categoryID: Long?,
    val category: Category?,
    val pageSectionItems: List<PageSectionItem>?,
) : Model(), Parcelable

@Parcelize
data class PageSectionItem (
    val id: Long,
    val taxonomy: String?,
    val order: Long?,
    val pageSectionID: Long?,
    val postID: Long?,
    val quoteID: Long?,
    val post: HomePost?,
    val quote: Quote?,
) : Model(), Parcelable

@Parcelize
data class HomePost (
    val id: Long,
    val createdAt: String?,
    val updatedAt: String?,
    val title: String?,
    val excerpt: String?,
    val featuredMedia: FeaturedMedia?,
    val videoURL: String?,
    val type: String?,
    val categories: List<Category>?,
    val mainCategoryName: String?,
    var isBreakingNews: Boolean = false,
    var isSavedByUser: Boolean = false, // This data does not come from API.
    var taxonomy: String = "", // This data does not come from API. We assign PageSectionItem taxonomy to here.
    var order: String = "", // This data does not come from API. It is using for horizontal order number of UI.
) : Model(), Parcelable {
    fun getReadableDateFormat() = createdAt.getReadableDateFR()
    fun getParsedExcerpt() = excerpt.getReadableTextFromHtml()
    fun getParsedTitle() = title.getReadableTextFromHtml()
    fun getParsedCategoryName() = mainCategoryName.getSafeSting()
    fun isVisibleCategoryName() = mainCategoryName.getSafeSting().isNotBlank()
}

@Parcelize
data class Quote (
    val id: Long,
    val title: String?,
    val content: String?,
    val author: String?,
    val authorRole: String?,
    val backgroundColor: String?,
    val textColor: String?
) : Model(), Parcelable
