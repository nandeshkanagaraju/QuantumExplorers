package fm.mrc.quantumexplorers.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fm.mrc.quantumexplorers.R
import fm.mrc.quantumexplorers.ui.theme.MonigueFont
import java.util.*
import fm.mrc.quantumexplorers.ui.screens.components.*
import fm.mrc.quantumexplorers.model.GameInfo
import fm.mrc.quantumexplorers.model.GameCategory
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring

data class Quiz(
    val question: String,
    val options: List<String>,
    val correctAnswer: Int
)

data class Course(
    val title: String,
    val topics: List<String>,
    val activities: List<String>,
    val quizzes: List<Quiz>,
    val ageRange: String
)

data class Achievement(
    val title: String,
    val description: String,
    val icon: String,
    val progress: Float, // 0f-1f
    val completed: Boolean
)

// Add new data classes for interactive content
data class ChemistryExperiment(
    val title: String,
    val description: String,
    val difficulty: Int, // 1-5
    val visualizationType: VisualizationType,
    val interactiveElements: List<InteractiveElement>,
    val safetyTips: List<String>
)

enum class VisualizationType {
    PARTICLE_COLLISION,
    MOLECULAR_FORMATION,
    STATE_CHANGE,
    ELECTRON_TRANSFER,
    BOND_FORMATION
}

data class InteractiveElement(
    val name: String,
    val description: String,
    val controlType: ControlType
)

enum class ControlType {
    SLIDER,
    BUTTON,
    DRAG_DROP,
    ROTATE_3D,
    MIXER,
    CIRCUIT_COMPONENT
}

private val chemistryExperiments = listOf(
    ChemistryExperiment(
        title = "Atomic Dance Party 🎉",
        description = "Watch atoms move, collide, and form molecules!",
        difficulty = 1,
        visualizationType = VisualizationType.PARTICLE_COLLISION,
        interactiveElements = listOf(
            InteractiveElement(
                name = "Temperature Control",
                description = "Adjust how fast atoms move",
                controlType = ControlType.SLIDER
            ),
            InteractiveElement(
                name = "Particle Selector",
                description = "Choose different types of atoms",
                controlType = ControlType.DRAG_DROP
            )
        ),
        safetyTips = listOf("Virtual experiments are always safe!", "Learn about real-lab safety")
    ),
    ChemistryExperiment(
        title = "Rainbow Reactions 🌈",
        description = "Mix chemicals and create colorful reactions!",
        difficulty = 2,
        visualizationType = VisualizationType.MOLECULAR_FORMATION,
        interactiveElements = listOf(
            InteractiveElement(
                name = "Chemical Mixer",
                description = "Combine different solutions",
                controlType = ControlType.MIXER
            ),
            InteractiveElement(
                name = "Color Observer",
                description = "Watch color changes in real-time",
                controlType = ControlType.BUTTON
            )
        ),
        safetyTips = listOf(
            "Never mix chemicals without adult supervision",
            "Always wear safety goggles in real labs"
        )
    )
)

// Circuit components for the Circuit Breaker game
enum class CircuitComponentType {
    WIRE,
    BATTERY,
    BULB,
    SWITCH,
    CIRCUIT_BREAKER
}

data class CircuitComponent(
    val type: CircuitComponentType,
    val position: Pair<Int, Int>, // Grid position
    val isConnected: Boolean = false,
    val rotation: Int = 0 // 0, 90, 180, 270 degrees
)

data class CircuitBreakerGame(
    val level: Int,
    val gridSize: Pair<Int, Int>, // width x height
    val components: List<CircuitComponent>,
    val maxCurrent: Float = 1.0f,
    val isComplete: Boolean = false,
    val isBreakerTripped: Boolean = false
)

private val circuitBreakerLevels = listOf(
    CircuitBreakerGame(
        level = 1,
        gridSize = Pair(6, 6),
        components = listOf(
            CircuitComponent(CircuitComponentType.BATTERY, Pair(0, 2)),
            CircuitComponent(CircuitComponentType.BULB, Pair(5, 2)),
            CircuitComponent(CircuitComponentType.CIRCUIT_BREAKER, Pair(2, 2))
        )
    ),
    CircuitBreakerGame(
        level = 2,
        gridSize = Pair(8, 8),
        components = listOf(
            CircuitComponent(CircuitComponentType.BATTERY, Pair(0, 3)),
            CircuitComponent(CircuitComponentType.BULB, Pair(7, 3)),
            CircuitComponent(CircuitComponentType.SWITCH, Pair(3, 3)),
            CircuitComponent(CircuitComponentType.CIRCUIT_BREAKER, Pair(5, 3))
        )
    )
)

private val scienceCourses = listOf(
    Course(
        title = "Forces & Motion",
        topics = listOf(
            "Newton's Laws",
            "Gravity",
            "Friction",
            "Speed & Acceleration"
        ),
        activities = listOf(
            "DIY balloon rocket experiment",
            "Rolling car ramp experiment"
        ),
        quizzes = listOf(
            Quiz(
                "What force pulls objects toward the Earth?",
                listOf("Magnetism", "Gravity", "Friction", "Electricity"),
                1
            ),
            Quiz(
                "What will happen if you push a ball in space?",
                listOf(
                    "It will stop after a while",
                    "It will keep moving forever",
                    "It will return to you"
                ),
                1
            )
        ),
        ageRange = "8-10 years"
    ),
    Course(
        title = "Amazing Atoms & Reactions",
        topics = listOf(
            "Atoms and Elements",
            "States of Matter",
            "Chemical Reactions"
        ),
        activities = listOf(
            "Make slime experiment",
            "Vinegar + baking soda volcano"
        ),
        quizzes = listOf(
            Quiz(
                "What is the smallest unit of matter?",
                listOf("Atom", "Molecule", "Proton"),
                0
            )
        ),
        ageRange = "11-13 years"
    ),
    Course(
        title = "Interactive Chemistry Lab ✨",
        topics = listOf(
            "🔬 Atomic Adventures",
            "⚗️ Magical Molecules",
            "🌡️ States of Matter",
            "⚡ Chemical Bonds",
            "🎨 Color Chemistry",
            "💥 Reaction Time!"
        ),
        activities = listOf(
            "Virtual Lab: Atomic Dance Party 🎉",
            "3D Molecule Builder 🏗️",
            "Rainbow Reaction Creator 🌈",
            "State Change Simulator ❄️",
            "Chemical Bond Explorer ⚡",
            "Color-Changing Potions 🧪"
        ),
        quizzes = listOf(
            Quiz(
                "What happens when atoms get excited?",
                listOf(
                    "They move faster",
                    "They turn blue",
                    "They disappear",
                    "They sleep"
                ),
                0
            ),
            Quiz(
                "Why do some reactions change color?",
                listOf(
                    "Magic spells",
                    "New compounds forming",
                    "Adding food coloring",
                    "Turning on lights"
                ),
                1
            ),
            Quiz(
                "What makes ice melt into water?",
                listOf(
                    "Adding energy/heat",
                    "Magic",
                    "Adding more ice",
                    "Saying please"
                ),
                0
            )
        ),
        ageRange = "9-12 years"
    )
)

private val technologyCourses = listOf(
    Course(
        title = "Coding with Scratch",
        topics = listOf(
            "Block coding basics",
            "Loops and conditions",
            "Animation basics",
            "Simple game creation"
        ),
        activities = listOf(
            "Create a jumping character game",
            "Build an interactive story"
        ),
        quizzes = listOf(
            Quiz(
                "What is Scratch used for?",
                listOf("Drawing pictures", "Making animations and games", "Writing essays", "Playing music"),
                1
            ),
            Quiz(
                "What is a loop in coding?",
                listOf("A repeated action", "A way to end a program", "A type of game", "A drawing tool"),
                0
            )
        ),
        ageRange = "8-12 years"
    ),
    Course(
        title = "Intro to Robotics",
        topics = listOf(
            "Basic robotics concepts",
            "Sensors and circuits",
            "Simple programming",
            "Robot movement"
        ),
        activities = listOf(
            "Build a simple robotic arm",
            "Program basic robot movements"
        ),
        quizzes = listOf(
            Quiz(
                "What powers most robots?",
                listOf("Water", "Electricity", "Wind", "Sound"),
                1
            )
        ),
        ageRange = "10-15 years"
    )
)

private val games = listOf(
    GameInfo(
        title = "Diagram Detective",
        description = "Test your observation skills! Spot differences in scientific diagrams",
        difficulty = 3,
        category = GameCategory.SCIENCE,
        points = 85,
        icon = "🔍",
        unlocked = true
    ),
    GameInfo(
        title = "Quantum Particle Adventure",
        description = "Help the quantum particle jump through energy levels!",
        difficulty = 3,
        category = GameCategory.SCIENCE,
        points = 100,
        icon = "⚛️",
        unlocked = true
    ),
    GameInfo(
        title = "DNA Builder",
        description = "Match base pairs and build the DNA double helix",
        difficulty = 2,
        category = GameCategory.SCIENCE,
        points = 75,
        icon = "🧬",
        unlocked = true
    ),
    GameInfo(
        title = "Circuit Creator",
        description = "Design and test electronic circuits",
        difficulty = 4,
        category = GameCategory.ENGINEERING,
        points = 80,
        icon = "⚡",
        unlocked = true
    ),
    GameInfo(
        title = "Geometric Art Studio",
        description = "Create beautiful patterns using mathematical principles",
        difficulty = 3,
        category = GameCategory.ARTS,
        points = 90,
        icon = "🎨",
        unlocked = true
    ),
    GameInfo(
        title = "Math Quest: The Number Kingdom",
        description = "Defeat Math Monsters by solving arithmetic puzzles",
        difficulty = 2,
        category = GameCategory.MATH,
        points = 100,
        icon = "🔢"
    ),
    GameInfo(
        title = "Physics Playground",
        description = "Build and experiment with physics simulations",
        difficulty = 3,
        category = GameCategory.SCIENCE,
        points = 150,
        icon = "🔬"
    )
)

enum class DashboardTab(val title: String, val icon: ImageVector) {
    LEARN("Learn", Icons.Default.Book),
    GAMES("Games", Icons.Default.SportsEsports),
    PROFILE("Profile", Icons.Default.Person);

    companion object {
        fun fromTitle(title: String): DashboardTab = values().first { it.title == title }
    }
}

@Composable
fun DashboardScreen(
    onNavigateToProfile: () -> Unit,
    onNavigateToChemistry: () -> Unit,
    onNavigateToCircuitBreaker: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(DashboardTab.LEARN) }
    
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                contentColor = Color(0xFF6B4EFF)
            ) {
                DashboardTab.values().forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = null
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedTab) {
                DashboardTab.LEARN -> LearningContent(onNavigateToChemistry)
                DashboardTab.GAMES -> GamesContent(onNavigateToCircuitBreaker)
                DashboardTab.PROFILE -> ProfileContent(onNavigateToProfile)
            }
        }
    }
}

@Composable
private fun LearningContent(onNavigateToChemistry: () -> Unit) {
    var selectedTheme by remember { mutableStateOf("Space") }
    var currentXP by remember { mutableStateOf(350) }
    val levelThreshold = 500
    val currentLevel = (currentXP / levelThreshold) + 1
    val xpProgress = (currentXP % levelThreshold) / levelThreshold.toFloat()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(getThemeBackground(selectedTheme))
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // AI Greeting Section
        AIGreetingCard()
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // XP and Level Progress
        LevelProgressCard(currentLevel, xpProgress, currentXP)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Daily Challenges
        DailyChallengesCard()
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Quick Access Learning Zones
        LearningZonesGrid()
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Learning Paths
        Text(
            text = "Learning Paths",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            LearningModule(
                icon = Icons.Default.Science,
                title = "Science Explorer",
                description = "Discover the wonders of science",
                courses = scienceCourses,
                onNavigateToChemistry = onNavigateToChemistry
            )
            
            LearningModule(
                icon = Icons.Default.Computer,
                title = "Technology Wizard",
                description = "Learn coding and robotics",
                courses = technologyCourses,
                onNavigateToChemistry = onNavigateToChemistry
            )

            // Engineering Module
            LearningModule(
                icon = Icons.Default.Build,
                title = "Engineering Adventures",
                description = "Design and build amazing projects",
                courses = listOf(),
                onNavigateToChemistry = onNavigateToChemistry,
                modifier = Modifier
            )

            // Arts Module
            LearningModule(
                icon = Icons.Default.Palette,
                title = "Creative Arts",
                description = "Combine art with quantum concepts",
                courses = listOf(),
                onNavigateToChemistry = onNavigateToChemistry,
                modifier = Modifier
            )

            // Mathematics Module
            LearningModule(
                icon = Icons.Default.Calculate,
                title = "Math Master",
                description = "Learn mathematics for quantum computing",
                courses = listOf(),
                onNavigateToChemistry = onNavigateToChemistry,
                modifier = Modifier
            )
        }
    }
}

@Composable
private fun AIGreetingCard() {
    val greeting = remember { getTimeBasedGreeting() }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF6B4EFF).copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFF6B4EFF)
                )
                Text(
                    text = greeting,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Text(
                text = "🔥 Amazing! You're on a 3-day learning streak!",
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun LevelProgressCard(currentLevel: Int, progress: Float, currentXP: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Level $currentLevel Explorer",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$currentXP XP",
                    color = Color(0xFF6B4EFF),
                    fontWeight = FontWeight.Medium
                )
            }
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = Color(0xFF6B4EFF),
                trackColor = Color(0xFFE6E1FF)
            )
        }
    }
}

@Composable
private fun DailyChallengesCard() {
    var selectedChallenge by remember { mutableStateOf<Int?>(null) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Today's Challenges",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            
            // Challenge options
            DailyChallenge(
                emoji = "🧪",
                title = "Science Riddle",
                description = "Solve the quantum particle mystery!",
                duration = "5 mins",
                isSelected = selectedChallenge == 0,
                onSelect = { selectedChallenge = 0 }
            )
            
            DailyChallenge(
                emoji = "🧩",
                title = "Math Puzzle",
                description = "Complete the sequence in 3 moves",
                duration = "3 mins",
                isSelected = selectedChallenge == 1,
                onSelect = { selectedChallenge = 1 }
            )
        }
    }
}

@Composable
private fun DailyChallenge(
    emoji: String,
    title: String,
    description: String,
    duration: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF6B4EFF).copy(alpha = 0.1f) else Color.White
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = emoji, fontSize = 24.sp)
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.Medium)
                Text(text = description, fontSize = 14.sp, color = Color.Gray)
            }
            Text(
                text = duration,
                fontSize = 12.sp,
                color = Color(0xFF6B4EFF),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun LearningZonesGrid() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Learning Zones",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            LearningZoneButton(
                icon = "🎯",
                label = "Missions",
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            LearningZoneButton(
                icon = "🎮",
                label = "Game Lab",
                modifier = Modifier.weight(1f)
            )
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            LearningZoneButton(
                icon = "🔬",
                label = "Experiments",
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            LearningZoneButton(
                icon = "📚",
                label = "Knowledge",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun LearningZoneButton(
    icon: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = { /* TODO: Navigate to zone */ },
        modifier = modifier.height(80.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF6B4EFF).copy(alpha = 0.1f),
            contentColor = Color(0xFF6B4EFF)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = icon, fontSize = 24.sp)
            Text(text = label, fontSize = 14.sp)
        }
    }
}

private fun getTimeBasedGreeting(): String {
    return when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
        in 0..11 -> "Good morning, Explorer! 🌅"
        in 12..16 -> "Good afternoon, Explorer! ☀️"
        else -> "Good evening, Explorer! 🌙"
    }
}

private fun getThemeBackground(theme: String): Color {
    return when (theme) {
        "Space" -> Color.White // You can customize with actual theme colors
        "Ocean" -> Color.White
        "Cyber" -> Color.White
        else -> Color.White
    }
}

@Composable
private fun LearningModule(
    icon: ImageVector,
    title: String,
    description: String,
    courses: List<Course>,
    onNavigateToChemistry: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Module header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color(0xFF6B4EFF),
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE6E1FF))
                            .padding(6.dp)
                    )
                    Column {
                        Text(
                            text = title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = description,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (expanded) "Show less" else "Show more"
                    )
                }
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier.padding(top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    courses.forEach { course ->
                        CourseCard(
                            course = course,
                            onNavigateToChemistry = onNavigateToChemistry
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CourseCard(
    course: Course,
    onNavigateToChemistry: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = course.title,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = course.ageRange,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            
            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Topics:",
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
                course.topics.forEach { topic ->
                    Text(
                        text = "• $topic",
                        fontSize = 14.sp
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Activities:",
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
                course.activities.forEach { activity ->
                    Text(
                        text = "• $activity",
                        fontSize = 14.sp
                    )
                }
            }
            
            TextButton(
                onClick = { expanded = !expanded },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(
                    text = if (expanded) "Show Less" else "Show More",
                    color = Color(0xFF6B4EFF)
                )
            }
            
            Button(
                onClick = {
                    if (course.title == "Interactive Chemistry Lab ✨") {
                        onNavigateToChemistry()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6B4EFF)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Start Course")
            }
        }
    }
}

@Composable
private fun GamesContent(onNavigateToCircuitBreaker: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Featured Game
        FeaturedGameCard()
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Daily Challenges
        GameChallengesSection()
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Game Categories with games
        GameCategoriesWithGames(onNavigateToCircuitBreaker)
    }
}

@Composable
private fun GameCategoriesWithGames(onNavigateToCircuitBreaker: () -> Unit) {
    Column {
        Text(
            text = "Game Categories",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        GameCategory.values().forEach { category ->
            CategoryGames(category, onNavigateToCircuitBreaker)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun CategoryGames(category: GameCategory, onNavigateToCircuitBreaker: () -> Unit) {
    val categoryGames = games.filter { it.category == category }
    
    Column {
        Text(
            text = when(category) {
                GameCategory.MATH -> "🔢 Mathematics Games"
                GameCategory.SCIENCE -> "🔬 Science Games"
                GameCategory.CODING -> "💻 Coding Games"
                GameCategory.ENGINEERING -> "⚙️ Engineering Games"
                GameCategory.ARTS -> "🎨 Arts Games"
                GameCategory.TECHNOLOGY -> "🔧 Technology Games"
            },
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        categoryGames.forEach { game ->
            GameCard(game, onNavigateToCircuitBreaker)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ProfileContent(onNavigateToProfile: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .clickable { onNavigateToProfile() }
    ) {
        // User Stats Card
        UserStatsCard()
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // View Full Profile Button
        Button(
            onClick = onNavigateToProfile,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("View Full Profile")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Achievements
        AchievementsSection()
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Learning Progress
        LearningProgressSection()
    }
}

@Composable
private fun LearningProgressSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Learning Progress",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            LearningProgress()
        }
    }
}

@Composable
private fun ChemistryExperimentCard(
    experiment: ChemistryExperiment,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp)
            .then(
                if (expanded) {
                    Modifier.height(200.dp)
                } else {
                    Modifier.height(100.dp)
                }
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = experiment.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) 
                            Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (expanded) "Show less" else "Show more"
                    )
                }
            }
            
            Text(
                text = experiment.description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            if (expanded) {
                // Difficulty indicator
                Row(
                    modifier = Modifier.padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Difficulty: ",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    repeat(experiment.difficulty) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFB300),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                
                // Interactive elements
                Text(
                    text = "Interactive Controls:",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
                experiment.interactiveElements.forEach { element ->
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = when (element.controlType) {
                                ControlType.SLIDER -> Icons.Default.Settings
                                ControlType.BUTTON -> Icons.Default.RadioButtonChecked
                                ControlType.DRAG_DROP -> Icons.Default.DragHandle
                                ControlType.ROTATE_3D -> Icons.Default.Refresh
                                ControlType.MIXER -> Icons.Default.Science
                                ControlType.CIRCUIT_COMPONENT -> Icons.Default.Build
                            },
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = element.name,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = element.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }
                }
                
                // Safety tips
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE8F5E9)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = Color(0xFF4CAF50)
                            )
                            Text(
                                text = " Safety Tips",
                                style = MaterialTheme.typography.titleSmall,
                                color = Color(0xFF4CAF50)
                            )
                        }
                        experiment.safetyTips.forEach { tip ->
                            Text(
                                text = "• $tip",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 24.dp, top = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
} 