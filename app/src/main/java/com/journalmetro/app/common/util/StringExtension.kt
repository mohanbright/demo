package com.journalmetro.app.common.util

import androidx.core.text.HtmlCompat
import com.journalmetro.app.common.util.date.DateFormatter
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.text.Normalizer
import java.util.*
import java.util.regex.Pattern

// *****
//Notifications
const val tagFID: String = "installationId" //key to send firebase installationID
const val tagToken: String = "handle" //key to send firebase token
const val tagPlatform: String = "platform"  //key to send platform
const val tagAndroid: Int = 2

// *****
// Val_Constant.
private const val tagAdvertisement: String = "ads" // Type ads.
private const val tagBlockquote: String = "blockquote" // Type blockquote.
private const val tagCite: String = "cite" // Type text.
private const val tagP: String = "p" // Type text.
private const val tagH1: String = "h1" // Type text.
private const val tagH2: String = "h2" // Type text.
private const val tagH3: String = "h3" // Type text.
private const val tagH4: String = "h4" // Type text.
private const val tagH5: String = "h5" // Type text.
private const val tagH6: String = "h6" // Type text.
private const val tagHr: String = "hr" // Type thematic break.
private const val tagImage: String = "img" // Type image.
private const val tagImageGallery: String = "gallery" // Type gallery.
private const val tagSrc: String = "src" // Type src.
private const val tagCaption: String = "caption" // Type caption.
const val tagVideo: String = "video" // Type video.
private const val tagVideos: String = "videos" // Type video.
private const val tagVideoYoutube: String = "youtube-video" // Type video.
private const val tagVideoVimeo: String = "vimeo-video" // Type vimeo.
private const val tagPolygon: String = "Polygon" // Type polygon.
private const val tagMultiPolygon: String = "MultiPolygon" // Type multipolygon.
private const val tagPlaylistSpotify: String = "spotify-playlist" // Type spotify playlist.
private const val tagVideoDailyMotion: String = "dailymotion-video" // Type video.

private const val tagInstagramPost: String = "instagram-post" // Type instagram post.
private const val tagFacebookPost: String = "fb-post" // Type facebook normal post.
private const val tagFacebookVideo: String = "fb-video" // Type facebook video post.
private const val tagTwitterTweet: String = "twitter-tweet" // Type twitter tweet post.
private const val tagTikTokPost: String = "tiktok-post" // Type tiktok post.
private const val tagSoundCloudAudio: String = "soundcloud-audio" // Type sound cloud audio post.
private const val bulletPoint: String = "li" // Type bullet point in post text.

// These are usually using in home page.
private const val tagCard: String = "card" // Type card.
private const val tagListGroup: String = "list-group" // Type main object of block list.
private const val tagListGroupItem: String = "list-group-item" // Type list item without image.
private const val tagListGroupPhotoItem: String = "list-group-photo-item" // Type big image list item.
private const val tagListGroupTwoColsItem: String = "list-group-2-cols-item" // Type small image list item.
private const val tagListGroupTwoCols: String = "list-group-2-cols" // Type small image list item.
private const val tagAdvertisementItem: String = "ads-item" // Type item of Ads list.
private const val tagQuote: String = "quote" // Type quote.
private const val tagQuoteItem: String = "quote-item" // Type item of quote list.
private const val tagCarousel: String = "carousel" // Type horizontal list.
private const val tagCarouselItem: String = "carousel-item" // Type item of horizontal list without image.

// Firebase Analytics Event detail view
const val tagDetailsEvent: String = "ArticleDetailView" //used for sending ArticleDetails event
const val tagDetailsContentType: String = "Posts" //used for sending article details event
const val tagPostId: String = "postId" // Using for key for selected post id for event.
const val tagPostLink: String = "postLink" // Using for key for selected post link for event.
const val tagPostTitle: String = "postTitle" // Using for key for selected post title for event.

// Firebase Analytics Event section view
const val tagSectionEvent: String = "SectionView" //used for sending SectionView event
const val tagSectionContentType: String = "Categories" //key for sending content type as categories for event tagSectionEvent
const val tagSectionId: String = "categoryId" //key for selected categoryId for event tagSectionEvent

fun String.capitalizeWords(): String = split(" ").map { it.capitalize() }.joinToString(" ")

val DIACRITICS_AND_FRIENDS = Pattern.compile("[\\p{InCombiningDiacriticalMarks}\\p{Lm}\\p{Sk}]+")

val String.strippedDiacritics: String
    get() {
        Normalizer.normalize(this, Normalizer.Form.NFD)
        return DIACRITICS_AND_FRIENDS.matcher(this).replaceAll("")
    }

fun String.allCharactersFitThe8BitRange(): Boolean {
    val byteArrayString = this.toByteArray(charset("UTF8"))
    for (scalar in byteArrayString) {
        if (scalar in 0x0000..0x00FF) {
            continue
        } else {
            return false
        }
    }
    return true
}

val String.getUnicodeString: String
    get() {
        val string = this
        var text = ""
        try {
            val utf8Bytes = string.toByteArray(charset("UTF8"))
            text = String(utf8Bytes, Charset.forName("UTF-8"))
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        return text
    }

fun String.hasValidLengthAsPersonName(): Boolean {
    return (this.count() >= 1) && (this.count() < 21)
}

fun String.hasValidLengthAsCompanyInfo(): Boolean { return (this.count() >= 1) && (this.count() < 129) }

// Convert HTML text to readable text.
fun String?.getReadableTextFromHtml(): String {
    return if (this.isNullOrBlank()) ""
    else HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
}

// Check last items of text are new line chars? If yes, remove them.
// Why we need? Sometimes, text can have multi new line chars. It is bad for design.
fun String?.removeLastNewLineChars(): String {
    return if (this.isNullOrBlank()) ""
    else {
        var text = this.toString()

        do {
            text = text.replace("\n", "")
        }
        while (text.endsWith("\n"))

        text // Return.
    }
}

// Convert date to text in French.
// Do not need to call safe-string fun because they already have it.
fun String?.getReadableDateFR(): String {
    return if (this.isNullOrBlank()) ""
    else DateFormatter.parseIsoDateTimeDayNumberMonthNameYearNumber(
        this, Locale.CANADA_FRENCH)
}

// Convert date and time to text in French.
// Do not need to call safe-string fun because they already have it.
fun String?.getReadableDateWithTimeFR(): String {
    return if (this.isNullOrBlank()) ""
    else  DateFormatter.parseIsoDateTimeDayNumberMonthNameYearNumberHourMinute(
        this, Locale.CANADA_FRENCH)
}

// Convert date to text in English.
// Do not need to call safe-string fun because they already have it.
fun String?.getReadableDateEN(): String {
    return if (this.isNullOrBlank()) ""
    else  DateFormatter.parseIsoDateTimeDayNumberMonthNameYearNumber(
        this, Locale.ENGLISH)
}

// Get video id from video url for DailyMotion video player.
// Do not need to call safe-string fun because they already have it.
fun String?.getVideoIdForDailyMotionVideoPlayer(): String {
    if (this.isNullOrBlank() || this.trim().isEmpty()) return ""

    return if (this.contains("http")) {
        val lastIndex = this.lastIndexOf("/")
        this.substring(lastIndex + 1)
    } else this
}

// Get video map from video id. This is specifically for Daily Motion.
fun String.getVideoMapFromVideoIdForDailyMotionPlayer(): Map<String, String> {
    return mapOf(
        "video" to this,
        "autoplay" to "false",
        "ui-highlight" to "1D443E", // This is color value.
        "ui-logo" to "false",
        "queue-enable" to "false",
        "queue-autoplay-next" to "false"
    )
}

// Api response changed. That is why, some Strings can be null from list.
fun String?.getSafeSting(): String {
    return if (this.isNullOrBlank()) "" else this.getReadableTextFromHtml().removeLastNewLineChars()
}

// Check that taxonomy is ads taxonomy? They already have null-check.
fun String?.isTaxonomyOfAdvertisement(): Boolean {
    return if (this.isNullOrBlank()) false else this.replace(" ","") == tagAdvertisement
}

// Check that taxonomy is blockquote taxonomy? They already have null-check.
fun String?.isTaxonomyOfBlockquote(): Boolean {
    return if (this.isNullOrBlank()) false else this.replace(" ","") == tagBlockquote
}

// Check that taxonomy is cite taxonomy? They already have null-check.
fun String?.isTaxonomyOfCite(): Boolean {
    return if (this.isNullOrBlank()) false else this.replace(" ","") == tagCite
}

// Check that taxonomy is p taxonomy? They already have null-check.
fun String?.isTaxonomyOfP(): Boolean {
    return if (this.isNullOrBlank()) false else this.replace(" ","") == tagP
}

// Check that taxonomy is H3 taxonomy? They already have null-check.
fun String?.isTaxonomyOfHs(): Boolean {
    return if (this.isNullOrBlank()) false else {
        this.replace(" ","") == tagH1 ||
        this.replace(" ","") == tagH2 ||
        this.replace(" ","") == tagH3 ||
        this.replace(" ","") == tagH4 ||
        this.replace(" ","") == tagH5 ||
        this.replace(" ","") == tagH6
    }
}

// Check that taxonomy is hr? They already have null-check.
fun String?.isTaxonomyOfHr(): Boolean {
    return if (this.isNullOrBlank()) false else this.replace(" ","") == tagHr
}

// Check that taxonomy is image taxonomy? They already have null-check.
fun String?.isTaxonomyOfImage(): Boolean {
    return if (this.isNullOrBlank()) false else this.replace(" ","") == tagImage
}

// Check that taxonomy is gallery taxonomy? They already have null-check.
fun String?.isTaxonomyOfImageGallery(): Boolean {
    return if (this.isNullOrBlank()) false else this.replace(" ","") == tagImageGallery
}

// Check that taxonomy is src taxonomy? They already have null-check.
fun String?.isTaxonomyOfSrc(): Boolean {
    return if (this.isNullOrBlank()) false else this.replace(" ","") == tagSrc
}

// Check that taxonomy is caption taxonomy? They already have null-check.
fun String?.isTaxonomyOfCaption(): Boolean {
    return if (this.isNullOrBlank()) false else this.replace(" ","") == tagCaption
}

// Check that taxonomy is video taxonomy? They already have null-check.
fun String?.isTaxonomyOfVideo(): Boolean {
    return if (this.isNullOrBlank()) false else this.replace(" ","") == tagVideo
}

// Check that taxonomy is video taxonomy? They already have null-check.
fun String?.isTaxonomyOfVideos(): Boolean {
    return if (this.isNullOrBlank()) false else this.replace(" ","") == tagVideos
}

// Check that taxonomy is spotify playlist taxonomy? They already have null-check.
fun String?.isTaxonomyOfSpotifyPlaylist(): Boolean {
    return if (this.isNullOrBlank()) false else this.replace(" ","") == tagPlaylistSpotify
}

// Check that taxonomy is youtube video taxonomy? They already have null-check.
fun String?.isTaxonomyOfVideoYoutube(): Boolean {
    return if (this.isNullOrBlank()) false else this.replace(" ","") == tagVideoYoutube
}
// Check that taxonomy is youtube video taxonomy? They already have null-check.
fun String?.isTaxonomyOfVideoDailyMotion(): Boolean {
    return if (this.isNullOrBlank()) false else this.replace(" ","") == tagVideoDailyMotion
}

// Check that taxonomy is vimeo video taxonomy? They already have null-check.
fun String?.isTaxonomyOfVideoVimeo(): Boolean {
    return if (this.isNullOrBlank()) false else this.replace(" ","") == tagVideoVimeo
}

// Check that taxonomy is Card ? They already have null-check.
fun String?.isTaxonomyOfCard(): Boolean {
    return if (this.isNullOrBlank()) false else this.replace(" ","") == tagCard
}

// Check that taxonomy is ListGroup ? They already have null-check.
fun String?.isTaxonomyOfListGroup(): Boolean {
    return if (this.isNullOrBlank()) false else this.replace(" ","") == tagListGroup
}

// Check that taxonomy is ListGroupItem ? They already have null-check.
fun String?.isTaxonomyOfListGroupItem(): Boolean {
    return if (this.isNullOrBlank()) false else this.replace(" ","") == tagListGroupItem
}

// Check that taxonomy is ListGroupPhotoItem ? They already have null-check.
fun String?.isTaxonomyOfListGroupPhotoItem(): Boolean {
    return if (this.isNullOrBlank()) false else this.replace(" ","") == tagListGroupPhotoItem
}

// Check that taxonomy is ListGroupTwoColsItem ? They already have null-check.
fun String?.isTaxonomyOfListGroupTwoColsItem(): Boolean {
    return if (this.isNullOrBlank()) false else this.replace(" ","") == tagListGroupTwoColsItem
}

// Check that taxonomy is ListGroupTwoCols ? They already have null-check.
fun String?.isTaxonomyOfListGroupTwoCols(): Boolean {
    return if (this.isNullOrBlank()) false else this.replace(" ","") == tagListGroupTwoCols
}

// Check that taxonomy is AdvertisementItem ? They already have null-check.
fun String?.isTaxonomyOfAdvertisementItem(): Boolean {
    return if (this.isNullOrBlank()) false else this.replace(" ","") == tagAdvertisementItem
}

// Check that taxonomy is Quote ? They already have null-check.
fun String?.isTaxonomyOfQuote(): Boolean {
    return if (this.isNullOrBlank()) false else this.replace(" ","") == tagQuote
}

// Check that taxonomy is QuoteItem ? They already have null-check.
fun String?.isTaxonomyOfQuoteItem(): Boolean {
    return if (this.isNullOrBlank()) false else this.replace(" ","") == tagQuoteItem
}

// Check that taxonomy is Carousel ? They already have null-check.
fun String?.isTaxonomyOfCarousel(): Boolean {
    return if (this.isNullOrBlank()) false else this.replace(" ","") == tagCarousel
}

// Check that taxonomy is CarouselItem ? They already have null-check.
fun String?.isTaxonomyOfCarouselItem(): Boolean {
    return if (this.isNullOrBlank()) false else this.replace(" ", "") == tagCarouselItem
}

// Check that taxonomy is Multi-Polygon ? They already have null-check.
fun String?.isTaxonomyOfMultiPolygon(): Boolean {
    return if (this.isNullOrBlank()) false else this.replace(" ", "") == tagMultiPolygon
}
// Check taxonomy is InstagramPost ?
fun String?.isTaxonomyOfInstagramPost(): Boolean {
    return if (this.isNullOrBlank()) false else this.replace(" ","") == tagInstagramPost
}

// Check taxonomy is FacebookPost ?
fun String?.isTaxonomyOfFacebookPost(): Boolean {
    return if (this.isNullOrBlank()) false else this.replace(" ","") == tagFacebookPost
}

// Check taxonomy is FacebookVideo ?
fun String?.isTaxonomyOfFacebookVideo(): Boolean {
    return if (this.isNullOrBlank()) false else this.replace(" ","") == tagFacebookVideo
}

// Check taxonomy is TwitterTweet ?
fun String?.isTaxonomyOfTwitterTweet(): Boolean {
    return if (this.isNullOrBlank()) false else this.replace(" ","") == tagTwitterTweet
}

// Check taxonomy is TikTokPost ?
fun String?.isTaxonomyOfTikTokPost(): Boolean {
    return if (this.isNullOrBlank()) false else this.replace(" ","") == tagTikTokPost
}

// Check taxonomy is SoundCloud ?
fun String?.isTaxonomyOfSoundCloud(): Boolean {
    return if (this.isNullOrBlank()) false else this.replace(" ","") == tagSoundCloudAudio
}

// Check taxonomy is BulletPoint ?
fun String?.isTaxonomyOfBulletPoint(): Boolean {
    return if (this.isNullOrBlank()) false else this.replace(" ", "") == bulletPoint
}