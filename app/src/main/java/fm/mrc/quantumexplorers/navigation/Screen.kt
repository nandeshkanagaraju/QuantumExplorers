sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object Profile : Screen("profile")
    object ChemistryLab : Screen("chemistry_lab")
    object CircuitBreaker : Screen("circuit_breaker")
    object Rollercoaster : Screen("rollercoaster")
    // ... other screens ...
} 