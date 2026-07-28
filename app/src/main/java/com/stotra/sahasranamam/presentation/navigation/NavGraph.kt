package com.stotra.sahasranamam.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.stotra.sahasranamam.presentation.home.HomeScreen
import com.stotra.sahasranamam.presentation.category.CategoryScreen
import com.stotra.sahasranamam.presentation.category.CategoryViewModel
import com.stotra.sahasranamam.presentation.study.StotraStudyScreen
import com.stotra.sahasranamam.presentation.study.StotraStudyViewModel

import com.stotra.sahasranamam.presentation.remedies.RemediesScreen
import com.stotra.sahasranamam.presentation.remedies.RemedyDetailScreen
import com.stotra.sahasranamam.presentation.remedies.RemediesViewModel

object NavRoutes {
    const val HOME = "home"
    const val STUDY = "study/{stotraId}"
    const val CATEGORY = "category/{categoryName}"
    const val REMEDIES = "remedies"
    const val REMEDY_DETAIL = "remedy_detail/{categoryId}"
}

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.HOME
    ) {
        composable(NavRoutes.HOME) {
            HomeScreen(
                onCategoryClick = { categoryName -> navController.navigate("category/$categoryName") },
                onResumeClick = { stotraId -> navController.navigate("study/$stotraId") },
                onRemediesClick = { navController.navigate(NavRoutes.REMEDIES) }
            )
        }

        composable(NavRoutes.CATEGORY) { backStackEntry ->
            val categoryName = backStackEntry.arguments?.getString("categoryName") ?: ""
            val viewModel: CategoryViewModel = hiltViewModel()
            CategoryScreen(
                categoryName = categoryName,
                viewModel = viewModel,
                onStotraClick = { stotraId -> navController.navigate("study/$stotraId") },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(NavRoutes.STUDY) {
            val viewModel: StotraStudyViewModel = hiltViewModel()
            StotraStudyScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(NavRoutes.REMEDIES) {
            val viewModel: RemediesViewModel = hiltViewModel()
            RemediesScreen(
                viewModel = viewModel,
                onCategorySelected = { categoryId -> navController.navigate("remedy_detail/$categoryId") },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(NavRoutes.REMEDY_DETAIL) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""
            val viewModel: RemediesViewModel = hiltViewModel()
            RemedyDetailScreen(
                categoryId = categoryId,
                viewModel = viewModel,
                onStotraSelected = { stotraId -> navController.navigate("study/$stotraId") },
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
