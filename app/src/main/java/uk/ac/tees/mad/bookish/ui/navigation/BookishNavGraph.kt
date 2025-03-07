package uk.ac.tees.mad.bookish.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import uk.ac.tees.mad.bookish.Screen
import uk.ac.tees.mad.bookish.ui.SplashScreen
import uk.ac.tees.mad.bookish.ui.auth.AuthScreen
import uk.ac.tees.mad.bookish.ui.details.BookDetailsScreen
import uk.ac.tees.mad.bookish.ui.home.HomeScreen

@Composable
fun BookishNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Splash.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(navController)
        }
        composable(Screen.Auth.route) {
            AuthScreen(navController)
        }
        composable(Screen.Home.route) {
            HomeScreen(navController)
        }
        composable(
            route = Screen.BookDetails.route,
            arguments = listOf(navArgument("bookId") { type = NavType.StringType })
        ) {
            val bookId = it.arguments?.getString("bookId") ?: ""
            BookDetailsScreen(navController, bookId)
        }
        composable(Screen.Favorites.route) {
        }
        composable(Screen.Profile.route) {
        }
    }
}