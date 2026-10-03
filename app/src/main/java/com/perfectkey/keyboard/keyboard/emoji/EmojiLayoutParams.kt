/*
 * Copyright (C) 2013 The Android Open Source Project
 * modified
 * SPDX-License-Identifier: Apache-2.0 AND GPL-3.0-only
 */
package com.perfectkey.keyboard.keyboard.emoji

import android.content.res.Resources
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.viewpager2.widget.ViewPager2
import com.perfectkey.keyboard.keyboard.internal.KeyboardParams
import com.perfectkey.keyboard.latin.R
import com.perfectkey.keyboard.latin.settings.Settings
import com.perfectkey.keyboard.latin.utils.ResourceUtils
import com.perfectkey.keyboard.latin.utils.ToolbarMode

internal class EmojiLayoutParams(res: Resources) {
    private val emojiListBottomMargin: Int
    val emojiKeyboardHeight: Int
    private val emojiCategoryPageIdViewHeight: Int
    val bottomRowKeyboardHeight: Int
    private val bottomPadding: Int
    private val bottomRowSideKeyFraction = 0.17f // width of the ABC and delete keys in emoji_bottom_row.json

    init {
        val sv = Settings.getValues()
        val defaultKeyboardHeight = ResourceUtils.getSecondaryKeyboardHeight(res, sv)

        val keyVerticalGap = (res.getFraction(R.fraction.config_key_vertical_gap_holo,
            defaultKeyboardHeight, defaultKeyboardHeight) * sv.mKeyGapScale).toInt()
        bottomPadding = (res.getFraction(R.fraction.config_keyboard_bottom_padding_holo,
            defaultKeyboardHeight, defaultKeyboardHeight) * sv.mBottomPaddingScale).toInt()
        val topPadding = res.getFraction(R.fraction.config_keyboard_top_padding_holo,
            defaultKeyboardHeight, defaultKeyboardHeight).toInt()

        val rowCount = KeyboardParams.DEFAULT_KEYBOARD_ROWS + if (sv.mShowsNumberRow) 1 else 0
        bottomRowKeyboardHeight = (defaultKeyboardHeight - bottomPadding - topPadding) / rowCount - keyVerticalGap / 2

        val pageIdHeight = res.getDimension(R.dimen.config_emoji_category_page_id_height)
        emojiCategoryPageIdViewHeight = pageIdHeight.toInt()
        val offset = 1.25f * res.displayMetrics.density * sv.mKeyboardHeightScale // like ClipboardLayoutParams
        // same 24dp of air above the first row as the letter pages have, so the keyboard keeps its height when switching
        val topAir = if (sv.mToolbarMode == ToolbarMode.HIDDEN) res.getDimensionPixelSize(R.dimen.keyboard_top_air) else 0
        val emojiListHeight = defaultKeyboardHeight - bottomRowKeyboardHeight - bottomPadding + (offset.toInt()) + topAir
        emojiListBottomMargin = 0
        // the search field above the grid takes 36dp + 8dp top + 4dp bottom margin
        val searchBarHeight = (SEARCH_BAR_TOTAL_DP * res.displayMetrics.density).toInt()
        emojiKeyboardHeight = emojiListHeight - emojiCategoryPageIdViewHeight - emojiListBottomMargin - searchBarHeight
    }

    fun setEmojiListProperties(vp: ViewPager2) {
        val lp = vp.layoutParams as LinearLayout.LayoutParams
        lp.height = emojiKeyboardHeight
        lp.bottomMargin = emojiListBottomMargin
        vp.layoutParams = lp
    }

    /** Fits the category icons exactly between the ABC and delete keys, centered on the keys' row (not on the padding below). */
    fun setTabStripProperties(strip: View, keyboardWidth: Int) {
        val lp = strip.layoutParams as ViewGroup.MarginLayoutParams
        val sideKey = (keyboardWidth * bottomRowSideKeyFraction).toInt()
        lp.marginStart = sideKey
        lp.marginEnd = sideKey
        strip.layoutParams = lp
        (strip as? LinearLayout)?.gravity = android.view.Gravity.CENTER_VERTICAL
        strip.setPadding(0, 0, 0, bottomPadding + (5 * strip.resources.displayMetrics.density).toInt()) // +5dp: optical match with the ABC and delete glyphs
    }

    fun setCategoryPageIdViewProperties(v: View) {
        val lp = v.layoutParams as LinearLayout.LayoutParams
        lp.height = emojiCategoryPageIdViewHeight
        v.layoutParams = lp
    }

    private companion object {
        const val SEARCH_BAR_TOTAL_DP = 48
    }
}
