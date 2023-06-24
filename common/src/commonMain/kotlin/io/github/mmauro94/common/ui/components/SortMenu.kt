package io.github.mmauro94.common.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import io.github.mmauro94.common.MR
import io.github.mmauro94.common.client.entities.SortType

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SortMenuButton(
    onSortSelected: (sort: SortType) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton({ expanded = true }) {
            Icon(Icons.Default.Sort, "sort")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            var openGroup by remember { mutableStateOf<SortType.Group?>(null) }

            AnimatedContent(
                targetState = openGroup,
                transitionSpec = {
                    if (targetState != null) {
                        slideInHorizontally { it } with slideOutHorizontally { -it }
                    } else {
                        slideInHorizontally { -it } with slideOutHorizontally { it }
                    }.using(
                        SizeTransform(clip = false),
                    )
                },
            ) { target ->
                Column(Modifier.width(MENU_WIDTH)) {
                    val sortSelected: (sort: SortType) -> Unit = {
                        onSortSelected(it)
                        expanded = false
                    }
                    if (target == null) {
                        GroupList(
                            onGroupSelected = { openGroup = it },
                            onSortSelected = sortSelected,
                        )
                    } else {
                        Group(
                            group = target,
                            onSortSelected = sortSelected,
                            onBack = { openGroup = null },
                        )
                    }
                }
            }
        }
    }
}

private val MENU_WIDTH = 200.dp

@Composable
private fun ColumnScope.GroupList(
    onSortSelected: (sort: SortType) -> Unit,
    onGroupSelected: (group: SortType.Group) -> Unit,
) {
    SortType.GROUPS.forEach { (group, sorts) ->
        DropdownMenuItem(
            modifier = Modifier.fillMaxWidth(),
            text = { Text(group.label.str()) },
            onClick = {
                if (sorts.size == 1) {
                    onSortSelected(sorts.single())
                } else {
                    onGroupSelected(group)
                }
            },
            leadingIcon = {
                Icon(group.icon, null)
            },
            trailingIcon = if (sorts.size > 1) {
                { Icon(Icons.Default.ArrowRight, null) }
            } else {
                null
            },
        )
    }
}

@Composable
private fun ColumnScope.Group(
    group: SortType.Group,
    onSortSelected: (sort: SortType) -> Unit,
    onBack: () -> Unit,
) {
    DropdownMenuItem(
        modifier = Modifier.fillMaxWidth(),
        text = { Text(stringResource(MR.strings.action_back)) },
        onClick = onBack,
        leadingIcon = { Icon(Icons.Default.ArrowBack, "back") },
    )
    Divider()
    SortType.GROUPS.getValue(group).forEach { type ->
        DropdownMenuItem(
            modifier = Modifier.fillMaxWidth(),
            text = { Text(type.label.str()) },
            onClick = { onSortSelected(type) },
        )
    }
}
