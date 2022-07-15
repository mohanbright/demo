package com.journalmetro.app.common.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.journalmetro.app.common.di.ViewModelKey
import com.journalmetro.app.ui.MainViewModel
import com.journalmetro.app.ui.articles.MyArticleListViewModel
import com.journalmetro.app.ui.common.viewmodel.ViewModelFactory
import com.journalmetro.app.ui.details.article.ArticleDetailViewModel
import com.journalmetro.app.ui.home.list.HomeListViewModel
import com.journalmetro.app.ui.local.LocalViewModel
import com.journalmetro.app.ui.search.SearchViewModel
import com.journalmetro.app.ui.sections.SectionsViewModel
import com.journalmetro.app.ui.sections.news.SectionNewsListViewModel
import com.journalmetro.app.ui.details.video.VideoDetailViewModel
import com.journalmetro.app.ui.home.main.HomeMainViewModel
import com.journalmetro.app.ui.notification.NotificationViewModel
import com.journalmetro.app.ui.videos.VideoListViewModel
import com.journalmetro.app.ui.web.WebViewViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    abstract fun bindViewModelFactory(viewModelFactory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainVM(viewModel: MainViewModel): ViewModel

    // *****
    // ViewModels of bottom bar fragments.
    @Binds
    @IntoMap
    @ViewModelKey(HomeMainViewModel::class)
    abstract fun bindHomeMainVM(viewModel: HomeMainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LocalViewModel::class)
    abstract fun bindLocalVM(viewModel: LocalViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MyArticleListViewModel::class)
    abstract fun bindMyArticlesListVM(viewModel: MyArticleListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(VideoListViewModel::class)
    abstract fun bindVideoListVM(viewModel: VideoListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SectionsViewModel::class)
    abstract fun bindSectionsVM(viewModel: SectionsViewModel):ViewModel

    // *****
    // ViewModels of extra page fragments.
    @Binds
    @IntoMap
    @ViewModelKey(SectionNewsListViewModel::class)
    abstract fun bindSectionNewsListVM(viewModel: SectionNewsListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SearchViewModel::class)
    abstract fun bindSearchVM(viewModel: SearchViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HomeListViewModel::class)
    abstract fun bindHomeListVM(viewModel: HomeListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(WebViewViewModel::class)
    abstract fun bindWebViewVM(viewModel: WebViewViewModel): ViewModel

    // *****
    // ViewModels of detail fragments.
    @Binds
    @IntoMap
    @ViewModelKey(ArticleDetailViewModel::class)
    abstract fun bindArticleDetailVM(viewModel: ArticleDetailViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(VideoDetailViewModel::class)
    abstract fun bindVideoDetailVM(viewModel: VideoDetailViewModel): ViewModel

    // *****
    // ViewModels of Notifications fragments.
    @Binds
    @IntoMap
    @ViewModelKey(NotificationViewModel::class)
    abstract fun bindNotificationDetailVM(viewModel: NotificationViewModel): ViewModel
}