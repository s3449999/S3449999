package uk.ac.tees.mad.bookish.ui.details

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import uk.ac.tees.mad.bookish.ui.BookDetailsViewModel

@Composable
fun BookDetailsScreen(
    navController: NavHostController,
    bookId: String,
    viewModel: BookDetailsViewModel = viewModel()
) {
    LaunchedEffect(Unit) { viewModel.loadBookDetails(bookId) }

    Text(bookId)

}