package com.rotarola.portafolio_kotlin.presentation.view.moleculs

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.rotarola.portafolio_kotlin.R
import com.rotarola.portafolio_kotlin.presentation.view.atoms.MenuIconButton

@Composable
fun MenuDrawerItem(
    icon: Painter,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationDrawerItem(
        label = { Text(text = label) },
        selected = selected,
        onClick = onClick,
        icon = {
            Image(
                painter = icon,
                contentDescription = label,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
            )
        },
        modifier = modifier.padding(vertical = 8.dp),
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            unselectedContainerColor = MaterialTheme.colorScheme.background
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuTopBar(
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text("Menu Page") },
        navigationIcon = {
            MenuIconButton(
                //icon = painterResource(id = R.drawable.baseline_home_24),
                contentDescription = "Menu",
                onClick = onMenuClick
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.background
        ),
        modifier = modifier
    )
}