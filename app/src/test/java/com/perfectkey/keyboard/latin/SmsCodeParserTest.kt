// SPDX-License-Identifier: GPL-3.0-only

package com.perfectkey.keyboard.latin

import com.perfectkey.keyboard.latin.smscode.SmsCodeParser
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SmsCodeParserTest {
    @Test fun english() = assertEquals("482913", SmsCodeParser.parse("Your verification code is 482913. Do not share it."))
    @Test fun google() = assertEquals("123456", SmsCodeParser.parse("G-123456 is your Google verification code."))
    @Test fun italian() = assertEquals("7731", SmsCodeParser.parse("Il tuo codice di verifica è 7731"))
    @Test fun spanish() = assertEquals("908172", SmsCodeParser.parse("Tu código de verificación es 908172"))
    @Test fun spaced() = assertEquals("123456", SmsCodeParser.parse("Your code: 123 456"))
    @Test fun alphanumeric() = assertEquals("AB12CD", SmsCodeParser.parse("Your code is AB12CD"))
    @Test fun noKeyword() = assertNull(SmsCodeParser.parse("See you at 1830, bring 5000 steps"))
    @Test fun amountIsNotCode() = assertNull(SmsCodeParser.parse("Payment confirmed: €1250 charged"))
    @Test fun phoneIsNotCode() = assertNull(SmsCodeParser.parse("Call +391234567890 to confirm"))
}
