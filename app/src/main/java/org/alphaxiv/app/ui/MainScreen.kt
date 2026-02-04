package org.alphaxiv.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Explore
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

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = "feed",
            modifier = Modifier.fillMaxSize()
        ) {
            composable("feed") {
                FeedScreen(
                    viewModel = hiltViewModel(),
                    onPaperClick = { id -> navController.navigate("details/$id") },
                    onMenuClick = { /* Open Drawer or Menu */ },
                    onBookmarksClick = { navController.navigate("bookmarks") }
                )
            }
            composable("bookmarks") {
                BookmarksScreen(
                    viewModel = hiltViewModel(),
                    onPaperClick = { id -> navController.navigate("details/$id") },
                    onBack = { navController.popBackStack() }
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
    }
}
