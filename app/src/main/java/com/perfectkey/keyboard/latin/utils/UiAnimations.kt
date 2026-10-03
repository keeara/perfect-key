// SPDX-License-Identifier: GPL-3.0-only
package com.perfectkey.keyboard.latin.utils

import android.content.Context
import android.provider.Settings
import android.view.View
import android.view.animation.DecelerateInterpolator

/** Small, quick transitions. Nothing here is used on the typing path, and everything is skipped if system animations are off. */
object UiAnimations {
    const val FAST_MS = 100L
    const val NORMAL_MS = 160L
    private val decelerate = DecelerateInterpolator(1.6f)

    @JvmStatic
    fun enabled(context: Context): Boolean =
        Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) != 0f

    /** fade in while rising from [riseDp] below the final position */
    @JvmStatic @JvmOverloads
    fun fadeInRising(view: View, riseDp: Float = 8f, duration: Long = NORMAL_MS) {
        view.animate().cancel()
        if (!enabled(view.context)) {
            view.alpha = 1f
            view.translationY = 0f
            return
        }
        view.alpha = 0f
        view.translationY = riseDp * view.resources.displayMetrics.density
        view.animate().alpha(1f).translationY(0f).setDuration(duration).setInterpolator(decelerate).start()
    }

    /** fade out while sinking, then run [end] (also runs immediately if animations are off) */
    @JvmStatic @JvmOverloads
    fun fadeOutSinking(view: View, riseDp: Float = 6f, duration: Long = FAST_MS, end: Runnable) {
        view.animate().cancel()
        if (!enabled(view.context)) {
            end.run()
            return
        }
        view.animate().alpha(0f).translationY(riseDp * view.resources.displayMetrics.density)
            .setDuration(duration).withEndAction {
                view.alpha = 1f
                view.translationY = 0f
                end.run()
            }.start()
    }

    @JvmStatic @JvmOverloads
    fun crossfadeIn(view: View, duration: Long = FAST_MS) {
        view.animate().cancel()
        if (!enabled(view.context)) {
            view.alpha = 1f
            return
        }
        view.alpha = 0f
        view.animate().alpha(1f).setDuration(duration).setInterpolator(decelerate).start()
    }

    @JvmStatic @JvmOverloads
    fun setAlphaAnimated(view: View, alpha: Float, duration: Long = FAST_MS) {
        view.animate().cancel()
        if (!enabled(view.context)) view.alpha = alpha
        else view.animate().alpha(alpha).setDuration(duration).start()
    }
}
