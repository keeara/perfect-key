/*
 * Copyright (C) 2012 The Android Open Source Project
 * modified
 * SPDX-License-Identifier: Apache-2.0 AND GPL-3.0-only
 */

package com.perfectkey.keyboard.latin.define

object ProductionFlags {
    // supporting hardware keyboard still has a bunch of issues
    // crash (possibly fixed with b7cb95fc9da213c99d82e8833fb5f950f39d232e)
    // different crash
    //  LatinIME.isInputViewShown() returns true when there is no input view, thus crashing in onUpdateSelection
    // physical layout ignored
    // physical layout ignored for uppercase letters only (?)
    const val IS_HARDWARE_KEYBOARD_SUPPORTED = false

    /**
     * Include all suggestions from all dictionaries in
     * [com.perfectkey.keyboard.latin.SuggestedWords.mRawSuggestions].
     */
    const val INCLUDE_RAW_SUGGESTIONS = false
}
