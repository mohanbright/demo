package com.journalmetro.app.ui.common.binding

import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.dailymotion.android.player.sdk.PlayerWebView
import com.dailymotion.android.player.sdk.events.ApiReadyEvent
import com.dailymotion.android.player.sdk.events.PlaybackReadyEvent
import com.dailymotion.android.player.sdk.events.StartEvent
import com.google.android.gms.ads.admanager.AdManagerAdView
import com.journalmetro.app.R
import com.journalmetro.app.common.util.*
import com.journalmetro.app.post.model.Post
import com.journalmetro.app.ui.MainActivity
import com.journalmetro.app.ui.common.view.LayoutManagerFactory

object BindingAdapters {
    @JvmStatic
    @BindingAdapter("android:visibility")
    fun showHide(view: View, show: Boolean) {
        view.visibility = if (show) View.VISIBLE else View.GONE
    }


}

@BindingAdapter(
        value = ["setAdapter", "setOrientation", "disableAnimation"],
        requireAll = false
)
fun bindRecyclerViewAdapter(
        recyclerView: RecyclerView,
        adapter: RecyclerView.Adapter<*>?,
        orientation: Int = RecyclerView.HORIZONTAL,
        disableAnimation: Boolean = false
) {
    recyclerView.apply {
        setHasFixedSize(false)
        layoutManager = LayoutManagerFactory.createLinearLayoutManager(this, orientation)
        if (disableAnimation) itemAnimator = null else DefaultItemAnimator()
        this.adapter = adapter
    }
}

@BindingAdapter(
    value = ["pagerAdapter"],
    requireAll = false
)
fun bindViewPagerAdapter(
    viewPager: ViewPager,
    adapter: PagerAdapter,
) {
    viewPager.apply {
        this.adapter = adapter
    }
}

@BindingAdapter(
        value = ["typefaceRes"],
        requireAll = true
)
fun typefaceRes(view: TextView, typefaceRes: String) {
    val typefaceResId = view.resources.getIdentifier(typefaceRes, "font", view.context.packageName)
    val typeface = ResourcesCompat.getFont(view.context, typefaceResId)
    if (typeface != null) {
        view.typeface = typeface
    }
}

// Upload image (First look cache, then send request).
@BindingAdapter("imageUrl")
fun loadImage(view: ImageView, url: String?) {
    var unfittedURL = url
    if (url != null) {
        unfittedURL = url.split("fit")[0]
    }
    Glide.with(view).load(unfittedURL)
        .placeholder(R.drawable.ic_place_holder)
        .error(R.drawable.ic_place_holder)
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
        .into(view)
}

// Upload small image (First look cache, then send request).
@BindingAdapter("loadImageAsSmall")
fun loadImageSmallSize(view: ImageView, url: String?) {
    Glide.with(view).load("$url?w=200")
        .placeholder(R.drawable.ic_place_holder)
        .error(R.drawable.ic_place_holder)
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
        .into(view)
}

// Upload video for video list first item.
@BindingAdapter("loadVideo")
fun loadVideo(player: PlayerWebView, videoUrl: String?) {

    if (videoUrl.getSafeSting().isNotBlank()) { // We have playing video url.

        val videoId = videoUrl.getVideoIdForDailyMotionVideoPlayer() // Get video id from url.

        // Initialize media player.
        initializeDailyMotionVideoPlayer(
            player,
            videoId.getVideoMapFromVideoIdForDailyMotionPlayer(),
            videoId
        )
    }
}

@BindingAdapter("adManagerLoadAd")
fun loadAds(adManagerAdView: AdManagerAdView, isAdsTaxonomy: Boolean?) {
    if (isAdsTaxonomy.isSafelyTrue()) adManagerAdView.loadAd(MainActivity().getAdRequest())
}

@BindingAdapter("adManagerLoadAdFromItem")
fun loadAdsFromItem(adManagerAdView: AdManagerAdView, item: Post) {

    val idView = adManagerAdView.adUnitId // View ads unit id.
    val idModel = item.adsUnitId // Model ads unit id.

    // Load ads if defined ads id and model ads id are equal.
    // Otherwise, do not load and hide it.
    if (idView == idModel) {
        adManagerAdView.visibility = View.VISIBLE
        adManagerAdView.loadAd(MainActivity().getAdRequest()) // Load ads.
    }
    else adManagerAdView.visibility = View.GONE
}

/*@BindingAdapter("postList")
fun bindPostList(recyclerView: RecyclerView, posts: List<Post>) {
    val adapter = recyclerView.adapter as? LocalNewsAdapter
    adapter?.submitList(posts)
}*/

@BindingAdapter("isRefreshing")
fun bindSwipeRefresh(swipeRefreshLayout: SwipeRefreshLayout, isRefreshing: Boolean) {
    swipeRefreshLayout.isRefreshing = isRefreshing
}

//Initialize DailyMotion video player.
private fun initializeDailyMotionVideoPlayer(
    dailyMotionPlayer: PlayerWebView,
    param: Map<String, String>,
    videoId: String
) {

    dailyMotionPlayer.setIsWebContentsDebuggingEnabled(false) // Set debugging if it is enable.

    dailyMotionPlayer.load(params = param) // Set params which are settings map.

    dailyMotionPlayer.toggleControls() // Controls.

    dailyMotionPlayer.showControls(false) // Hide player controls.

    dailyMotionPlayer.unmute() // Turn sound on by default.

    dailyMotionPlayer.scaleMode(PlayerWebView.SCALE_MODE_FIT) // Set player fit on screen.

    dailyMotionPlayer.play() // Play player media.

    val progressBar = (dailyMotionPlayer.parent as? RelativeLayout)?.let {
        it.createAndAddProgressBar(
            RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            ).apply { addRule(RelativeLayout.CENTER_IN_PARENT) }, videoId
        )
    }
    dailyMotionPlayer.setEventListener { event ->
        when (event) {
            is ApiReadyEvent, is StartEvent -> progressBar?.visibility = View.VISIBLE
            is PlaybackReadyEvent -> progressBar?.visibility = View.GONE
            else -> {}
        }
    }

}