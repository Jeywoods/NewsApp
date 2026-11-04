
package com.example.newsapp.presentation.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.newsapp.di.AppContainer
import com.example.newsapp.domain.model.News
import com.example.newsapp.presentation.categories.CategoriesScreen
import com.example.newsapp.presentation.categories.CategoriesViewModel
import com.example.newsapp.presentation.categories.CategoriesViewModelFactory
import com.example.newsapp.presentation.details.NewsDetailsScreen
import com.example.newsapp.presentation.details.NewsDetailsViewModel
import com.example.newsapp.presentation.details.NewsDetailsViewModelFactory
import com.example.newsapp.presentation.news.NewsScreen
import com.example.newsapp.presentation.news.NewsViewModel
import com.example.newsapp.presentation.news.NewsViewModelFactory

@Composable
fun NewsNavigation(
    appContainer: AppContainer
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "categories"
    ) {
        composable("categories") {
            val factory = remember {
                CategoriesViewModelFactory(
                    appContainer.getCategoriesUseCase
                )
            }

            val viewModel: CategoriesViewModel = viewModel(
                factory = factory
            )

            CategoriesScreen(
                viewModel = viewModel,
                onCategoryClick = { category ->
                    navController.navigate(
                        "news/${category.id}/${Uri.encode(category.name)}"
                    )
                }
            )
        }

        composable(
            route = "news/{categoryId}/{categoryName}",
            arguments = listOf(
                navArgument("categoryId") {
                    type = NavType.IntType
                },
                navArgument("categoryName") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->

            val categoryId = backStackEntry.arguments
                ?.getInt("categoryId") ?: return@composable

            val categoryName = backStackEntry.arguments
                ?.getString("categoryName") ?: return@composable

            val factory = remember(categoryId) {
                NewsViewModelFactory(
                    categoryId = categoryId,
                    getNewsUseCase = appContainer.getNewsUseCase
                )
            }

            val viewModel: NewsViewModel = viewModel(
                key = "news_$categoryId",
                factory = factory
            )

            NewsScreen(
                categoryName = categoryName,
                viewModel = viewModel,
                onNewsClick = { news ->
                    navController.navigate(
                        "newsDetails" +
                                "?newsId=${news.id}" +
                                "&title=${Uri.encode(news.title)}" +
                                "&date=${Uri.encode(news.date)}" +
                                "&shortDescription=${Uri.encode(news.shortDescription)}"
                    )
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "newsDetails?newsId={newsId}&title={title}&date={date}&shortDescription={shortDescription}",
            arguments = listOf(
                navArgument("newsId") {
                    type = NavType.IntType
                },
                navArgument("title") {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("date") {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("shortDescription") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->

            val arguments = backStackEntry.arguments
            val newsId = arguments?.getInt("newsId")
                ?: return@composable

            val initialNews = remember(newsId) {
                News(
                    id = newsId,
                    title = arguments.getString("title").orEmpty(),
                    date = arguments.getString("date").orEmpty(),
                    shortDescription = arguments.getString("shortDescription").orEmpty()
                )
            }

            val factory = remember(newsId) {
                NewsDetailsViewModelFactory(
                    initialNews = initialNews,
                    getNewsDetailsUseCase = appContainer.getNewsDetailsUseCase
                )
            }

            val viewModel: NewsDetailsViewModel = viewModel(
                key = "details_$newsId",
                factory = factory
            )

            NewsDetailsScreen(
                viewModel = viewModel,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
