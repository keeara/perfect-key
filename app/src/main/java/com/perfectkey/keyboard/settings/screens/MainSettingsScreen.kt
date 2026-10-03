// SPDX-License-Identifier: GPL-3.0-only
package com.perfectkey.keyboard.settings.screens

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.perfectkey.keyboard.latin.R
import com.perfectkey.keyboard.latin.utils.JniUtils
import com.perfectkey.keyboard.latin.utils.locale
import com.perfectkey.keyboard.latin.utils.SubtypeSettings
import com.perfectkey.keyboard.latin.utils.Theme
import com.perfectkey.keyboard.latin.utils.prefs
import com.perfectkey.keyboard.latin.utils.previewDark
import com.perfectkey.keyboard.settings.GroupedPage
import com.perfectkey.keyboard.settings.SearchSettingsScreen
import com.perfectkey.keyboard.settings.SettingsGroup
import com.perfectkey.keyboard.settings.SettingsRowData
import com.perfectkey.keyboard.settings.initPreview

@Composable
fun MainSettingsScreen(
    onClickAbout: () -> Unit,
    onClickTextCorrection: () -> Unit,
    onClickPreferences: () -> Unit,
    onClickGestureTyping: () -> Unit,
    onClickAdvanced: () -> Unit,
    onClickAppearance: () -> Unit,
    onClickLanguage: () -> Unit,
    onClickLayouts: () -> Unit,
    onClickDictionaries: () -> Unit,
    onClickBack: () -> Unit,
) {
    SearchSettingsScreen(
        onClickBack = onClickBack,
        title = stringResource(R.string.settings_title),
        settings = emptyList(),
    ) {
        val enabledLanguages = SubtypeSettings.getEnabledSubtypes(true).map { it.locale().language.uppercase() }.distinct().joinToString(", ")
        val appearance = stringResource(AppearanceMode.current(LocalContext.current.prefs()).titleId)
        GroupedPage {
            SettingsGroup(
                SettingsRowData(stringResource(R.string.language_and_layouts_title), enabledLanguages, onClick = onClickLanguage),
                SettingsRowData(stringResource(R.string.settings_screen_appearance), appearance, onClick = onClickAppearance),
            )
            SettingsGroup(
                SettingsRowData(stringResource(R.string.settings_screen_correction), onClick = onClickTextCorrection),
                SettingsRowData(stringResource(R.string.settings_screen_preferences), onClick = onClickPreferences),
                *(if (JniUtils.sHaveGestureLib)
                    arrayOf(SettingsRowData(stringResource(R.string.settings_screen_gesture), onClick = onClickGestureTyping))
                else emptyArray()),
            )
            SettingsGroup(
                SettingsRowData(stringResource(R.string.settings_screen_secondary_layouts), onClick = onClickLayouts),
                SettingsRowData(stringResource(R.string.dictionary_settings_category), onClick = onClickDictionaries),
            )
            SettingsGroup(
                SettingsRowData(stringResource(R.string.settings_screen_advanced), onClick = onClickAdvanced),
                SettingsRowData(stringResource(R.string.settings_screen_about), onClick = onClickAbout),
            )
        }
    }
}

@Preview
@Composable
private fun PreviewScreen() {
    initPreview(LocalContext.current)
    Theme(previewDark) {
        Surface {
            MainSettingsScreen({}, {}, {}, {}, {}, {}, {}, {}, {}, {})
        }
    }
}
