// SPDX-License-Identifier: GPL-3.0-only

package com.perfectkey.keyboard.latin.smscode

import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.widget.PopupWindow
import android.widget.TextView
import com.perfectkey.keyboard.event.HapticEvent
import com.perfectkey.keyboard.keyboard.KeyboardTypeface
import com.perfectkey.keyboard.keyboard.internal.KeyPreviewBalloonDrawable
import com.perfectkey.keyboard.keyboard.internal.keyboard_parser.floris.KeyCode
import com.perfectkey.keyboard.latin.AudioAndHapticFeedbackManager
import com.perfectkey.keyboard.latin.LatinIME
import com.perfectkey.keyboard.latin.common.ColorType
import com.perfectkey.keyboard.latin.settings.Defaults
import com.perfectkey.keyboard.latin.settings.Settings
import com.perfectkey.keyboard.latin.utils.dpToPx
import com.perfectkey.keyboard.latin.utils.prefs

/** A small pill with the latest received verification code, floating above the keyboard. Tap it to type the code. */
class SmsCodePill(private val ime: LatinIME) {
    private var window: PopupWindow? = null
    private val expire = Runnable { dismiss() }

    /** Shows the pill if the feature is on and a recent code is available, otherwise hides it. */
    fun update(anchor: View?) {
        dismiss()
        if (anchor == null) return
        if (!ime.prefs().getBoolean(Settings.PREF_SUGGEST_SMS_CODES, Defaults.PREF_SUGGEST_SMS_CODES)) return
        val code = SmsCodeStore.current() ?: return
        // wait until the keyboard has a position on screen
        anchor.post { if (anchor.isAttachedToWindow && anchor.windowToken != null) show(anchor, code) }
    }

    private fun show(anchor: View, code: String) {
        dismiss()
        val res = ime.resources
        val colors = Settings.getValues().mColors
        val pill = TextView(ime).apply {
            text = code
            textSize = 15f
            setTypeface(typeface, Typeface.BOLD)
            KeyboardTypeface.applyToTextView(this)
            letterSpacing = 0.06f
            setTextColor(colors.get(ColorType.KEY_TEXT))
            gravity = Gravity.CENTER
            setPadding(14.dpToPx(res), 5.dpToPx(res), 14.dpToPx(res), 5.dpToPx(res))
            // same bubble as the long-press accent popup
            background = KeyPreviewBalloonDrawable(ime).apply { setFullyRound(true) }
            contentDescription = ime.getString(com.perfectkey.keyboard.latin.R.string.spoken_sms_code_suggestion) + " " + code
            setOnClickListener {
                SmsCodeStore.clear()
                ime.onTextInput(code)
                AudioAndHapticFeedbackManager.getInstance().performHapticAndAudioFeedback(KeyCode.NOT_SPECIFIED, it, HapticEvent.KEY_LONG_PRESS)
                dismiss()
            }
        }
        pill.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val loc = IntArray(2)
        // popup coordinates are relative to the input window, not the screen
        anchor.getLocationInWindow(loc)
        val x = loc[0] + (anchor.width - pill.measuredWidth) / 2
        val y = loc[1] - pill.measuredHeight - 6.dpToPx(res)
        window = PopupWindow(pill, pill.measuredWidth, pill.measuredHeight).apply {
            isFocusable = false
            isOutsideTouchable = false
            elevation = 8.dpToPx(res).toFloat()
            setBackgroundDrawable(null)
            showAtLocation(anchor, Gravity.TOP or Gravity.START, x, y)
        }
        pill.postDelayed(expire, SHOWN_MILLIS.coerceAtMost(SmsCodeStore.remainingMillis()).coerceAtLeast(0))
    }

    companion object {
        const val SHOWN_MILLIS = 10_000L
    }

    fun dismiss() {
        window?.contentView?.removeCallbacks(expire)
        try { window?.dismiss() } catch (_: Exception) {}
        window = null
    }
}
