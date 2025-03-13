import fm.mrc.quantumexplorers.ui.screens.components.FeaturedGameCard
import fm.mrc.quantumexplorers.ui.screens.components.GameChallengesSection
import fm.mrc.quantumexplorers.ui.screens.components.GameCard
import fm.mrc.quantumexplorers.model.GameInfo
import fm.mrc.quantumexplorers.model.GameCategory

@Composable
private fun FeaturedGameCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Featured Game",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Coming Soon!",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun GameChallengesSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Daily Challenges",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Check back later for daily challenges!",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun GameCard(game: GameInfo) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Handle game click */ },
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = game.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = game.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Play ${game.title}",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

private val games = listOf(
    GameInfo(
        title = "Quantum Puzzle",
        description = "Solve quantum mechanics puzzles",
        difficulty = 3,
        category = GameCategory.SCIENCE,
        points = 100,
        icon = "🔬",
        unlocked = true
    ),
    GameInfo(
        title = "Math Challenge",
        description = "Test your math skills",
        difficulty = 2,
        category = GameCategory.MATH,
        points = 75,
        icon = "🔢",
        unlocked = true
    ),
    GameInfo(
        title = "Code Quest",
        description = "Learn programming concepts",
        difficulty = 4,
        category = GameCategory.CODING,
        points = 150,
        icon = "💻",
        unlocked = false
    )
    // Add more games as needed
) 