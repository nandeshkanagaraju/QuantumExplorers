package fm.mrc.quantumexplorers.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.geometry.Offset
import fm.mrc.quantumexplorers.ui.components.AtomicDancePartySimulation
import fm.mrc.quantumexplorers.ui.components.RainbowReactionsSimulation

// Define experiments list here instead of using the private one from DashboardScreen
private val chemistryLabExperiments = listOf(
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
    ),
    ChemistryExperiment(
        title = "State Shifter ❄️",
        description = "Transform matter between solid, liquid, and gas!",
        difficulty = 2,
        visualizationType = VisualizationType.STATE_CHANGE,
        interactiveElements = listOf(
            InteractiveElement(
                name = "Temperature Adjuster",
                description = "Heat or cool the matter",
                controlType = ControlType.SLIDER
            ),
            InteractiveElement(
                name = "Pressure Control",
                description = "Adjust the pressure",
                controlType = ControlType.SLIDER
            )
        ),
        safetyTips = listOf(
            "Be careful with extreme temperatures in real life",
            "Observe pressure changes safely"
        )
    )
)

@Composable
fun ChemistryLabScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedExperiment by remember { mutableStateOf<ChemistryExperiment?>(null) }
    var showReactionVisualizer by remember { mutableStateOf(false) }
    var currentPoints by remember { mutableStateOf(0) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Interactive Chemistry Lab ✨") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Points"
                        )
                        Text("$currentPoints pts", fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            // Welcome Section
            item {
                WelcomeSection()
            }

            // Features Section
            item {
                FeaturesSection()
            }

            // Available Experiments
            item {
                Text(
                    "Available Experiments 🧪",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            items(chemistryLabExperiments) { experiment ->
                ExperimentCard(
                    experiment = experiment,
                    onStartExperiment = {
                        selectedExperiment = experiment
                        showReactionVisualizer = true
                        currentPoints += 10
                    }
                )
            }
        }

        // Reaction Visualizer Dialog
        if (showReactionVisualizer && selectedExperiment != null) {
            ReactionVisualizerDialog(
                experiment = selectedExperiment!!,
                onDismiss = { showReactionVisualizer = false },
                onComplete = { points ->
                    currentPoints += points
                }
            )
        }
    }
}

@Composable
private fun WelcomeSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF6B4EFF).copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "Welcome to Your Chemistry Adventure! 🚀",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Get ready to explore the fascinating world of chemistry through interactive experiments and amazing visualizations!",
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun FeaturesSection() {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        FeatureItem(
            icon = Icons.Default.Animation,
            title = "Interactive Reaction Visualizations",
            description = "Watch atoms collide and molecules form!"
        )
        FeatureItem(
            icon = Icons.Default.Science,
            title = "Fun & Engaging Experiments",
            description = "Virtual labs where you control the reactions!"
        )
        FeatureItem(
            icon = Icons.Default.EmojiEvents,
            title = "Gamified Learning",
            description = "Earn rewards as you explore the world of chemistry!"
        )
    }
}

@Composable
private fun FeatureItem(
    icon: ImageVector,
    title: String,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = Color(0xFF6B4EFF),
                modifier = Modifier.size(32.dp)
            )
            Column {
                Text(
                    title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    description,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ExperimentCard(
    experiment: ChemistryExperiment,
    onStartExperiment: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                experiment.title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(experiment.description)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Difficulty indicator
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Difficulty: ")
                    repeat(experiment.difficulty) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Button(
                    onClick = onStartExperiment,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6B4EFF)
                    )
                ) {
                    Text("Start Experiment")
                }
            }
        }
    }
}

@Composable
private fun ReactionVisualizerDialog(
    experiment: ChemistryExperiment,
    onDismiss: () -> Unit,
    onComplete: (points: Int) -> Unit
) {
    var isCompleted by remember { mutableStateOf(false) }
    
    // Use DisposableEffect instead of LaunchedEffect with delay
    DisposableEffect(Unit) {
        // Mark as completed after some time
        val handler = android.os.Handler(android.os.Looper.getMainLooper())
        val runnable = Runnable {
            if (!isCompleted) {
                isCompleted = true
                onComplete(25)
            }
        }
        
        // Schedule the completion after 10 seconds
        handler.postDelayed(runnable, 10000)
        
        // Clean up when the composable is disposed
        onDispose {
            handler.removeCallbacks(runnable)
        }
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(experiment.title) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Use our new components based on experiment type
                when (experiment.visualizationType) {
                    VisualizationType.PARTICLE_COLLISION -> {
                        // Full-featured atomic dance party simulation
                        AtomicDancePartySimulation(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(400.dp)
                        )
                    }
                    VisualizationType.MOLECULAR_FORMATION -> {
                        // Rainbow Reactions simulation
                        RainbowReactionsSimulation(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(400.dp)
                        )
                    }
                    else -> {
                        // For other experiment types, use the existing implementation
                        // Particle Selector
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    "Select Particle Type",
                                    fontWeight = FontWeight.Bold
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    ParticleType.values().forEach { particleType ->
                                        ParticleButton(
                                            particleType = particleType,
                                            isSelected = false,
                                            onClick = { /* Handle selection */ }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Safety tips
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFF3E0)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(
                            "Safety Tips:",
                            fontWeight = FontWeight.Bold
                        )
                        experiment.safetyTips.forEach { tip ->
                            Text("• $tip")
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
private fun ParticleButton(
    particleType: ParticleType,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        modifier = Modifier.size(64.dp)
    ) {
        Text(
            text = particleType.getSymbol(),
            color = if (isSelected) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )
    }
}

enum class ParticleType {
    HYDROGEN,
    OXYGEN,
    CARBON,
    NITROGEN;

    fun getSymbol() = when (this) {
        HYDROGEN -> "H"
        OXYGEN -> "O"
        CARBON -> "C"
        NITROGEN -> "N"
    }

    fun getFullName() = when (this) {
        HYDROGEN -> "Hydrogen"
        OXYGEN -> "Oxygen"
        CARBON -> "Carbon"
        NITROGEN -> "Nitrogen"
    }

    fun getDescription() = when (this) {
        HYDROGEN -> "The lightest element, essential for water and life."
        OXYGEN -> "Vital for breathing and combustion reactions."
        CARBON -> "The building block of organic chemistry and life."
        NITROGEN -> "Key component of amino acids and the atmosphere."
    }

    fun getAtomicNumber() = when (this) {
        HYDROGEN -> 1
        OXYGEN -> 8
        CARBON -> 6
        NITROGEN -> 7
    }

    fun getMass() = when (this) {
        HYDROGEN -> "1.008"
        OXYGEN -> "15.999"
        CARBON -> "12.011"
        NITROGEN -> "14.007"
    }
} 