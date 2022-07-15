package com.journalmetro.app.common.app

import android.app.Application
import com.facebook.FacebookSdk
import com.google.firebase.FirebaseApp
import com.journalmetro.app.common.di.component.ApplicationComponent
import com.journalmetro.app.common.di.component.DaggerApplicationComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import net.danlew.android.joda.JodaTimeAndroid
import javax.inject.Inject


class ThisApplication : Application(), HasAndroidInjector {

    private lateinit var component: ApplicationComponent

    @Inject
    lateinit var androidInjector : DispatchingAndroidInjector<Any>

    init {
        instance = this
    }

    companion object {
        private var instance: ThisApplication? = null

        fun applicationComponent(): ApplicationComponent? = instance?.component
    }

    override fun onCreate() {
        super.onCreate()
        component = DaggerApplicationComponent.builder()
            .application(this)
            .build()
        component.inject(this)

        JodaTimeAndroid.init(this)
        //Initialize Firebase App
        FirebaseApp.initializeApp(this)
        FacebookSdk.setAutoInitEnabled(true)
        // Initialize the SDK
        //Places.initialize(this, "")
    }



    override fun androidInjector(): AndroidInjector<Any> = androidInjector

}