// SPDX-License-Identifier: GPL-3.0-only
package com.perfectkey.keyboard.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.perfectkey.keyboard.latin.utils.NextScreenIcon

/** One line of a [SettingsGroup]: title on the left, current value and chevron (or a check mark) on the right. */
class SettingsRowData(
    val title: String,
    val value: String? = null,
    val checked: Boolean? = null, // null: opens another screen, otherwise a selection row
    val onClick: () -> Unit,
)

/** Plain scrolling page with a soft background; groups sit on it as rounded cards. */
@Composable
fun GroupedPage(content: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        content = content,
    )
}

@Composable
fun SettingsGroup(vararg rows: SettingsRowData, footer: String? = null) {
    Column {
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.surfaceContainerLowest,
        ) {
            Column {
                rows.forEachIndexed { index, row ->
                    SettingsRow(row)
                    if (index < rows.lastIndex)
                        HorizontalDivider(Modifier.padding(start = 16.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                }
            }
        }
        if (footer != null)
            Text(
                footer,
                Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
            )
    }
}

@Composable
private fun SettingsRow(row: SettingsRowData) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = row.onClick)
            .heightIn(min = 52.dp)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(row.title, Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
        if (row.value != null)
            Text(
                row.value,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.widthIn(max = 160.dp),
            )
        when (row.checked) {
            null -> NextScreenIcon()
            true -> Text("✓", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleMedium)
            false -> {}
        }
    }
}
