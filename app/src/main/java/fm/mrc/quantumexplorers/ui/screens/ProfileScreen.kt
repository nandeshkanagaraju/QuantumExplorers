package fm.mrc.quantumexplorers.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.PaddingValues
import fm.mrc.quantumexplorers.ui.theme.MonigueFont
import fm.mrc.quantumexplorers.viewmodel.LearningProgressViewModel
import fm.mrc.quantumexplorers.model.*
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.TouchApp
import fm.mrc.quantumexplorers.accessibility.AccessibilityManager
import androidx.compose.material3.LinearProgressIndicator
import fm.mrc.quantumexplorers.accessibility.AccessibilityPriority
import android.app.Activity
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: LearningProgressViewModel,
    accessibilityManager: AccessibilityManager,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showTTSSetup by remember { mutableStateOf(false) }

    // Get the activity context
    val context = LocalContext.current
    val activity = context as Activity

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Your Profile",
                        fontFamily = MonigueFont,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF6B4EFF),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Header
            item {
                ProfileHeader(viewModel.userProfile.collectAsState().value)
            }

            // Stats Section
            item {
                StatsSection()
            }

            // Achievements Section
            item {
                AchievementsSection()
            }

            // Recent Activity Section
            item {
                RecentActivitySection()
            }

            // Learning Progress Section
            item {
                LearningProgressSection(
                    learningUnits = viewModel.learningUnits.collectAsState().value,
                    unitProgress = viewModel.unitProgress.collectAsState().value
                )
            }

            // Leaderboard Section
            item {
                LeaderboardSection()
            }

            // Add Accessibility Settings Section
            item {
                AccessibilitySettingsSection(
                    accessibilityManager = accessibilityManager,
                    onShowTTSSetup = { showTTSSetup = true }
                )
            }

            // Progress indicator with proper syntax
            item {
                LinearProgressIndicator(
                    progress = { viewModel.progress.value },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Color(0xFF6B4EFF),
                    trackColor = Color(0xFF6B4EFF).copy(alpha = 0.1f)
                )
            }
        }
    }

    // Show TTS Setup dialog when needed
    if (showTTSSetup) {
        TTSSetupScreen(
            accessibilityManager = accessibilityManager,
            onSetupComplete = { 
                showTTSSetup = false
                accessibilityManager.toggleVoiceFeedback()
            }
        )
    }
}

@Composable
private fun ProfileHeader(profile: ExplorerProfile?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF6B4EFF).copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    modifier = Modifier.size(50.dp),
                    tint = Color(0xFF6B4EFF)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = profile?.name ?: "Quantum Explorer",
                fontFamily = MonigueFont,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D2D2D)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${profile?.rank?.name?.replace("_", " ") ?: "NOVICE"}",
                fontSize = 16.sp,
                color = Color(0xFF6B4EFF),
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Calculate progress based on profile level
            val progress = profile?.let {
                val currentPoints = it.totalPoints.toFloat()
                val requiredPoints = it.level.requiredPoints.toFloat()
                (currentPoints / requiredPoints).coerceIn(0f, 1f)
            } ?: 0f

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = Color(0xFF6B4EFF),
                trackColor = Color(0xFF6B4EFF).copy(alpha = 0.1f)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Show progress text
            Text(
                text = "${profile?.totalPoints ?: 0}/${profile?.level?.requiredPoints ?: 100} XP",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun StatsSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "Your Stats",
                fontFamily = MonigueFont,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D2D2D)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    icon = Icons.Filled.Science,
                    value = "15",
                    label = "Experiments"
                )
                StatItem(
                    icon = Icons.Filled.EmojiEvents,
                    value = "8",
                    label = "Badges"
                )
                StatItem(
                    icon = Icons.Filled.Whatshot,
                    value = "5",
                    label = "Day Streak"
                )
            }
        }
    }
}

@Composable
private fun StatItem(
    icon: ImageVector,
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF6B4EFF).copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF6B4EFF)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2D2D2D)
        )
        
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}

@Composable
private fun AchievementsSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "Achievements",
                fontFamily = MonigueFont,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D2D2D)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Display recent achievements
            sampleBadges.take(3).forEach { badge ->
                BadgeItem(badge)
                if (badge != sampleBadges.take(3).last()) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun BadgeItem(badge: Badge) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF5F5F5))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = badge.icon,
            fontSize = 24.sp
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = badge.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF2D2D2D)
            )
            Text(
                text = badge.description,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
        
        Card(
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = when (badge.rarity) {
                    BadgeRarity.COMMON -> Color(0xFF4CAF50)
                    BadgeRarity.RARE -> Color(0xFF2196F3)
                    BadgeRarity.EPIC -> Color(0xFF9C27B0)
                    BadgeRarity.LEGENDARY -> Color(0xFFFFB300)
                }
            )
        ) {
            Text(
                text = badge.rarity.name,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun RecentActivitySection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "Recent Activity",
                fontFamily = MonigueFont,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D2D2D)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Display recent activities
            recentActivities.take(3).forEach { activity ->
                ActivityItem(activity)
                if (activity != recentActivities.take(3).last()) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun ActivityItem(activity: RecentActivity) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF5F5F5))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF6B4EFF).copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = when (activity.type) {
                    ActivityType.EXPERIMENT_COMPLETED -> Icons.Filled.Science
                    ActivityType.BADGE_EARNED -> Icons.Filled.EmojiEvents
                    ActivityType.LEVEL_UP -> Icons.Filled.TrendingUp
                    ActivityType.QUIZ_COMPLETED -> Icons.Filled.Assignment
                    ActivityType.COURSE_STARTED -> Icons.Filled.School
                },
                contentDescription = null,
                tint = Color(0xFF6B4EFF)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = activity.description,
                fontSize = 14.sp,
                color = Color(0xFF2D2D2D)
            )
            Text(
                text = "Earned ${activity.xpEarned} XP",
                fontSize = 12.sp,
                color = Color(0xFF6B4EFF),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun LearningProgressSection(
    learningUnits: List<LearningUnit>,
    unitProgress: Map<String, Float>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "Learning Progress",
                fontFamily = MonigueFont,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D2D2D)
            )

            Spacer(modifier = Modifier.height(16.dp))

            learningUnits.forEach { unit ->
                UnitProgressItem(
                    unit = unit,
                    progress = unitProgress[unit.id] ?: 0f
                )
                if (unit != learningUnits.last()) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun UnitProgressItem(
    unit: LearningUnit,
    progress: Float
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF5F5F5))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = unit.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF2D2D2D)
            )
            Text(
                text = "${(progress * 100).toInt()}%",
                fontSize = 14.sp,
                color = Color(0xFF6B4EFF),
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.End
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = Color(0xFF6B4EFF),
            trackColor = Color(0xFF6B4EFF).copy(alpha = 0.1f)
        )
    }
}

@Composable
private fun LeaderboardSection() {
    var selectedTimeRange by remember { mutableStateOf(TimeRange.WEEKLY) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Leaderboard",
                    fontFamily = MonigueFont,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D2D2D)
                )
                
                TimeRangeSelector(
                    selectedRange = selectedTimeRange,
                    onRangeSelected = { selectedTimeRange = it }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Top 3 Players
            TopThreePlayers()

            Spacer(modifier = Modifier.height(16.dp))

            // Other Rankings
            LeaderboardList()
        }
    }
}

enum class TimeRange(val label: String) {
    DAILY("Today"),
    WEEKLY("This Week"),
    MONTHLY("This Month"),
    ALL_TIME("All Time")
}

@Composable
private fun TimeRangeSelector(
    selectedRange: TimeRange,
    onRangeSelected: (TimeRange) -> Unit
) {
    Row(
        modifier = Modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TimeRange.values().forEach { range ->
            FilterChip(
                selected = selectedRange == range,
                onClick = { onRangeSelected(range) },
                label = { 
                    Text(
                        text = range.label,
                        fontSize = 12.sp
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF6B4EFF),
                    selectedLabelColor = Color.White
                )
            )
        }
    }
}

@Composable
private fun TopThreePlayers() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        // Second Place
        TopPlayerItem(
            rank = 2,
            name = "Alice",
            points = 2800,
            modifier = Modifier.weight(1f),
            containerColor = Color(0xFFC0C0C0)
        )
        
        // First Place
        TopPlayerItem(
            rank = 1,
            name = "Bob",
            points = 3200,
            modifier = Modifier.weight(1f),
            containerColor = Color(0xFFFFD700),
            isWinner = true
        )
        
        // Third Place
        TopPlayerItem(
            rank = 3,
            name = "Charlie",
            points = 2500,
            modifier = Modifier.weight(1f),
            containerColor = Color(0xFFCD7F32)
        )
    }
}

@Composable
private fun TopPlayerItem(
    rank: Int,
    name: String,
    points: Int,
    modifier: Modifier = Modifier,
    containerColor: Color,
    isWinner: Boolean = false
) {
    Column(
        modifier = modifier.padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(if (isWinner) 80.dp else 60.dp)
                .clip(CircleShape)
                .background(containerColor.copy(alpha = 0.2f))
                .border(2.dp, containerColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isWinner) Icons.Filled.EmojiEvents else Icons.Filled.Stars,
                contentDescription = null,
                tint = containerColor,
                modifier = Modifier.size(if (isWinner) 40.dp else 30.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = name,
            fontSize = if (isWinner) 16.sp else 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2D2D2D),
            textAlign = TextAlign.Center
        )
        
        Text(
            text = "$points XP",
            fontSize = if (isWinner) 14.sp else 12.sp,
            color = Color(0xFF6B4EFF),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun LeaderboardList() {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Generate some sample leaderboard entries
        (4..8).forEach { rank ->
            LeaderboardListItem(
                rank = rank,
                name = "Player $rank",
                points = 3000 - (rank * 100),
                isCurrentUser = rank == 6
            )
        }
    }
}

@Composable
private fun LeaderboardListItem(
    rank: Int,
    name: String,
    points: Int,
    isCurrentUser: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isCurrentUser) Color(0xFF6B4EFF).copy(alpha = 0.1f) else Color(0xFFF5F5F5))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "#$rank",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (isCurrentUser) Color(0xFF6B4EFF) else Color(0xFF2D2D2D)
            )
            
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF6B4EFF).copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = name.first().toString(),
                    color = Color(0xFF6B4EFF),
                    fontWeight = FontWeight.Bold
                )
            }
            
            Text(
                text = name,
                fontSize = 14.sp,
                fontWeight = if (isCurrentUser) FontWeight.Bold else FontWeight.Normal,
                color = if (isCurrentUser) Color(0xFF6B4EFF) else Color(0xFF2D2D2D)
            )
        }
        
        Text(
            text = "$points XP",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = if (isCurrentUser) Color(0xFF6B4EFF) else Color(0xFF2D2D2D)
        )
    }
}

// Sample data
private val sampleBadges = listOf(
    Badge(
        id = "1",
        name = "First Experiment",
        description = "Completed your first chemistry experiment",
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
    )
)

private val recentActivities = listOf(
    RecentActivity(
        id = "1",
        type = ActivityType.EXPERIMENT_COMPLETED,
        description = "Completed 'Atomic Dance Party' experiment",
        timestamp = System.currentTimeMillis(),
        xpEarned = 100
    ),
    RecentActivity(
        id = "2",
        type = ActivityType.BADGE_EARNED,
        description = "Earned 'Quick Learner' badge",
        timestamp = System.currentTimeMillis() - 3600000,
        xpEarned = 50
    )
)

@Composable
private fun AccessibilitySettingsSection(
    accessibilityManager: AccessibilityManager,
    onShowTTSSetup: () -> Unit
) {
    var showAccessibilityDialog by remember { mutableStateOf(false) }
    val isEnabled by accessibilityManager.isAccessibilityEnabled.collectAsState()
    val isVoiceFeedbackEnabled by accessibilityManager.isVoiceFeedbackEnabled.collectAsState()
    val isHighContrastEnabled by accessibilityManager.isHighContrastEnabled.collectAsState()
    val isEnhancedTouchEnabled by accessibilityManager.isEnhancedTouchEnabled.collectAsState()
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .semantics {
                contentDescription = "Accessibility Settings Card"
            },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .semantics {
                    contentDescription = "Accessibility Settings Controls"
                }
        ) {
            // Main accessibility toggle
            AccessibilityMainToggle(
                isEnabled = isEnabled,
                onToggle = {
                    accessibilityManager.toggleAccessibility()
                    if (it) showAccessibilityDialog = true
                }
            )
            
            if (isEnabled) {
                Spacer(modifier = Modifier.height(16.dp))
                
                // Voice Feedback Setting
                AccessibilityOption(
                    icon = Icons.Filled.VolumeUp,
                    title = "Voice Feedback",
                    subtitle = "Enable voice announcements for actions",
                    checked = isVoiceFeedbackEnabled,
                    onCheckedChange = { 
                        if (it) onShowTTSSetup()
                        else accessibilityManager.toggleVoiceFeedback()
                    }
                )
                
                // High Contrast Setting
                AccessibilityOption(
                    icon = Icons.Filled.Contrast,
                    title = "High Contrast",
                    subtitle = "Increase visual contrast for better visibility",
                    checked = isHighContrastEnabled,
                    onCheckedChange = { 
                        accessibilityManager.toggleHighContrast()
                    }
                )
                
                // Touch Feedback Setting
                AccessibilityOption(
                    icon = Icons.Filled.TouchApp,
                    title = "Enhanced Touch",
                    subtitle = "Larger touch targets and haptic feedback",
                    checked = isEnhancedTouchEnabled,
                    onCheckedChange = { 
                        accessibilityManager.toggleEnhancedTouch()
                    }
                )
            }
        }
    }
    
    // Accessibility Features Dialog
    if (showAccessibilityDialog) {
        AccessibilityFeaturesDialog(
            onDismiss = { showAccessibilityDialog = false }
        )
    }
}

@Composable
private fun AccessibilityMainToggle(
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "Main accessibility toggle. " +
                    "Current state: ${if (isEnabled) "enabled" else "disabled"}"
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Accessibility,
                contentDescription = null,
                tint = Color(0xFF6B4EFF),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Accessibility Settings",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        
        Switch(
            checked = isEnabled,
            onCheckedChange = onToggle,
            modifier = Modifier.semantics {
                contentDescription = "Toggle all accessibility features"
            }
        )
    }
}

@Composable
private fun AccessibilityFeaturesDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Accessibility Features Enabled",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column {
                Text("The following features are now active:")
                Spacer(modifier = Modifier.height(8.dp))
                BulletPoint("Voice feedback for actions")
                BulletPoint("High contrast mode")
                BulletPoint("Larger touch targets")
                BulletPoint("Screen reader support")
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "You can customize these features in the Accessibility Settings section.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.semantics {
                    contentDescription = "Close accessibility features dialog"
                }
            ) {
                Text("Got it")
            }
        }
    )
}

@Composable
private fun AccessibilityOption(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .semantics {
                contentDescription = "$title option. Current state: ${if (checked) "enabled" else "disabled"}"
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF6B4EFF),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
private fun BulletPoint(text: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text("• ", style = MaterialTheme.typography.bodyMedium)
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
} 