// SPDX-License-Identifier: GPL-3.0-only
package com.perfectkey.keyboard.settings.screens

import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.edit
import com.perfectkey.keyboard.keyboard.KeyboardSwitcher
import com.perfectkey.keyboard.keyboard.KeyboardTheme
import com.perfectkey.keyboard.latin.R
import com.perfectkey.keyboard.latin.settings.Defaults
import com.perfectkey.keyboard.latin.settings.Settings
import com.perfectkey.keyboard.latin.utils.prefs
import com.perfectkey.keyboard.settings.GroupedPage
import com.perfectkey.keyboard.settings.SearchSettingsScreen
import com.perfectkey.keyboard.settings.SettingsGroup
import com.perfectkey.keyboard.settings.SettingsRowData

/** The keyboard follows the phone's light or dark mode, or stays light or dark. */
enum class AppearanceMode(val titleId: Int) {
    System(R.string.appearance_system), Light(R.string.appearance_light), Dark(R.string.appearance_dark);

    fun apply(prefs: SharedPreferences) {
        prefs.edit {
            putBoolean(Settings.PREF_THEME_DAY_NIGHT, this@AppearanceMode == System)
            putString(Settings.PREF_THEME_COLORS, if (this@AppearanceMode == Dark) KeyboardTheme.THEME_DARK else KeyboardTheme.THEME_LIGHT)
            putString(Settings.PREF_THEME_COLORS_NIGHT, KeyboardTheme.THEME_DARK)
        }
        KeyboardSwitcher.getInstance().setThemeNeedsReload()
    }

    companion object {
        fun current(prefs: SharedPreferences): AppearanceMode = when {
            prefs.getBoolean(Settings.PREF_THEME_DAY_NIGHT, Defaults.PREF_THEME_DAY_NIGHT) -> System
            prefs.getString(Settings.PREF_THEME_COLORS, Defaults.PREF_THEME_COLORS) == KeyboardTheme.THEME_DARK -> Dark
            else -> Light
        }
    }
}

@Composable
fun AppearanceScreen(onClickBack: () -> Unit) {
    val prefs = LocalContext.current.prefs()
    var mode by remember { mutableStateOf(AppearanceMode.current(prefs)) }
    SearchSettingsScreen(
        onClickBack = onClickBack,
        title = stringResource(R.string.settings_screen_appearance),
        settings = emptyList(),
    ) {
        GroupedPage {
            SettingsGroup(
                *AppearanceMode.entries.map { entry ->
                    SettingsRowData(stringResource(entry.titleId), checked = mode == entry) {
                        mode = entry
                        entry.apply(prefs)
                    }
                }.toTypedArray(),
                footer = stringResource(R.string.appearance_footer),
            )
        }
    }
}
