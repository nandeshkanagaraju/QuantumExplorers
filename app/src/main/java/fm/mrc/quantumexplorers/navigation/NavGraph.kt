package fm.mrc.quantumexplorers.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.Modifier
import fm.mrc.quantumexplorers.ui.screens.*
import fm.mrc.quantumexplorers.model.determineProfile
import fm.mrc.quantumexplorers.viewmodel.LearningProgressViewModel
import fm.mrc.quantumexplorers.accessibility.AccessibilityManager

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object SignIn : Screen("signIn")
    object SignUp : Screen("signUp")
    object Assessment : Screen("assessment")
    object Results : Screen("results/{profileName}") {
        fun createRoute(profileName: String) = "results/$profileName"
    }
    object Dashboard : Screen("dashboard")
    object Profile : Screen("profile")
    object LearningZone : Screen("learningZone")
    object ChemistryCourse : Screen("chemistry-course")
    object ChemistryLab : Screen("chemistry-lab")
    object CircuitBreaker : Screen("circuit-breaker")
}

@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: LearningProgressViewModel,
    accessibilityManager: AccessibilityManager
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Welcome.route
    ) {
        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onBeginJourney = { navController.navigate(Screen.SignIn.route) },
                onSignUpClick = { navController.navigate(Screen.SignUp.route) }
            )
        }

        composable(Screen.SignIn.route) {
            SignInScreen(
                onSignIn = { _, _ ->
                    navController.navigate(Screen.Assessment.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                },
                onNavigateToSignUp = { navController.navigate(Screen.SignUp.route) },
                onBackPressed = { navController.popBackStack() }
            )
        }

        composable(Screen.SignUp.route) {
            SignUpScreen(
                onSignUp = { _, _, _ ->
                    navController.navigate(Screen.Assessment.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                },
                onNavigateToSignIn = { navController.navigate(Screen.SignIn.route) },
                onBackPressed = { navController.popBackStack() }
            )
        }

        composable(Screen.Assessment.route) {
            AssessmentScreen(
                onComplete = { answers -> 
                    val profileName = determineProfile(answers)
                    navController.navigate(Screen.Results.createRoute(profileName)) {
                        popUpTo(Screen.Assessment.route) { inclusive = true }
                    }
                },
                onBackPressed = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Results.route,
            arguments = listOf(
                navArgument("profileName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val profileName = backStackEntry.arguments?.getString("profileName") ?: "Undefined"
            ResultsScreen(
                profileName = profileName,
                onBackToHome = {
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToDashboard = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Results.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                },
                onNavigateToChemistry = {
                    navController.navigate(Screen.ChemistryLab.route)
                },
                onNavigateToCircuitBreaker = {
                    navController.navigate(Screen.CircuitBreaker.route)
                }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                viewModel = viewModel,
                accessibilityManager = accessibilityManager,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.LearningZone.route) {
            val learningViewModel = hiltViewModel<LearningProgressViewModel>()
            LearningZoneScreen(
                viewModel = learningViewModel,
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(Screen.ChemistryCourse.route) {
            val chemistryViewModel = hiltViewModel<LearningProgressViewModel>()
            LearningZoneScreen(
                viewModel = chemistryViewModel,
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(Screen.ChemistryLab.route) {
            ChemistryLabScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(Screen.CircuitBreaker.route) {
            CircuitBreakerGameScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }
    }
} 