package com.journalmetro.app.ui.common.view

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.util.AttributeSet
import com.journalmetro.app.R

/**
 * A ios style waiting spinner.
 *
 * @constructor Creates an waiting spinner imageView with animation.
 */

class WaitingSpinnerImageView (context: Context, attrs: AttributeSet): androidx.appcompat.widget.AppCompatImageView(context, attrs) {

    init {
        this.setBackgroundResource(R.drawable.animation)
    }

    /**
     * Starts the waiting spinner animation.
     */
    fun animateWaitingSpinner(){
        val animationDrawable = this.background as AnimationDrawable
        animationDrawable.start()
    }

    /**
     * Stops the waiting spinner animation
     */
    fun stopAnimatingWaitingSpinner(){
        val animationDrawable = this.background as AnimationDrawable
        animationDrawable.stop()
    }

}