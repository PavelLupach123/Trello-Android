package com.example.trello.screens

sealed class Screen(val route:String){
    object MyApp: Screen(route = "myapp_screen")
    object PermissionScreen: Screen("permission_screen")
    object SettingsScreen : Screen("settings_screen")
    object SearchScreen: Screen("search_screen")
    object DeletedTodosScreen:Screen("deleted_todo_screen")

    object GuideScreen:Screen("guide_screen")
}
