package fm.mrc.quantumexplorers.ui.components

import android.media.MediaPlayer
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.*
import kotlin.random.Random
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable

// Data classes for our simulation
data class Chemical(
    val id: String,
    val name: String,
    val color: Color,
    val density: Float, // 0.0 to 1.0
    val reactivity: Float, // 0.0 to 1.0
    val acidity: Float, // 0.0 to 1.0 (0 = acidic, 1 = basic)
    val viscosity: Float, // 0.0 to 1.0
    val icon: Int? = null // Resource ID for icon
)

data class Reaction(
    val id: String,
    val chemicals: List<Chemical>,
    val resultColors: List<Color>,
    val energyLevel: Float, // 0.0 to 1.0
    val duration: Int, // milliseconds
    val effectType: ReactionEffectType,
    val soundEffect: ReactionSoundType
)

data class ReactionParticle(
    var position: Offset,
    var velocity: Offset,
    var color: Color,
    var size: Float,
    var alpha: Float,
    var lifespan: Float // 0.0 to 1.0
)

enum class ReactionEffectType {
    EXPLOSION,
    BUBBLING,
    SWIRLING,
    CRYSTALLIZATION,
    COLOR_CHANGE,
    GLOW,
    SMOKE
}

enum class ReactionSoundType {
    FIZZ,
    BUBBLE,
    SIZZLE,
    POP,
    WHOOSH,
    SPARKLE
}

@Composable
fun RainbowReactionsSimulation(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    // Available chemicals
    val availableChemicals = remember {
        listOf(
            Chemical(
                id = "acid_red",
                name = "Red Acid",
                color = Color(0xFFE53935),
                density = 0.7f,
                reactivity = 0.8f,
                acidity = 0.2f,
                viscosity = 0.3f
            ),
            Chemical(
                id = "base_blue",
                name = "Blue Base",
                color = Color(0xFF1E88E5),
                density = 0.6f,
                reactivity = 0.7f,
                acidity = 0.8f,
                viscosity = 0.4f
            ),
            Chemical(
                id = "catalyst_purple",
                name = "Purple Catalyst",
                color = Color(0xFF8E24AA),
                density = 0.5f,
                reactivity = 0.9f,
                acidity = 0.5f,
                viscosity = 0.2f
            ),
            Chemical(
                id = "solvent_green",
                name = "Green Solvent",
                color = Color(0xFF43A047),
                density = 0.4f,
                reactivity = 0.6f,
                acidity = 0.6f,
                viscosity = 0.5f
            ),
            Chemical(
                id = "crystal_yellow",
                name = "Yellow Crystal",
                color = Color(0xFFFDD835),
                density = 0.8f,
                reactivity = 0.5f,
                acidity = 0.4f,
                viscosity = 0.7f
            ),
            Chemical(
                id = "metal_orange",
                name = "Orange Metal",
                color = Color(0xFFF4511E),
                density = 0.9f,
                reactivity = 0.4f,
                acidity = 0.3f,
                viscosity = 0.8f
            ),
            Chemical(
                id = "gas_cyan",
                name = "Cyan Gas",
                color = Color(0xFF00BCD4),
                density = 0.3f,
                reactivity = 0.8f,
                acidity = 0.7f,
                viscosity = 0.1f
            ),
            Chemical(
                id = "powder_pink",
                name = "Pink Powder",
                color = Color(0xFFEC407A),
                density = 0.6f,
                reactivity = 0.7f,
                acidity = 0.5f,
                viscosity = 0.6f
            )
        )
    }
    
    // State for the simulation
    var selectedChemical by remember { mutableStateOf<Chemical?>(null) }
    var mixingAreaChemicals by remember { mutableStateOf(listOf<Chemical>()) }
    var currentReaction by remember { mutableStateOf<Reaction?>(null) }
    var reactionParticles by remember { mutableStateOf(listOf<ReactionParticle>()) }
    var reactionProgress by remember { mutableStateOf(0f) }
    var isReacting by remember { mutableStateOf(false) }
    var reactionIntensity by remember { mutableStateOf(0f) }
    var dragPosition by remember { mutableStateOf<Offset?>(null) }
    var reactionHistory by remember { mutableStateOf(listOf<Reaction>()) }
    var showCustomizer by remember { mutableStateOf(false) }
    var customChemical by remember { mutableStateOf<Chemical?>(null) }
    
    // Sound effects
    var mediaPlayer: MediaPlayer? by remember { mutableStateOf(null) }
    
    // Reaction animation
    LaunchedEffect(isReacting) {
        if (isReacting && currentReaction != null) {
            // Start reaction animation
            reactionProgress = 0f
            reactionParticles = createInitialParticles(currentReaction!!)
            
            // Play sound effect
            playReactionSound(context, currentReaction!!.soundEffect)
            
            // Animate reaction
            val duration = currentReaction!!.duration
            val startTime = System.currentTimeMillis()
            
            while (isReacting) {
                val currentTime = System.currentTimeMillis()
                val elapsed = currentTime - startTime
                
                if (elapsed >= duration) {
                    // Reaction complete
                    isReacting = false
                    reactionHistory = reactionHistory + currentReaction!!
                    break
                }
                
                // Update progress
                reactionProgress = (elapsed.toFloat() / duration).coerceIn(0f, 1f)
                
                // Update particles
                reactionParticles = updateReactionParticles(
                    reactionParticles,
                    currentReaction!!,
                    (elapsed.toFloat() / duration) * 0.016f
                )
                
                // Update intensity based on progress
                reactionIntensity = when (currentReaction!!.effectType) {
                    ReactionEffectType.EXPLOSION -> sin(reactionProgress * PI.toFloat()) * 2f
                    ReactionEffectType.BUBBLING -> sin(reactionProgress * 10 * PI.toFloat()) * 0.5f + 0.5f
                    ReactionEffectType.SWIRLING -> reactionProgress
                    ReactionEffectType.CRYSTALLIZATION -> sqrt(reactionProgress)
                    ReactionEffectType.COLOR_CHANGE -> reactionProgress
                    ReactionEffectType.GLOW -> sin(reactionProgress * 5 * PI.toFloat()) * 0.5f + 0.5f
                    ReactionEffectType.SMOKE -> 1f - reactionProgress
                }
                
                delay(16) // ~60 FPS
            }
        }
    }
    
    Column(modifier = modifier.fillMaxSize()) {
        // Reaction visualization area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1A237E).copy(alpha = 0.8f),
                            Color(0xFF000000)
                        )
                    )
                )
                .padding(16.dp)
        ) {
            // Laboratory background elements
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Draw lab equipment outlines
                drawLabBackground()
            }
            
            // Mixing area
            MixingArea(
                chemicals = mixingAreaChemicals,
                reaction = currentReaction,
                particles = reactionParticles,
                progress = reactionProgress,
                intensity = reactionIntensity,
                onDrop = { chemical ->
                    mixingAreaChemicals = mixingAreaChemicals + chemical
                    
                    // Check if we can create a reaction
                    if (mixingAreaChemicals.size >= 2) {
                        currentReaction = createReaction(mixingAreaChemicals)
                        isReacting = true
                    }
                },
                onMix = {
                    // Implementation for mixing
                },
                onClear = {
                    mixingAreaChemicals = listOf()
                    currentReaction = null
                    isReacting = false
                    reactionParticles = listOf()
                },
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(240.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF263238))
                    .border(2.dp, Color(0xFF546E7A), CircleShape)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { _ -> 
                                // Handle drag start
                            },
                            onDrag = { _, _ ->
                                // Handle drag
                            },
                            onDragEnd = {
                                // Handle drop of chemical
                            },
                            onDragCancel = {
                                // Handle drag cancel
                            }
                        )
                    }
            )
            
            // Reset button
            Button(
                onClick = {
                    mixingAreaChemicals = listOf()
                    currentReaction = null
                    isReacting = false
                    reactionParticles = listOf()
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6B4EFF)
                )
            ) {
                Text("Reset")
            }
            
            // Customize button
            Button(
                onClick = { showCustomizer = true },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6B4EFF)
                )
            ) {
                Text("Customize")
            }
            
            // Reaction info
            if (isReacting && currentReaction != null) {
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp)
                        .fillMaxWidth(0.8f),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF37474F).copy(alpha = 0.8f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Reaction in Progress",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        LinearProgressIndicator(
                            progress = reactionProgress,
                            modifier = Modifier.fillMaxWidth(),
                            color = currentReaction!!.resultColors.first()
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Text(
                            getReactionDescription(currentReaction!!),
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
        
        // Chemical selection area
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF37474F)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    "Available Chemicals",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(availableChemicals + listOfNotNull(customChemical)) { chemical ->
                        ChemicalItem(
                            chemical = chemical,
                            isSelected = selectedChemical == chemical,
                            onClick = {
                                selectedChemical = chemical
                            }
                        )
                    }
                }
            }
        }
    }
    
    // Chemical customizer dialog
    if (showCustomizer) {
        ChemicalCustomizerDialog(
            onDismiss = { showCustomizer = false },
            onCreateChemical = { newChemical ->
                customChemical = newChemical
                showCustomizer = false
            }
        )
    }
    
    // Cleanup
    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
        }
    }
}

@Composable
private fun ChemicalItem(
    chemical: Chemical,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .size(80.dp)
            .shadow(
                elevation = if (isSelected) 8.dp else 2.dp,
                shape = RoundedCornerShape(8.dp)
            )
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = chemical.color.copy(alpha = 0.8f)
        ),
        border = if (isSelected) BorderStroke(2.dp, Color.White) else null
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Add a gradient overlay for better visibility
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                chemical.color.copy(alpha = 0.5f),
                                chemical.color
                            )
                        )
                    )
            )
            
            // Chemical name with better contrast
            Text(
                text = chemical.name,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp)
                    .background(
                        color = Color.Black.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            )
        }
    }
}

@Composable
private fun ChemicalCustomizerDialog(
    onDismiss: () -> Unit,
    onCreateChemical: (Chemical) -> Unit
) {
    var name by remember { mutableStateOf("Custom Chemical") }
    var color by remember { mutableStateOf(Color(0xFFFF9800)) }
    var density by remember { mutableStateOf(0.5f) }
    var reactivity by remember { mutableStateOf(0.5f) }
    var acidity by remember { mutableStateOf(0.5f) }
    var viscosity by remember { mutableStateOf(0.5f) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Custom Chemical") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Name input
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Chemical Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Color selection
                Text("Color")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf(
                        Color(0xFFE53935), // Red
                        Color(0xFFFB8C00), // Orange
                        Color(0xFFFDD835), // Yellow
                        Color(0xFF43A047), // Green
                        Color(0xFF1E88E5), // Blue
                        Color(0xFF8E24AA), // Purple
                        Color(0xFFEC407A)  // Pink
                    ).forEach { colorOption ->
                        Button(
                            onClick = { color = colorOption },
                            modifier = Modifier.size(32.dp),
                            shape = CircleShape,
                            contentPadding = PaddingValues(0.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorOption
                            )
                        ) {
                            // Empty content
                        }
                    }
                }
                
                // Property sliders
                Text("Density")
                Slider(
                    value = density,
                    onValueChange = { density = it },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Text("Reactivity")
                Slider(
                    value = reactivity,
                    onValueChange = { reactivity = it },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Text("Acidity")
                Slider(
                    value = acidity,
                    onValueChange = { acidity = it },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Text("Viscosity")
                Slider(
                    value = viscosity,
                    onValueChange = { viscosity = it },
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Preview
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(color)
                        .align(Alignment.CenterHorizontally)
                ) {
                    // Show viscosity as wave pattern
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCircle(
                            color = Color.White.copy(alpha = 0.2f),
                            radius = size.minDimension / 2 * viscosity,
                            center = center
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val newChemical = Chemical(
                        id = "custom_${System.currentTimeMillis()}",
                        name = name,
                        color = color,
                        density = density,
                        reactivity = reactivity,
                        acidity = acidity,
                        viscosity = viscosity
                    )
                    onCreateChemical(newChemical)
                }
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun DrawScope.drawLabBackground() {
    // Draw lab table
    drawRect(
        color = Color(0xFF5D4037),
        topLeft = Offset(0f, size.height * 0.8f),
        size = Size(size.width, size.height * 0.2f)
    )
    
    // Draw lab equipment outlines
    val equipmentColor = Color.White.copy(alpha = 0.1f)
    
    // Flask outline
    val flaskPath = Path().apply {
        moveTo(size.width * 0.1f, size.height * 0.7f)
        lineTo(size.width * 0.1f, size.height * 0.5f)
        lineTo(size.width * 0.05f, size.height * 0.4f)
        lineTo(size.width * 0.15f, size.height * 0.4f)
        lineTo(size.width * 0.1f, size.height * 0.5f)
    }
    drawPath(flaskPath, equipmentColor, style = Stroke(width = 2f))
    
    // Beaker outline
    drawRect(
        color = equipmentColor,
        topLeft = Offset(size.width * 0.8f, size.height * 0.6f),
        size = Size(size.width * 0.15f, size.height * 0.2f),
        style = Stroke(width = 2f)
    )
    
    // Test tubes
    repeat(3) { i ->
        val x = size.width * (0.3f + i * 0.1f)
        drawLine(
            color = equipmentColor,
            start = Offset(x, size.height * 0.5f),
            end = Offset(x, size.height * 0.7f),
            strokeWidth = 2f
        )
        drawLine(
            color = equipmentColor,
            start = Offset(x - 5f, size.height * 0.7f),
            end = Offset(x + 5f, size.height * 0.7f),
            strokeWidth = 2f
        )
    }
}

private fun DrawScope.drawChemicalsInMixingArea(chemicals: List<Chemical>) {
    if (chemicals.isEmpty()) return
    
    // Calculate total volume
    val totalVolume = chemicals.size
    
    // Draw layers based on density
    val sortedChemicals = chemicals.sortedByDescending { it.density }
    val layerHeight = size.height / totalVolume
    
    sortedChemicals.forEachIndexed { index, chemical ->
        val yStart = size.height - (index + 1) * layerHeight
        
        // Draw chemical layer
        drawRect(
            color = chemical.color.copy(alpha = 0.8f),
            topLeft = Offset(0f, yStart),
            size = Size(size.width, layerHeight)
        )
        
        // Draw viscosity effect (waves)
        val wavePath = Path().apply {
            val amplitude = 5f * chemical.viscosity
            val frequency = 0.1f / chemical.viscosity
            
            moveTo(0f, yStart)
            
            for (x in 0..size.width.toInt() step 5) {
                val y = yStart + amplitude * sin(x * frequency)
                lineTo(x.toFloat(), y)
            }
            
            lineTo(size.width, yStart)
            close()
        }
        
        drawPath(
            path = wavePath,
            color = chemical.color,
            style = Stroke(width = 2f)
        )
    }
}

private fun DrawScope.drawReaction(
    reaction: Reaction,
    particles: List<ReactionParticle>,
    progress: Float,
    intensity: Float
) {
    // Draw background glow
    drawCircle(
        brush = Brush.radialGradient(
            colors = reaction.resultColors.map { it.copy(alpha = 0.3f * intensity) },
            center = center,
            radius = size.minDimension / 2
        ),
        radius = size.minDimension / 2,
        center = center
    )
    
    // Draw reaction effect
    when (reaction.effectType) {
        ReactionEffectType.EXPLOSION -> {
            // Draw expanding circles
            reaction.resultColors.forEachIndexed { index, color ->
                val radius = size.minDimension * 0.5f * progress * (1f - index * 0.2f)
                drawCircle(
                    color = color.copy(alpha = (1f - progress) * 0.7f),
                    radius = radius,
                    center = center,
                    style = Stroke(width = 4f * intensity)
                )
            }
        }
        ReactionEffectType.BUBBLING -> {
            // Draw bubbles
            particles.forEach { particle ->
                drawCircle(
                    color = particle.color.copy(alpha = particle.alpha),
                    radius = particle.size,
                    center = particle.position
                )
            }
        }
        ReactionEffectType.SWIRLING -> {
            // Draw swirling pattern
            val path = Path().apply {
                moveTo(center.x, center.y)
                
                for (angle in 0..360 step 5) {
                    val radius = size.minDimension * 0.4f * progress * (1f + sin(angle / 30f) * 0.2f)
                    val x = center.x + radius * cos(angle * PI.toFloat() / 180f)
                    val y = center.y + radius * sin(angle * PI.toFloat() / 180f)
                    lineTo(x, y)
                }
            }
            
            drawPath(
                path = path,
                brush = Brush.linearGradient(
                    colors = reaction.resultColors,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, size.height)
                ),
                style = Stroke(width = 4f * intensity)
            )
        }
        ReactionEffectType.CRYSTALLIZATION -> {
            // Draw crystalline structure
            val crystalCount = (20 * progress).toInt() + 1
            
            repeat(crystalCount) { i ->
                val angle = i * (2 * PI.toFloat() / crystalCount)
                val radius = size.minDimension * 0.4f * progress
                val x = center.x + radius * cos(angle)
                val y = center.y + radius * sin(angle)
                
                val color = reaction.resultColors[i % reaction.resultColors.size]
                
                // Draw crystal
                val crystalPath = Path().apply {
                    moveTo(x, y)
                    lineTo(x + 20f * intensity, y)
                    lineTo(x + 10f * intensity, y - 30f * intensity)
                    close()
                }
                
                drawPath(
                    path = crystalPath,
                    color = color.copy(alpha = 0.7f),
                    style = Stroke(width = 2f)
                )
            }
        }
        ReactionEffectType.COLOR_CHANGE -> {
            // Draw color transition
            val colorIndex = (progress * (reaction.resultColors.size - 1)).toInt()
            val nextColorIndex = (colorIndex + 1).coerceAtMost(reaction.resultColors.size - 1)
            val colorProgress = (progress * (reaction.resultColors.size - 1)) - colorIndex
            
            val currentColor = lerp(
                reaction.resultColors[colorIndex],
                reaction.resultColors[nextColorIndex],
                colorProgress
            )
            
            drawCircle(
                color = currentColor.copy(alpha = 0.8f),
                radius = size.minDimension * 0.4f,
                center = center
            )
            
            // Draw ripple effect
            repeat(3) { i ->
                val rippleProgress = (progress + i * 0.2f) % 1f
                drawCircle(
                    color = currentColor.copy(alpha = (1f - rippleProgress) * 0.3f),
                    radius = size.minDimension * 0.4f * rippleProgress,
                    center = center,
                    style = Stroke(width = 2f)
                )
            }
        }
        ReactionEffectType.GLOW -> {
            // Draw glowing effect
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        reaction.resultColors.first(),
                        reaction.resultColors.first().copy(alpha = 0f)
                    ),
                    center = center,
                    radius = size.minDimension * 0.5f
                ),
                radius = size.minDimension * 0.5f * (0.8f + 0.2f * intensity),
                center = center
            )
            
            // Draw particles
            particles.forEach { particle ->
                drawCircle(
                    color = particle.color.copy(alpha = particle.alpha * intensity),
                    radius = particle.size,
                    center = particle.position
                )
            }
        }
        ReactionEffectType.SMOKE -> {
            // Draw smoke particles
            particles.forEach { particle ->
                drawCircle(
                    color = particle.color.copy(alpha = particle.alpha),
                    radius = particle.size,
                    center = particle.position
                )
            }
        }
    }
}

private fun createInitialParticles(reaction: Reaction): List<ReactionParticle> {
    val particles = mutableListOf<ReactionParticle>()
    
    // Number of particles based on energy level
    val particleCount = (50 + (reaction.energyLevel * 100).toInt()).coerceIn(50, 200)
    
    // Create particles with positions distributed within the reaction area
    repeat(particleCount) {
        // Calculate random position within a circle
        val angle = Random.nextFloat() * 2 * PI.toFloat()
        val distance = Random.nextFloat() * 150f
        val x = 500f + cos(angle) * distance
        val y = 200f + sin(angle) * distance
        
        // Random velocity based on energy level
        val speed = 1f + Random.nextFloat() * 5f * reaction.energyLevel
        val direction = Random.nextFloat() * 2 * PI.toFloat()
        val vx = cos(direction) * speed
        val vy = sin(direction) * speed
        
        // Random color from result colors
        val color = reaction.resultColors[Random.nextInt(reaction.resultColors.size)]
        
        // Create particle
        particles.add(
            ReactionParticle(
                position = Offset(x, y),
                velocity = Offset(vx, vy),
                color = color,
                size = 5f + Random.nextFloat() * 10f,
                alpha = 0.7f + Random.nextFloat() * 0.3f,
                lifespan = 0.5f + Random.nextFloat() * 0.5f
            )
        )
    }
    
    return particles
}

private fun updateReactionParticles(
    particles: List<ReactionParticle>,
    reaction: Reaction,
    deltaTime: Float
): List<ReactionParticle> {
    return particles.mapNotNull { particle ->
        // Update position based on velocity
        val newPosition = particle.position + particle.velocity * deltaTime
        
        // Update lifespan
        val newLifespan = particle.lifespan - deltaTime * 0.2f
        
        // Remove particles that have expired
        if (newLifespan <= 0f) {
            null
        } else {
            // Apply different movement patterns based on effect type
            val newVelocity = when (reaction.effectType) {
                ReactionEffectType.EXPLOSION -> {
                    // Particles move outward
                    val direction = atan2(particle.position.y - 200f, particle.position.x - 500f)
                    val speed = particle.velocity.getDistance() * (1f + deltaTime * 0.5f)
                    Offset(cos(direction) * speed, sin(direction) * speed)
                }
                ReactionEffectType.BUBBLING -> {
                    // Particles move upward with some randomness
                    Offset(
                        particle.velocity.x + (Random.nextFloat() - 0.5f) * 2f,
                        particle.velocity.y - deltaTime * 10f
                    )
                }
                ReactionEffectType.SWIRLING -> {
                    // Particles move in a circular pattern
                    val centerX = 500f
                    val centerY = 200f
                    val dx = particle.position.x - centerX
                    val dy = particle.position.y - centerY
                    val distance = sqrt(dx * dx + dy * dy)
                    val angle = atan2(dy, dx) + deltaTime * 2f
                    val newX = centerX + cos(angle) * distance
                    val newY = centerY + sin(angle) * distance
                    Offset((newX - particle.position.x) / deltaTime, (newY - particle.position.y) / deltaTime)
                }
                ReactionEffectType.CRYSTALLIZATION -> {
                    // Particles slow down and align to a grid
                    particle.velocity * 0.95f
                }
                ReactionEffectType.COLOR_CHANGE -> {
                    // Particles move randomly
                    Offset(
                        particle.velocity.x + (Random.nextFloat() - 0.5f) * 5f,
                        particle.velocity.y + (Random.nextFloat() - 0.5f) * 5f
                    )
                }
                ReactionEffectType.GLOW -> {
                    // Particles pulse outward and inward
                    val direction = atan2(particle.position.y - 200f, particle.position.x - 500f)
                    val pulseFactor = sin(newLifespan * 10f) * 5f
                    Offset(cos(direction) * pulseFactor, sin(direction) * pulseFactor)
                }
                ReactionEffectType.SMOKE -> {
                    // Particles rise and spread
                    Offset(
                        particle.velocity.x + (Random.nextFloat() - 0.5f) * 2f,
                        particle.velocity.y - deltaTime * 5f
                    )
                }
            }
            
            // Update particle
            particle.copy(
                position = newPosition,
                velocity = newVelocity,
                lifespan = newLifespan,
                alpha = newLifespan * particle.alpha / particle.lifespan,
                size = particle.size * (0.9f + 0.2f * sin(newLifespan * 10f)) // Pulsing size
            )
        }
    }
}

private fun createReaction(chemicals: List<Chemical>): Reaction {
    // Calculate reaction properties based on the chemicals
    val avgReactivity = chemicals.map { it.reactivity }.average().toFloat()
    val avgDensity = chemicals.map { it.density }.average().toFloat()
    val avgViscosity = chemicals.map { it.viscosity }.average().toFloat()
    
    // Determine reaction colors
    val resultColors = mutableListOf<Color>()
    
    // Add base colors from the chemicals
    resultColors.addAll(chemicals.map { it.color })
    
    // Add some blended colors
    if (chemicals.size >= 2) {
        for (i in 0 until chemicals.size - 1) {
            for (j in i + 1 until chemicals.size) {
                val blendedColor = lerp(
                    chemicals[i].color,
                    chemicals[j].color,
                    0.5f
                )
                resultColors.add(blendedColor)
            }
        }
    }
    
    // Determine reaction effect type based on chemical properties
    val effectType = when {
        avgReactivity > 0.8f -> ReactionEffectType.EXPLOSION
        avgDensity < 0.3f -> ReactionEffectType.SMOKE
        avgViscosity < 0.3f -> ReactionEffectType.BUBBLING
        chemicals.any { it.acidity < 0.3f } && chemicals.any { it.acidity > 0.7f } -> ReactionEffectType.GLOW
        chemicals.size >= 3 -> ReactionEffectType.SWIRLING
        chemicals.any { it.density > 0.7f } -> ReactionEffectType.CRYSTALLIZATION
        else -> ReactionEffectType.COLOR_CHANGE
    }
    
    // Determine sound effect
    val soundEffect = when (effectType) {
        ReactionEffectType.EXPLOSION -> ReactionSoundType.POP
        ReactionEffectType.BUBBLING -> ReactionSoundType.BUBBLE
        ReactionEffectType.SWIRLING -> ReactionSoundType.WHOOSH
        ReactionEffectType.CRYSTALLIZATION -> ReactionSoundType.SPARKLE
        ReactionEffectType.GLOW -> ReactionSoundType.SIZZLE
        else -> ReactionSoundType.FIZZ
    }
    
    // Calculate energy level and duration
    val energyLevel = avgReactivity * 0.7f + Random.nextFloat() * 0.3f
    val duration = (3000 + (avgReactivity * 7000).toInt()).coerceIn(3000, 10000)
    
    return Reaction(
        id = "reaction_${System.currentTimeMillis()}",
        chemicals = chemicals,
        resultColors = resultColors,
        energyLevel = energyLevel,
        duration = duration,
        effectType = effectType,
        soundEffect = soundEffect
    )
}

private fun getReactionDescription(reaction: Reaction): String {
    val chemicalNames = reaction.chemicals.joinToString(", ") { it.name }
    
    val effectDescription = when (reaction.effectType) {
        ReactionEffectType.EXPLOSION -> "explosive reaction"
        ReactionEffectType.BUBBLING -> "bubbling reaction"
        ReactionEffectType.SWIRLING -> "swirling reaction"
        ReactionEffectType.CRYSTALLIZATION -> "crystallization"
        ReactionEffectType.COLOR_CHANGE -> "color-changing reaction"
        ReactionEffectType.GLOW -> "glowing reaction"
        ReactionEffectType.SMOKE -> "smoking reaction"
    }
    
    val energyDescription = when {
        reaction.energyLevel > 0.8f -> "high-energy"
        reaction.energyLevel > 0.5f -> "moderate-energy"
        else -> "low-energy"
    }
    
    return "Mixing $chemicalNames creates a $energyDescription $effectDescription."
}

private fun playReactionSound(context: android.content.Context, soundType: ReactionSoundType) {
    // In a real implementation, we would load and play actual sound resources
    // For now, we'll just simulate the sound effect
    
    // Example of how to play a sound if resources were available:
    /*
    val soundResId = when (soundType) {
        ReactionSoundType.FIZZ -> R.raw.fizz_sound
        ReactionSoundType.BUBBLE -> R.raw.bubble_sound
        ReactionSoundType.SIZZLE -> R.raw.sizzle_sound
        ReactionSoundType.POP -> R.raw.pop_sound
        ReactionSoundType.WHOOSH -> R.raw.whoosh_sound
        ReactionSoundType.SPARKLE -> R.raw.sparkle_sound
    }
    
    val mediaPlayer = MediaPlayer.create(context, soundResId)
    mediaPlayer.setOnCompletionListener { it.release() }
    mediaPlayer.start()
    */
}

// Helper function to interpolate between colors
private fun lerp(start: Color, end: Color, fraction: Float): Color {
    return Color(
        red = start.red + (end.red - start.red) * fraction,
        green = start.green + (end.green - start.green) * fraction,
        blue = start.blue + (end.blue - start.blue) * fraction,
        alpha = start.alpha + (end.alpha - start.alpha) * fraction
    )
}

@Composable
private fun MixingArea(
    chemicals: List<Chemical>,
    reaction: Reaction?,
    particles: List<ReactionParticle>,
    progress: Float,
    intensity: Float,
    onDrop: (Chemical) -> Unit,
    onMix: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF263238)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Background gradient for better visual appeal
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF263238),
                                Color(0xFF37474F)
                            )
                        )
                    )
            )
            
            // Reaction visualization
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Draw beaker outline
                val beakerWidth = size.width * 0.8f
                val beakerHeight = size.height * 0.7f
                val beakerLeft = (size.width - beakerWidth) / 2
                val beakerTop = (size.height - beakerHeight) * 0.9f
                
                // Draw beaker glass (semi-transparent)
                drawRect(
                    color = Color.White.copy(alpha = 0.1f),
                    topLeft = Offset(beakerLeft, beakerTop),
                    size = Size(beakerWidth, beakerHeight),
                    style = Stroke(width = 2f)
                )
                
                // Draw liquid in beaker
                if (chemicals.isNotEmpty()) {
                    // Calculate liquid height based on number of chemicals
                    val liquidHeight = (beakerHeight * 0.8f) * (chemicals.size.coerceAtMost(5) / 5f)
                    
                    // Create gradient from chemical colors
                    val chemicalColors = chemicals.map { it.color }
                    val gradient = if (chemicalColors.size > 1) {
                        Brush.verticalGradient(
                            colors = chemicalColors,
                            startY = beakerTop + beakerHeight - liquidHeight,
                            endY = beakerTop + beakerHeight
                        )
                    } else {
                        Brush.verticalGradient(
                            colors = listOf(
                                chemicalColors.first(),
                                chemicalColors.first().copy(alpha = 0.7f)
                            ),
                            startY = beakerTop + beakerHeight - liquidHeight,
                            endY = beakerTop + beakerHeight
                        )
                    }
                    
                    // Draw liquid with gradient
                    drawRect(
                        brush = gradient,
                        topLeft = Offset(beakerLeft, beakerTop + beakerHeight - liquidHeight),
                        size = Size(beakerWidth, liquidHeight)
                    )
                    
                    // Draw bubbles if reacting
                    if (reaction != null) {
                        // Draw reaction particles
                        particles.forEach { particle ->
                            drawCircle(
                                color = particle.color.copy(alpha = particle.alpha),
                                radius = particle.size,
                                center = particle.position
                            )
                        }
                        
                        // Draw reaction glow
                        drawCircle(
                            color = reaction.resultColors.first().copy(alpha = 0.3f * intensity),
                            radius = 100f * intensity,
                            center = Offset(size.width / 2, beakerTop + beakerHeight - liquidHeight / 2)
                        )
                    }
                }
                
                // Draw chemical indicators
                chemicals.forEachIndexed { index, chemical ->
                    val indicatorSize = 30f
                    val spacing = 10f
                    val startX = beakerLeft + 20f
                    val startY = beakerTop + 20f + (indicatorSize + spacing) * index
                    
                    drawCircle(
                        color = chemical.color,
                        radius = indicatorSize / 2,
                        center = Offset(startX, startY)
                    )
                }
            }
            
            // Control buttons
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = onMix,
                    enabled = chemicals.size >= 2,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50),
                        disabledContainerColor = Color(0xFF4CAF50).copy(alpha = 0.5f)
                    )
                ) {
                    Text("Mix")
                }
                
                Button(
                    onClick = onClear,
                    enabled = chemicals.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF44336),
                        disabledContainerColor = Color(0xFFF44336).copy(alpha = 0.5f)
                    )
                ) {
                    Text("Clear")
                }
            }
            
            // Drop area for chemicals
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            // Handle tap to add selected chemical
                        }
                    }
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { _ -> 
                                // Handle drag start
                            },
                            onDrag = { _, _ ->
                                // Handle drag
                            },
                            onDragEnd = {
                                // Handle drop of chemical
                            },
                            onDragCancel = {
                                // Handle drag cancel
                            }
                        )
                    }
            )
            
            // Reaction progress indicator
            if (reaction != null) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .align(Alignment.TopCenter)
                        .padding(top = 16.dp),
                    color = reaction.resultColors.first(),
                    trackColor = Color.DarkGray
                )
            }
        }
    }
} 