// SPDX-License-Identifier: GPL-3.0-only

package com.perfectkey.keyboard.latin.smscode

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import com.perfectkey.keyboard.latin.settings.Defaults
import com.perfectkey.keyboard.latin.settings.Settings
import com.perfectkey.keyboard.latin.utils.prefs

/**
 * Receives incoming SMS (only while the user has enabled the feature) and keeps the verification code in memory.
 * The code is not put on the clipboard: Android shows a "copied" overlay for every clipboard write.
 */
class SmsCodeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return
        if (!context.prefs().getBoolean(Settings.PREF_SUGGEST_SMS_CODES, Defaults.PREF_SUGGEST_SMS_CODES)) return
        val body = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            ?.joinToString("") { it.messageBody.orEmpty() } ?: return
        val code = SmsCodeParser.parse(body) ?: return
        SmsCodeStore.put(code)
    }
}
