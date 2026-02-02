package org.alphaxiv.app.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.alphaxiv.app.ui.screens.bookmarks.BookmarksScreen
import org.alphaxiv.app.ui.screens.details.DetailScreen
import org.alphaxiv.app.ui.screens.feed.FeedScreen
import org.alphaxiv.app.ui.screens.search.SearchScreen

sealed class Screen(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    data object Feed : Screen("feed", "Explore", Icons.Default.Explore)
    data object Search : Screen("search", "Search", Icons.Default.Search)
    data object Bookmarks : Screen("bookmarks", "Bookmarks", Icons.Default.Bookmark)
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute in listOf(Screen.Feed.route, Screen.Search.route, Screen.Bookmarks.route)) {
                NavigationBar {
                    val items = listOf(Screen.Feed, Screen.Search, Screen.Bookmarks)
                    items.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.label) },
                            label = { Text(screen.label) },
                            selected = currentRoute == screen.route,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Feed.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Feed.route) {
                FeedScreen(
                    viewModel = hiltViewModel(),
                    onPaperClick = { id -> navController.navigate("details/$id") }
                )
            }
            composable(Screen.Search.route) {
                SearchScreen(
                    viewModel = hiltViewModel(),
                    onPaperClick = { id -> navController.navigate("details/$id") }
                )
            }
            composable(Screen.Bookmarks.route) {
                BookmarksScreen(
                    viewModel = hiltViewModel(),
                    onPaperClick = { id -> navController.navigate("details/$id") }
                )
            }
            composable(
                route = "details/{paperId}",
                arguments = listOf(navArgument("paperId") { type = NavType.StringType })
            ) { backStackEntry ->
                val paperId = backStackEntry.arguments?.getString("paperId") ?: ""
                DetailScreen(
                    id = paperId,
                    viewModel = hiltViewModel(),
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
