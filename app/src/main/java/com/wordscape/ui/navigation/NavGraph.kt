package com.wordscape.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.wordscape.ui.screens.ar.ArScreen
import com.wordscape.ui.screens.forest.ForestScreen
import com.wordscape.ui.screens.home.HomeScreen
import com.wordscape.ui.screens.journal.JournalScreen
import com.wordscape.ui.screens.learning.LearningScreen
import com.wordscape.ui.screens.settings.SettingsScreen
import com.wordscape.ui.screens.splash.SplashScreen


@Composable
fun WordScapeNavGraph() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Check if bottom bar should be visible
    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.Journal.route,
        Screen.Forest.route,
        Screen.AR.route,
        Screen.Settings.route
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    BottomNavBar(
                        currentRoute = currentRoute,
                        onNavigate = { route ->
                            if (currentRoute != route) {
                                navController.navigate(route) {
                                    popUpTo(Screen.Home.route) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (showBottomBar) 88.dp else 0.dp) // Avoid overlap with bottom nav
        ) {
            composable(
                route = Screen.Splash.route,
                enterTransition = { fadeInTransition() },
                exitTransition = { fadeOutTransition() }
            ) {
                SplashScreen(
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(
                route = Screen.Home.route,
                enterTransition = { fadeInTransition() },
                exitTransition = { fadeOutTransition() }
            ) {
                HomeScreen(
                    onNavigateToLearning = { worldId, index ->
                        navController.navigate(Screen.Learning.createRoute(worldId, index))
                    }
                )
            }

            composable(
                route = Screen.Learning.route,
                arguments = listOf(
                    navArgument("worldId") { type = NavType.StringType },
                    navArgument("wordIndex") { type = NavType.IntType }
                ),
                enterTransition = { slideInTransition() },
                exitTransition = { slideOutTransition() }
            ) { backStackEntry ->
                val worldId = backStackEntry.arguments?.getString("worldId") ?: "animals"
                val wordIndex = backStackEntry.arguments?.getInt("wordIndex") ?: 0
                LearningScreen(
                    worldId = worldId,
                    wordIndex = wordIndex,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(
                route = Screen.Journal.route,
                enterTransition = { fadeInTransition() },
                exitTransition = { fadeOutTransition() }
            ) {
                JournalScreen()
            }

            composable(
                route = Screen.Forest.route,
                enterTransition = { fadeInTransition() },
                exitTransition = { fadeOutTransition() }
            ) {
                ForestScreen()
            }

            composable(
                route = Screen.AR.route,
                enterTransition = { fadeInTransition() },
                exitTransition = { fadeOutTransition() }
            ) {
                ArScreen()
            }

            composable(
                route = Screen.Settings.route,
                enterTransition = { fadeInTransition() },
                exitTransition = { fadeOutTransition() }
            ) {
                SettingsScreen()
            }
        }
    }
}

private fun <T> AnimatedContentTransitionScope<T>.fadeInTransition() =
    fadeIn(animationSpec = tween(500))

private fun <T> AnimatedContentTransitionScope<T>.fadeOutTransition() =
    fadeOut(animationSpec = tween(500))

private fun <T> AnimatedContentTransitionScope<T>.slideInTransition() =
    slideIntoContainer(
        AnimatedContentTransitionScope.SlideDirection.Left,
        animationSpec = tween(500)
    )

private fun <T> AnimatedContentTransitionScope<T>.slideOutTransition() =
    slideOutOfContainer(
        AnimatedContentTransitionScope.SlideDirection.Right,
        animationSpec = tween(500)
    )
