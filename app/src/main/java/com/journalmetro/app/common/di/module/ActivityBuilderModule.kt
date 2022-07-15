package com.journalmetro.app.common.di.module

import com.journalmetro.app.ui.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilderModule {

    @ContributesAndroidInjector(modules = [FragmentProvider::class])
    abstract fun bindMainActivity(): MainActivity
}
