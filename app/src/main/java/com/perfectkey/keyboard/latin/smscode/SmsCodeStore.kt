// SPDX-License-Identifier: GPL-3.0-only

package com.perfectkey.keyboard.latin.smscode

import android.os.Handler
import android.os.Looper

/** Keeps the latest received code in memory only (never written to disk) for a few minutes. */
object SmsCodeStore {
    const val VALID_MILLIS = 60 * 1000L

    private var code: String? = null
    private var receivedAt = 0L
    private val mainHandler = Handler(Looper.getMainLooper())

    /** Called on the main thread when a new code arrives. */
    var onNewCode: (() -> Unit)? = null

    fun put(newCode: String) {
        code = newCode
        receivedAt = System.currentTimeMillis()
        mainHandler.post { onNewCode?.invoke() }
    }

    /** The code, if it is still recent and was not used or dismissed. */
    fun current(): String? {
        val c = code ?: return null
        if (System.currentTimeMillis() - receivedAt > VALID_MILLIS) {
            code = null
            return null
        }
        return c
    }

    fun remainingMillis() = VALID_MILLIS - (System.currentTimeMillis() - receivedAt)

    fun clear() {
        code = null
    }
}
