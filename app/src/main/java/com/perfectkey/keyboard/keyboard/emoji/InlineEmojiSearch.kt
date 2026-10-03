// SPDX-License-Identifier: GPL-3.0-only
package com.perfectkey.keyboard.keyboard.emoji

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.graphics.drawable.GradientDrawable
import android.view.animation.DecelerateInterpolator
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.perfectkey.keyboard.event.Event
import com.perfectkey.keyboard.keyboard.internal.keyboard_parser.floris.KeyCode
import com.perfectkey.keyboard.latin.R
import com.perfectkey.keyboard.latin.common.ColorType
import com.perfectkey.keyboard.latin.settings.Settings
import com.perfectkey.keyboard.latin.utils.UiAnimations

/**
 * Emoji search inside the keyboard : the alphabet keyboard is shown, the typed words appear in a field above it
 * and the matching emojis are listed next to it. Key events are consumed here instead of going to the text field.
 */
class InlineEmojiSearch(
    private val panel: View,
    private val onSearchEmoji: (String) -> Unit,
    private val onClose: () -> Unit,
) {
    private val queryView: TextView = panel.findViewById(R.id.emoji_search_query)
    private val cursor: View = panel.findViewById(R.id.emoji_search_cursor)
    private val placeholder: TextView = panel.findViewById(R.id.emoji_search_placeholder)
    private val results: LinearLayout = panel.findViewById(R.id.emoji_search_results)
    private val query = StringBuilder()
    var isActive = false
        private set
    private var collapseAnimator: ValueAnimator? = null
    private val resultsScroll: View = results.parent as View
    private var resultsShown = false
    private var resultsAnimator: ValueAnimator? = null
    private val density = panel.resources.displayMetrics.density
    // the results row is 48dp high; the panel's bottom margin pulls the keyboard up into its empty top space
    private val resultsHeightPx get() = (48 * density).toInt()
    private val marginWithResultsPx get() = (-18 * density).toInt()
    private val marginWithoutResultsPx get() = (-10 * density).toInt()

    fun start() {
        collapseAnimator?.cancel() // a collapse that is still running is finished first (its end action hides the panel)
        collapseAnimator = null
        val colors = Settings.getValues().mColors
        val density = panel.resources.displayMetrics.density
        val pill = GradientDrawable().apply {
            cornerRadius = 18 * density
            setColor(colors.get(ColorType.EMOJI_SEARCH_BACKGROUND))
        }
        panel.findViewById<View>(R.id.emoji_search_field).background = pill
        val textColor = colors.get(ColorType.EMOJI_SEARCH_TEXT)
        queryView.setTextColor(textColor)
        cursor.setBackgroundColor(textColor)
        placeholder.setTextColor(textColor)
        placeholder.alpha = 0.5f
        panel.findViewById<android.widget.ImageView>(R.id.emoji_search_field_icon).apply { setColorFilter(textColor); alpha = 0.6f }
        panel.findViewById<android.widget.ImageView>(R.id.emoji_search_close).apply {
            setColorFilter(textColor)
            alpha = 0.6f
            setOnClickListener { onClose() }
        }
        query.setLength(0)
        setResultsShown(false, animate = false)
        isActive = true
        panel.visibility = View.VISIBLE
        UiAnimations.fadeInRising(panel)
        refresh()
    }

    /** [animated]: the panel collapses (height and fade) so the keyboard does not jump; false = gone immediately */
    fun stop(animated: Boolean = false) {
        isActive = false
        query.setLength(0)
        resultsAnimator?.cancel()
        panel.animate().cancel()
        val lp = panel.layoutParams as ViewGroup.MarginLayoutParams
        val startHeight = panel.height
        val startMargin = lp.bottomMargin
        val originalHeight = ViewGroup.LayoutParams.WRAP_CONTENT
        val finish = {
            if (!isActive) {
                panel.visibility = View.GONE
                results.removeAllViews()
            }
            // restore for the next time
            lp.height = originalHeight
            lp.bottomMargin = startMargin
            panel.layoutParams = lp
            panel.alpha = 1f
            panel.translationY = 0f
        }
        if (!animated || startHeight <= 0 || !UiAnimations.enabled(panel.context)) {
            finish()
            return
        }
        collapseAnimator = ValueAnimator.ofInt(startHeight, 0).apply {
            duration = UiAnimations.NORMAL_MS
            interpolator = DecelerateInterpolator(1.4f)
            addUpdateListener {
                val h = it.animatedValue as Int
                val f = h.toFloat() / startHeight
                lp.height = h
                lp.bottomMargin = Math.round(startMargin * f)
                panel.alpha = f
                panel.layoutParams = lp
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) = finish().let { }
            })
            start()
        }
    }

    /** @return true if the event was consumed by the search field */
    fun handleEvent(event: Event): Boolean {
        val keyCode = event.keyCode
        when {
            keyCode == KeyCode.DELETE -> {
                if (query.isNotEmpty()) query.setLength(query.offsetByCodePoints(query.length, -1))
            }
            keyCode == KeyCode.EMOJI || keyCode == KeyCode.CLIPBOARD || keyCode == KeyCode.SETTINGS -> {
                stop()
                return false
            }
            event.codePoint == '\n'.code -> {
                onClose()
                return true
            }
            keyCode >= 0 && event.codePoint >= 0 -> query.appendCodePoint(Character.toLowerCase(event.codePoint))
            else -> return false // shift, symbols, ... keep working normally
        }
        refresh()
        return true
    }

    fun handleText(text: CharSequence): Boolean {
        query.append(text.toString().lowercase())
        refresh()
        return true
    }

    private fun refresh() {
        queryView.text = query.toString()
        placeholder.visibility = if (query.isEmpty()) View.VISIBLE else View.INVISIBLE // invisible keeps the close button on the right
        showResults(EmojiSearchActivity.searchEmojis(query.toString().trim(), panel.context))
    }


    /** the results row only takes space when there is at least one emoji to show */
    private fun setResultsShown(show: Boolean, animate: Boolean) {
        if (show == resultsShown && resultsScroll.layoutParams.height == (if (show) resultsHeightPx else 0)) return
        resultsShown = show
        resultsAnimator?.cancel()
        val scrollLp = resultsScroll.layoutParams
        val panelLp = panel.layoutParams as ViewGroup.MarginLayoutParams
        val fromHeight = scrollLp.height.coerceAtLeast(0).let { if (it > resultsHeightPx) resultsHeightPx else it }
        val toHeight = if (show) resultsHeightPx else 0
        val apply = { h: Int ->
            val f = h.toFloat() / resultsHeightPx
            scrollLp.height = h
            panelLp.bottomMargin = Math.round(marginWithoutResultsPx + (marginWithResultsPx - marginWithoutResultsPx) * f)
            resultsScroll.alpha = f
            resultsScroll.layoutParams = scrollLp
            panel.layoutParams = panelLp
        }
        if (!animate || !UiAnimations.enabled(panel.context)) {
            apply(toHeight)
            return
        }
        resultsAnimator = ValueAnimator.ofInt(fromHeight, toHeight).apply {
            duration = UiAnimations.FAST_MS + 40
            interpolator = DecelerateInterpolator(1.4f)
            addUpdateListener { apply(it.animatedValue as Int) }
            start()
        }
    }

    private fun showResults(emojis: List<String>) {
        results.removeAllViews()
        setResultsShown(emojis.isNotEmpty(), animate = true)
        val density = panel.resources.displayMetrics.density
        emojis.take(60).forEach { emoji ->
            results.addView(TextView(panel.context).apply {
                text = emoji
                textSize = 30f
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams((46 * density).toInt(), ViewGroup.LayoutParams.MATCH_PARENT)
                setOnClickListener {
                    RecentEmojis.add(emoji)
                    onSearchEmoji(emoji)
                }
            })
        }
    }
}
