package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.screens.categories.CategoriesScreen
import com.example.ui.screens.categories.CategoryDetailScreen
import com.example.ui.screens.duas.DuasScreen
import com.example.ui.screens.favorites.FavoritesScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.reader.DhikrReaderScreen
import com.example.ui.screens.ruqyah.RuqyahScreen
import com.example.ui.screens.search.SearchScreen
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.screens.splash.SplashScreen
import com.example.ui.screens.stats.StatisticsScreen
import com.example.ui.screens.tasbeeh.TasbeehScreen
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.HisnViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val hisnViewModel: HisnViewModel = viewModel()
            val userSettings by hisnViewModel.userSettings.collectAsState()

            val isDarkTheme = when (userSettings.isDarkMode) {
                true -> true
                false -> false
                null -> isSystemInDarkTheme()
            }

            MyApplicationTheme(
                darkTheme = isDarkTheme,
                palette = userSettings.themePalette
            ) {
                // Arabic RTL Layout Provider
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    HisnApp(viewModel = hisnViewModel)
                }
            }
        }
    }
}

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val testTag: String
) {
    object Home : BottomNavItem("home", "الرئيسية", Icons.Default.Home, "nav_home")
    object Categories : BottomNavItem("categories", "الأذكار", Icons.Default.MenuBook, "nav_categories")
    object Favorites : BottomNavItem("favorites", "المفضلة", Icons.Default.Bookmark, "nav_favorites")
    object Tasbeeh : BottomNavItem("tasbeeh", "المسبحة", Icons.Default.Timer, "nav_tasbeeh")
    object Settings : BottomNavItem("settings", "الإعدادات", Icons.Default.Settings, "nav_settings")
}

@Composable
fun HisnApp(viewModel: HisnViewModel) {
    val navController = rememberNavController()
    val isSplashDismissed by viewModel.isSplashDismissed.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Categories,
        BottomNavItem.Favorites,
        BottomNavItem.Tasbeeh,
        BottomNavItem.Settings
    )

    val showBottomBar = currentRoute in listOf(
        BottomNavItem.Home.route,
        BottomNavItem.Categories.route,
        BottomNavItem.Favorites.route,
        BottomNavItem.Tasbeeh.route,
        BottomNavItem.Settings.route
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                if (showBottomBar && isSplashDismissed) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 8.dp,
                        modifier = Modifier.testTag("bottom_nav_bar")
                    ) {
                        bottomNavItems.forEach { item ->
                            val selected = currentRoute == item.route
                            NavigationBarItem(
                                selected = selected,
                                onClick = {
                                    if (currentRoute != item.route) {
                                        navController.navigate(item.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.title
                                    )
                                },
                                label = {
                                    Text(
                                        text = item.title,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                modifier = Modifier.testTag(item.testTag)
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = BottomNavItem.Home.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(BottomNavItem.Home.route) {
                    HomeScreen(
                        viewModel = viewModel,
                        onNavigateToCategory = { catId ->
                            navController.navigate("category_detail/$catId")
                        },
                        onNavigateToReader = {
                            navController.navigate("reader")
                        },
                        onNavigateToSearch = {
                            navController.navigate("search")
                        },
                        onNavigateToTasbeeh = {
                            navController.navigate(BottomNavItem.Tasbeeh.route)
                        },
                        onNavigateToDuas = {
                            navController.navigate("duas")
                        },
                        onNavigateToCategories = {
                            navController.navigate(BottomNavItem.Categories.route)
                        },
                        onNavigateToStats = {
                            navController.navigate("statistics")
                        }
                    )
                }

                composable(BottomNavItem.Categories.route) {
                    CategoriesScreen(
                        viewModel = viewModel,
                        onCategoryClick = { catId ->
                            navController.navigate("category_detail/$catId")
                        }
                    )
                }

                composable(
                    route = "category_detail/{categoryId}",
                    arguments = listOf(navArgument("categoryId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val categoryId = backStackEntry.arguments?.getString("categoryId") ?: "morning"
                    CategoryDetailScreen(
                        categoryId = categoryId,
                        viewModel = viewModel,
                        onBackClick = { navController.popBackStack() },
                        onOpenReader = { navController.navigate("reader") }
                    )
                }

                composable("reader") {
                    DhikrReaderScreen(
                        viewModel = viewModel,
                        onBackClick = { navController.popBackStack() }
                    )
                }

                composable(BottomNavItem.Favorites.route) {
                    FavoritesScreen(
                        viewModel = viewModel,
                        onOpenReader = { navController.navigate("reader") }
                    )
                }

                composable(BottomNavItem.Tasbeeh.route) {
                    TasbeehScreen(viewModel = viewModel)
                }

                composable("search") {
                    SearchScreen(
                        viewModel = viewModel,
                        onBackClick = { navController.popBackStack() },
                        onOpenReader = { navController.navigate("reader") }
                    )
                }

                composable("ruqyah") {
                    RuqyahScreen(
                        viewModel = viewModel,
                        onBackClick = { navController.popBackStack() },
                        onOpenReader = { navController.navigate("reader") }
                    )
                }

                composable("duas") {
                    DuasScreen(
                        viewModel = viewModel,
                        onBackClick = { navController.popBackStack() },
                        onOpenReader = { navController.navigate("reader") }
                    )
                }

                composable(BottomNavItem.Settings.route) {
                    SettingsScreen(
                        viewModel = viewModel,
                        onNavigateToStats = { navController.navigate("statistics") }
                    )
                }

                composable("statistics") {
                    StatisticsScreen(
                        viewModel = viewModel,
                        onBackClick = { navController.popBackStack() }
                    )
                }
            }
        }

        // Animated Splash Overlay
        AnimatedVisibility(
            visible = !isSplashDismissed,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            SplashScreen(
                onDismiss = { viewModel.dismissSplash() }
            )
        }
    }
}
