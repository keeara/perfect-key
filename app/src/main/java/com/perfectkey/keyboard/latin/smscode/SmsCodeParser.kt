// SPDX-License-Identifier: GPL-3.0-only

package com.perfectkey.keyboard.latin.smscode

/** Finds a verification code in the text of an SMS. Returns null when the message does not look like one. */
object SmsCodeParser {
    private val keywords = Regex(
        "code|otp|one[- ]time|verif|passcode|password|pin\\b|security|confirm|authenticat|" +
            "codice|verifica|monouso|sicurezza|conferma|autenticazione|" +
            "c[oó]digo|verificaci[oó]n|clave|seguridad|contrase[nñ]a",
        RegexOption.IGNORE_CASE
    )
    private val digits = Regex("(?<![\\d+.,€$£%])(?<!\\d-)(\\d{4,8})(?![\\d€$£%])|(?<!\\d)(\\d{3})[ -](\\d{3})(?!\\d)")
    private val alphanumeric = Regex(
        "(?:code|codice|c[oó]digo|otp|pin)\\s*(?:is|[eè]|es|:|-)?\\s*:?\\s*([A-Z0-9]{5,8})\\b",
        RegexOption.IGNORE_CASE
    )

    fun parse(text: String): String? {
        if (!keywords.containsMatchIn(text)) return null
        for (m in digits.findAll(text)) {
            val code = m.groupValues[1].ifEmpty { m.groupValues[2] + m.groupValues[3] }
            if (code.length == 4 && isPartOfDate(text, m.range)) continue
            return code
        }
        val alnum = alphanumeric.find(text)?.groupValues?.get(1)
        if (alnum != null && alnum.any { it.isDigit() } && alnum.any { it.isLetter() }) return alnum.uppercase()
        return null
    }

    // a 4-digit number directly next to a date separator is more likely a year than a code
    private fun isPartOfDate(text: String, range: IntRange): Boolean {
        val before = text.getOrNull(range.first - 1)
        val after = text.getOrNull(range.last + 1)
        return before == '/' || after == '/'
    }
}
