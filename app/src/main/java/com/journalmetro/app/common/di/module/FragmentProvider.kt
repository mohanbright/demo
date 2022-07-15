package com.journalmetro.app.common.di.module

import com.journalmetro.app.ui.articles.MyArticleListFragment
import com.journalmetro.app.ui.details.article.ArticleDetailFragment
import com.journalmetro.app.ui.home.list.HomeListFragment
import com.journalmetro.app.ui.local.LocalFragment
import com.journalmetro.app.ui.search.SearchFragment
import com.journalmetro.app.ui.sections.SectionsFragment
import com.journalmetro.app.ui.sections.news.SectionNewsListFragment
import com.journalmetro.app.ui.details.video.VideoDetailFragment
import com.journalmetro.app.ui.home.main.HomeMainFragment
import com.journalmetro.app.ui.notification.NotificationListFragment
import com.journalmetro.app.ui.videos.VideoListFragment
import com.journalmetro.app.ui.web.WebViewFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentProvider {

    // *****
    // Bottom bar fragments.
    @ContributesAndroidInjector
    abstract fun contributeHomeMainFragment() : HomeMainFragment

    @ContributesAndroidInjector
    abstract fun contributeLocalFragment() : LocalFragment

    @ContributesAndroidInjector
    abstract fun contributeMyArticleListFragment() : MyArticleListFragment

    @ContributesAndroidInjector
    abstract fun contributeVideoListFragment() : VideoListFragment

    @ContributesAndroidInjector
    abstract fun contributeSectionsFragment() : SectionsFragment

    // *****
    // Extra page fragments.
    @ContributesAndroidInjector
    abstract fun contributeSectionNewsListFragment() : SectionNewsListFragment

    @ContributesAndroidInjector
    abstract fun contributeSearchFragment(): SearchFragment

    @ContributesAndroidInjector
    abstract fun contributeHomeListFragment() : HomeListFragment

    @ContributesAndroidInjector
    abstract fun contributeWebViewFragment() : WebViewFragment

    // *****
    // Detail fragments.
    @ContributesAndroidInjector
    abstract fun contributeArticleDetailFragment() : ArticleDetailFragment
    // *****
    // Video Detail fragments.
    @ContributesAndroidInjector
    abstract fun contributeVideoDetailFragment() : VideoDetailFragment

    //*****
    //Notification List
    @ContributesAndroidInjector
    abstract fun contributeNotificationListFragment() : NotificationListFragment
}