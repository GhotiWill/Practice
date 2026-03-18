package com.example.bardakovexam.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.bardakovexam.presentation.screens.CatalogScreen
import com.example.bardakovexam.presentation.screens.CreateNewPasswordScreen
import com.example.bardakovexam.presentation.screens.FavoriteScreen
import com.example.bardakovexam.presentation.screens.ForgotPasswordScreen
import com.example.bardakovexam.presentation.screens.HomeScreen
import com.example.bardakovexam.presentation.screens.LoyaltyCardScreen
import com.example.bardakovexam.presentation.screens.ProfileScreen
import com.example.bardakovexam.presentation.screens.RegisterAccountScreen
import com.example.bardakovexam.presentation.screens.SignInScreen
import com.example.bardakovexam.presentation.screens.VerificationScreen

@Composable
fun navHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = navRoutes.signIn) {
        composable(navRoutes.register) { RegisterAccountScreen(navController) }
        composable(navRoutes.profile) { ProfileScreen(navController) }
        composable(navRoutes.catalog) { CatalogScreen(navController) }
        composable(navRoutes.newPassword) { CreateNewPasswordScreen(navController) }
        composable(navRoutes.forgotPassword) { ForgotPasswordScreen(navController) }
        composable(navRoutes.home) { HomeScreen(navController) }
        composable(navRoutes.signIn) { SignInScreen(navController) }
        composable(navRoutes.loyalty) { LoyaltyCardScreen(navController) }
        composable(navRoutes.favorite) { FavoriteScreen(navController) }
        composable(
            navRoutes.verification,
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            VerificationScreen(navController, backStackEntry.arguments?.getString("email").orEmpty())
        }
    }
}
