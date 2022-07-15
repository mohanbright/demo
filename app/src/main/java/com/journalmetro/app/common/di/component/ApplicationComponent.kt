package com.journalmetro.app.common.di.component

import android.app.Application
import com.journalmetro.app.common.app.ThisApplication
import com.journalmetro.app.common.di.module.*
import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton


@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        AppModule::class,
        ViewModelModule::class,
        ActivityBuilderModule::class,
        HttpModule::class,
        DataSourceModule::class,
        RepositoryModule::class,
        DatabaseModule::class
    ]
)
interface ApplicationComponent {

    fun inject(application: ThisApplication)

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): ApplicationComponent
    }
}