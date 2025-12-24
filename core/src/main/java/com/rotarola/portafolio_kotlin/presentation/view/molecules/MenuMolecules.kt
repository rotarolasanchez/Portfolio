package com.rotarola.portafolio_kotlin.presentation.view.molecules

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.rotarola.portafolio_kotlin.presentation.view.atoms.MenuIconButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuTopBar(
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier,
    tittle:String = ""
) {
    TopAppBar(
        title = { Text(tittle) },
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