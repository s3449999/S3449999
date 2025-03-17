package uk.ac.tees.mad.bookish.ui.favorite

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController

@Composable
fun FavoriteScreen(
    navController: NavHostController,
    viewModel: FavoritesViewModel = viewModel()
) {
    Text("Favorite Screen")

}