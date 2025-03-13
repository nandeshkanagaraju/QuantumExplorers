package fm.mrc.quantumexplorers.model

import java.util.*

data class ExplorerProfile(
    val name: String,
    val level: ExplorerLevel,
    val totalPoints: Int,
    val achievements: List<Achievement>,
    val completedUnits: List<String>, // Learning unit IDs
    val badges: List<Badge>,
    val rank: ExplorerRank = calculateRank(totalPoints)
)

enum class ExplorerRank {
    NOVICE,
    APPRENTICE,
    EXPLORER,
    MASTER,
    QUANTUM_SAGE
}

data class ExplorerLevel(
    val number: Int,
    val title: String,
    val requiredPoints: Int,
    val icon: String
)

data class Achievement(
    val title: String,
    val description: String,
    val points: Int,
    val icon: String,
    val dateEarned: Long
)

data class Badge(
    val id: String,
    val name: String,
    val description: String,
    val icon: String,
    val rarity: BadgeRarity,
    val dateEarned: Long
)

enum class BadgeRarity {
    COMMON,
    RARE,
    EPIC,
    LEGENDARY
}

data class BadgeProgress(
    val name: String,
    val current: Int,
    val required: Int,
    val icon: String
)

data class LeaderboardEntry(
    val id: String,
    val name: String,
    val level: Int,
    val xp: Int,
    val rank: Int,
    val avatarUrl: String? = null,
    val badges: List<Badge> = emptyList()
)

data class UserStats(
    val totalExperiments: Int,
    val questionsAnswered: Int,
    val experimentsCompleted: Int,
    val accuracy: Float,
    val streakDays: Int,
    val totalTimeSpent: Long
)

data class RecentActivity(
    val id: String,
    val type: ActivityType,
    val description: String,
    val timestamp: Long,
    val xpEarned: Int
)

enum class ActivityType {
    EXPERIMENT_COMPLETED,
    BADGE_EARNED,
    LEVEL_UP,
    QUIZ_COMPLETED,
    COURSE_STARTED
}

// Define explorer levels
val explorerLevels = listOf(
    ExplorerLevel(1, "Quantum Novice", 0, "🔬"),
    ExplorerLevel(2, "Particle Pioneer", 100, "⚛️"),
    ExplorerLevel(3, "Wave Walker", 250, "🌊"),
    ExplorerLevel(4, "Energy Expert", 500, "⚡"),
    ExplorerLevel(5, "Quantum Master", 1000, "🌟")
)

// Sample badges
val sampleBadges = listOf(
    Badge(
        id = "1",
        name = "Science Explorer",
        description = "Completed first science experiment",
        icon = "🧪",
        rarity = BadgeRarity.COMMON,
        dateEarned = System.currentTimeMillis()
    ),
    Badge(
        id = "2",
        name = "Quick Learner",
        description = "Completed 5 experiments in one day",
        icon = "🚀",
        rarity = BadgeRarity.RARE,
        dateEarned = System.currentTimeMillis()
    ),
    Badge(
        id = "3",
        name = "Math Wizard",
        description = "Master of quantum mathematics",
        icon = "🔢",
        rarity = BadgeRarity.EPIC,
        dateEarned = System.currentTimeMillis()
    ),
    Badge(
        id = "4",
        name = "Quantum Champion",
        description = "Achieved legendary status",
        icon = "👑",
        rarity = BadgeRarity.LEGENDARY,
        dateEarned = System.currentTimeMillis()
    )
)

// Sample achievements
val mathAchievements = listOf(
    Achievement(
        title = "Math Explorer",
        description = "Completed first quantum math lesson",
        points = 100,
        icon = "🔢",
        dateEarned = System.currentTimeMillis()
    ),
    Achievement(
        title = "Quantum Calculator",
        description = "Solved 10 quantum math problems",
        points = 200,
        icon = "⚛️",
        dateEarned = System.currentTimeMillis()
    )
)

fun calculateRank(points: Int): ExplorerRank = when {
    points < 100 -> ExplorerRank.NOVICE
    points < 250 -> ExplorerRank.APPRENTICE
    points < 500 -> ExplorerRank.EXPLORER
    points < 1000 -> ExplorerRank.MASTER
    else -> ExplorerRank.QUANTUM_SAGE
}

val defaultProfile = ExplorerProfile(
    name = "Quantum Explorer",
    level = explorerLevels[0],
    totalPoints = 0,
    achievements = emptyList(),
    completedUnits = emptyList(),
    badges = emptyList()
)

val badgeProgressList = listOf(
    BadgeProgress("Chemistry Master", 7, 10, "🧪"),
    BadgeProgress("Quiz Champion", 15, 20, "📝"),
    BadgeProgress("Streak Master", 5, 7, "🔥")
)

val recentActivities = listOf(
    RecentActivity(
        id = UUID.randomUUID().toString(),
        type = ActivityType.EXPERIMENT_COMPLETED,
        description = "Completed 'Atomic Dance Party' experiment",
        timestamp = System.currentTimeMillis(),
        xpEarned = 100
    ),
    RecentActivity(
        id = UUID.randomUUID().toString(),
        type = ActivityType.BADGE_EARNED,
        description = "Earned 'Quick Learner' badge",
        timestamp = System.currentTimeMillis() - 3600000,
        xpEarned = 50
    ),
    RecentActivity(
        id = UUID.randomUUID().toString(),
        type = ActivityType.QUIZ_COMPLETED,
        description = "Completed Quantum Basics Quiz",
        timestamp = System.currentTimeMillis() - 7200000,
        xpEarned = 75
    )
) 