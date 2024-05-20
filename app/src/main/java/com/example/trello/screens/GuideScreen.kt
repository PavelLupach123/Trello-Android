package com.example.trello.screens

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.trello.datastore.SettingsStore
import com.example.trello.ui.theme.TrelloTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuideScreen(
    navHostController: NavHostController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val settingStore = SettingsStore(context)
    val savedThemeKey = settingStore.getThemeModeKey.collectAsState(initial = "")
    val height = 12.dp
    TrelloTheme(
        darkTheme = when (savedThemeKey.value) {
            "0" -> {
                isSystemInDarkTheme()
            }
            "1" -> {false}
            else -> {true}
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = "Про додаток") },
                    navigationIcon = {
                        IconButton(onClick = {
                            navHostController.navigate(route = Screen.MyApp.route){
                                popUpTo(route = Screen.MyApp.route){
                                    inclusive = true
                                }
                            }
                        }) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        ) {paddingValues ->
            Column(
                modifier = modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Ласкаво просимо до Trello.",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Justify
                )
            }
        }
    }
}
