package org.alphaxiv.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.alphaxiv.app.ui.screens.blog.BlogScreen
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
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            NavHost(
                navController = navController,
                startDestination = Screen.Feed.route,
                modifier = Modifier.padding(bottom = if (currentRoute in listOf(Screen.Feed.route, Screen.Search.route, Screen.Bookmarks.route)) 0.dp else innerPadding.calculateBottomPadding())
            ) {
                composable(Screen.Feed.route) {
                    FeedScreen(
                        viewModel = hiltViewModel(),
                        onPaperClick = { id -> navController.navigate("details/$id") },
                        onSearchClick = { navController.navigate(Screen.Search.route) }
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
                        onBack = { navController.popBackStack() },
                        onViewBlog = { navController.navigate("blog/$paperId") }
                    )
                }
                composable(
                    route = "blog/{paperId}",
                    arguments = listOf(navArgument("paperId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val paperId = backStackEntry.arguments?.getString("paperId") ?: ""
                    BlogScreen(
                        id = paperId,
                        viewModel = hiltViewModel(),
                        onBack = { navController.popBackStack() }
                    )
                }
            }

            // Expressive Floating Bottom Bar
            if (currentRoute in listOf(Screen.Feed.route, Screen.Search.route, Screen.Bookmarks.route)) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 24.dp, vertical = 24.dp)
                        .widthIn(max = 400.dp)
                        .fillMaxWidth()
                        .height(64.dp),
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.95f),
                    tonalElevation = 6.dp,
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val items = listOf(Screen.Feed, Screen.Search, Screen.Bookmarks)
                        items.forEach { screen ->
                            val isSelected = currentRoute == screen.route
                            IconButton(
                                onClick = {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    imageVector = screen.icon,
                                    contentDescription = screen.label,
                                    tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(if (isSelected) 28.dp else 24.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}