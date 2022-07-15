package com.journalmetro.app.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.journalmetro.app.ui.MainActivity

/**
 * Created by App Developer on May/2021.
 */
class SplashActivity : AppCompatActivity() {

    private val delayDuration: Long = 3000
    private lateinit var viewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Not using for now.
        viewModel = ViewModelProvider(this).get(SplashViewModel::class.java)

        // Create delay for 3000 ms.
        createDelay()
    }

    private fun createDelay() {
        Handler(Looper.getMainLooper()).postDelayed({

            // Directly open Main Activity.
            openMainActivity()

        }, delayDuration)
    }

    private fun openMainActivity() {
        // Start main activity.
        startActivity(Intent(this@SplashActivity, MainActivity::class.java))

        // Close activity.
        finish()
    }
}