package fm.mrc.quantumexplorers.ui.components

import android.media.MediaPlayer
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.*
import kotlin.random.Random
import kotlin.collections.ArrayDeque
import android.content.Context
import android.util.Log
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontStyle

// Updated imports for DrawScope transformations
import androidx.compose.ui.graphics.drawscope.withTransform

// Add import at the top of the file
import androidx.compose.material.icons.filled.LinearScale

// Data classes for our simulation
enum class CircuitComponentType {
    WIRE,
    BATTERY,
    BULB,
    SWITCH,
    RESISTOR,
    CIRCUIT_BREAKER
}

data class CircuitComponent(
    val id: String,
    val type: CircuitComponentType,
    var position: Offset,
    var rotation: Int = 0, // 0, 90, 180, 270 degrees
    var isConnected: Boolean = false,
    var isOn: Boolean = false,
    var currentFlow: Float = 0f,
    var maxCurrent: Float = 1.0f,
    var isTripped: Boolean = false
)

data class CircuitConnection(
    val id: String,
    val fromComponent: String,
    val toComponent: String,
    var isActive: Boolean = false
)

data class CircuitLevel(
    val id: Int,
    val title: String = "Level $id",
    val description: String = "Complete the circuit",
    val learningObjective: String = "Learn about electrical circuits",
    val gridSize: Pair<Int, Int>,
    val components: List<CircuitComponent>,
    val connections: List<CircuitConnection> = listOf(),
    val availableComponents: List<CircuitComponentType>,
    val targetState: Boolean,
    val maxCurrent: Float,
    val successMessage: String = "Great job! You've completed the circuit.",
    val failureMessage: String = "Try again. Make sure all connections are correct."
)

data class CircuitFeedback(
    val message: String,
    val isSuccess: Boolean
)

@Composable
fun CircuitBreakerSimulation(
    modifier: Modifier = Modifier,
    onLevelComplete: (Int) -> Unit = {}
) {
    // Store the canvas size
    var canvasSize by remember { mutableStateOf(Size(0f, 0f)) }
    
    // State for showing component info
    var showComponentInfo by remember { mutableStateOf<CircuitComponentType?>(null) }
    
    // State for showing how to play dialog
    var showHowToPlay by remember { mutableStateOf(false) }
    
    // State for showing hint dialog
    var showHint by remember { mutableStateOf(false) }
    
    // Add state variables for the wire tutorial
    var showWireTutorial by remember { mutableStateOf(false) }
    
    // New state variables for fixed component placement
    var showPlacementDialog by remember { mutableStateOf(false) }
    var componentToPlace by remember { mutableStateOf<CircuitComponentType?>(null) }
    var placementPosition by remember { mutableStateOf(Offset.Zero) }
    
    // First, add state variables for component selection
    var selectedComponentType by remember { mutableStateOf<CircuitComponentType?>(null) }
    var isPlacingComponent by remember { mutableStateOf(false) }
    
    // Define circuit levels
    val circuitLevels = remember {
        listOf(
            CircuitLevel(
                id = 1,
                title = "Simple Circuit",
                description = "Connect a battery to a light bulb to make it glow.",
                learningObjective = "Learn how to create a complete circuit with a power source and a load.",
                gridSize = Pair(6, 6),
                components = listOf(
                    CircuitComponent(
                        id = "battery_1",
                        type = CircuitComponentType.BATTERY,
                        position = Offset(100f, 200f),
                        maxCurrent = 1.0f
                    ),
                    CircuitComponent(
                        id = "bulb_1",
                        type = CircuitComponentType.BULB,
                        position = Offset(300f, 200f)
                    )
                ),
                connections = listOf(),
                availableComponents = listOf(
                    CircuitComponentType.WIRE,
                    CircuitComponentType.SWITCH
                ),
                targetState = true,
                maxCurrent = 1.0f,
                successMessage = "Great job! You've created a complete circuit. The electrons flow from the battery through the wires to the bulb, making it light up.",
                failureMessage = "The bulb isn't lighting up. Make sure you've connected the battery to the bulb with wires to complete the circuit."
            ),
            CircuitLevel(
                id = 2,
                title = "Adding a Switch",
                description = "Add a switch to control when the bulb lights up.",
                learningObjective = "Learn how switches control the flow of electricity in a circuit.",
                gridSize = Pair(8, 8),
                components = listOf(
                    CircuitComponent(
                        id = "battery_1",
                        type = CircuitComponentType.BATTERY,
                        position = Offset(100f, 200f),
                        maxCurrent = 1.0f
                    ),
                    CircuitComponent(
                        id = "bulb_1",
                        type = CircuitComponentType.BULB,
                        position = Offset(300f, 200f)
                    )
                ),
                connections = listOf(),
                availableComponents = listOf(
                    CircuitComponentType.WIRE,
                    CircuitComponentType.SWITCH,
                    CircuitComponentType.RESISTOR
                ),
                targetState = true,
                maxCurrent = 1.0f,
                successMessage = "Excellent! Now you can turn the bulb on and off by toggling the switch.",
                failureMessage = "The switch needs to be in the closed position (on) for the bulb to light up."
            ),
            CircuitLevel(
                id = 3,
                title = "Circuit Protection",
                description = "Use a circuit breaker to protect the circuit from excessive current.",
                learningObjective = "Learn how circuit breakers protect electrical systems from damage.",
                gridSize = Pair(10, 10),
                components = listOf(
                    CircuitComponent(
                        id = "battery_1",
                        type = CircuitComponentType.BATTERY,
                        position = Offset(100f, 200f),
                        maxCurrent = 3.0f
                    ),
                    CircuitComponent(
                        id = "bulb_1",
                        type = CircuitComponentType.BULB,
                        position = Offset(300f, 200f)
                    )
                ),
                connections = listOf(),
                availableComponents = listOf(
                    CircuitComponentType.WIRE,
                    CircuitComponentType.SWITCH,
                    CircuitComponentType.RESISTOR,
                    CircuitComponentType.CIRCUIT_BREAKER
                ),
                targetState = true,
                maxCurrent = 1.0f,
                successMessage = "Perfect! The circuit breaker protects the circuit by cutting off power when the current exceeds safe levels.",
                failureMessage = "The circuit needs protection! Try adding a circuit breaker in series with the bulb."
            )
        )
    }
    
    // State for the simulation
    var currentLevelIndex by remember { mutableStateOf(0) }
    var currentLevel by remember { mutableStateOf(circuitLevels[currentLevelIndex]) }
    var components by remember { mutableStateOf(currentLevel.components) }
    var connections by remember { mutableStateOf(currentLevel.connections) }
    var selectedComponent by remember { mutableStateOf<CircuitComponent?>(null) }
    var isCircuitComplete by remember { mutableStateOf(false) }
    var isBreakerTripped by remember { mutableStateOf(false) }
    var feedback by remember { mutableStateOf<CircuitFeedback?>(null) }
    var points by remember { mutableStateOf(0) }
    
    // Fix the LaunchedEffect to ensure components are properly initialized
    LaunchedEffect(currentLevel, canvasSize) {
        if (canvasSize.width > 0 && canvasSize.height > 0) {
            // Clear existing components when level changes
            components = emptyList()
            connections = emptyList()
            
            // Add battery at fixed position (left side)
            val batteryPosition = Offset(canvasSize.width * 0.25f, canvasSize.height * 0.5f)
            val batteryComponent = CircuitComponent(
                id = "battery_fixed",
                type = CircuitComponentType.BATTERY,
                position = batteryPosition,
                rotation = 0,
                isOn = true,
                maxCurrent = 1.0f
            )
            
            // Add bulb at fixed position (right side)
            val bulbPosition = Offset(canvasSize.width * 0.75f, canvasSize.height * 0.5f)
            val bulbComponent = CircuitComponent(
                id = "bulb_fixed",
                type = CircuitComponentType.BULB,
                position = bulbPosition,
                rotation = 0,
                isOn = false
            )
            
            // Update components list
            components = listOf(batteryComponent, bulbComponent)
            
            // Log the components for debugging
            Log.d("CircuitBreaker", "Components initialized: ${components.size}")
            Log.d("CircuitBreaker", "Battery position: $batteryPosition")
            Log.d("CircuitBreaker", "Bulb position: $bulbPosition")
            Log.d("CircuitBreaker", "Canvas size: $canvasSize")
            
            // Add a default wire in the middle to help users understand how to connect components
            if (currentLevelIndex == 0) {
                delay(500) // Short delay to ensure the canvas is ready
                
                // Add a wire in the middle
                val wirePosition = Offset(canvasSize.width * 0.5f, canvasSize.height * 0.5f)
                val wireComponent = CircuitComponent(
                    id = "wire_default",
                    type = CircuitComponentType.WIRE,
                    position = wirePosition,
                    rotation = 0,
                    isOn = false
                )
                
                // Update components list with the wire
                components = components + wireComponent
                
                // Show a hint about connecting components
                feedback = CircuitFeedback(
                    message = "Connect the battery to the bulb using wires!",
                    isSuccess = true
                )
                
                // Show tutorial after a short delay
                delay(1000)
                showWireTutorial = true
            }
        }
    }
    
    // Circuit simulation
    LaunchedEffect(components, connections) {
        // Simulate circuit behavior
        val updatedComponents = simulateCircuit(components, connections)
        
        // Check if circuit is complete
        val allBulbsOn = updatedComponents.filter { it.type == CircuitComponentType.BULB }
            .all { it.isOn }
        
        // Check if breaker is tripped
        val anyBreakerTripped = updatedComponents.any { 
            it.type == CircuitComponentType.CIRCUIT_BREAKER && it.currentFlow > it.maxCurrent 
        }
        
        if (anyBreakerTripped && !isBreakerTripped) {
            // Play breaker sound
            playSound(CircuitSoundType.CLICK)
            feedback = CircuitFeedback(
                message = "Circuit breaker tripped! Too much current!",
                isSuccess = false
            )
        }
        
        isBreakerTripped = anyBreakerTripped
        
        if (allBulbsOn && !isBreakerTripped && !isCircuitComplete) {
            // Level complete!
            isCircuitComplete = true
            
            // Play success sound
            playSound(CircuitSoundType.CONNECT)
            
            // Award points
            val levelPoints = 50 + (currentLevel.id * 25)
            points += levelPoints
            
            // Show feedback
            feedback = CircuitFeedback(
                message = "Circuit complete! +$levelPoints points",
                isSuccess = true
            )
            
            // Notify level completion
            onLevelComplete(currentLevel.id)
            
            // Auto-advance to next level after delay
            delay(3000)
            if (currentLevelIndex < circuitLevels.size - 1) {
                currentLevelIndex++
                currentLevel = circuitLevels[currentLevelIndex]
                components = currentLevel.components
                connections = currentLevel.connections
                isCircuitComplete = false
                isBreakerTripped = false
            }
        }
        
        components = updatedComponents
    }
    
    // Feedback message auto-dismiss
    LaunchedEffect(feedback) {
        if (feedback != null) {
            delay(3000)
            feedback = null
        }
    }
    
    Column(modifier = modifier.fillMaxSize()) {
        // Single top app bar with level info
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 4.dp
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Header with title and points
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back button and title
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Circuit Breaker",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.ElectricBolt,
                            contentDescription = null,
                            tint = Color(0xFFFFC107)
                        )
                    }
                    
                    // Points and help
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Points",
                            tint = Color(0xFFFFC107)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$points pts",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Help,
                            contentDescription = "Help",
                            modifier = Modifier.clickable { showHowToPlay = true }
                        )
                    }
                }
                
                // Game description card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFEEE6FF)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Build Your Own Circuits!",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = null
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Text(
                            text = "Drag and drop components to create working circuits. Learn about electricity, conductivity, and circuit protection!",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                
                // Level info
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Level ${currentLevel.id}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Points",
                                tint = Color(0xFFFFC107),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$points pts",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    
                    Text(
                        text = "Level ${currentLevel.id} of ${circuitLevels.size}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // Progress bar
                    LinearProgressIndicator(
                        progress = { currentLevel.id.toFloat() / circuitLevels.size.toFloat() },
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFF6B4EFF),
                        trackColor = Color(0xFFE0E0E0)
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // Level description
                    Text(
                        text = currentLevel.description,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        
        // Circuit board
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .background(
                    color = Color(0xFF1E2A30),
                    shape = RoundedCornerShape(16.dp)
                )
                .border(
                    width = 2.dp,
                    color = Color(0xFF455A64),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(8.dp)
        ) {
            // Grid background with more visible grid
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
            ) {
                // Store the canvas size
                canvasSize = size
                
                // Draw background
                drawRect(
                    color = Color(0xFF263238),
                    size = size
                )
                
                // Draw grid with more visible lines
                drawCircuitGrid(size)
                
                // Draw a border around the circuit board
                drawRect(
                    color = Color(0xFF455A64),
                    size = size,
                    style = Stroke(width = 4f)
                )
                
                // Draw fixed component positions indicators
                // Battery position indicator
                val batteryPosition = Offset(size.width * 0.25f, size.height * 0.5f)
                drawCircle(
                    color = Color(0xFFFFC107).copy(alpha = 0.3f),
                    radius = 60f,
                    center = batteryPosition
                )
                
                // Bulb position indicator
                val bulbPosition = Offset(size.width * 0.75f, size.height * 0.5f)
                drawCircle(
                    color = Color(0xFFFFEB3B).copy(alpha = 0.3f),
                    radius = 60f,
                    center = bulbPosition
                )
            }
            
            // Components and connections
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            if (isPlacingComponent && selectedComponentType != null) {
                                when (selectedComponentType) {
                                    CircuitComponentType.WIRE -> {
                                        // Create a new wire at the tapped position
                                        val wireId = "wire_${System.currentTimeMillis()}"
                                        val newWire = CircuitComponent(
                                            id = wireId,
                                            type = CircuitComponentType.WIRE,
                                            position = offset,
                                            rotation = 0,
                                            isOn = false
                                        )
                                        
                                        // Add the wire to components
                                        components = components + newWire
                                        playSound(CircuitSoundType.PLACE)
                                        
                                        // Try to create connections with nearby components
                                        val nearbyComponents = components
                                            .filter { it.id != wireId } // Exclude the wire we just placed
                                            .filter { component ->
                                                val hitRadius = 80f
                                                (component.position - offset).getDistance() < hitRadius
                                            }
                                            .sortedBy { (it.position - offset).getDistance() }
                                        
                                        // Create connections with up to two nearest components
                                        if (nearbyComponents.isNotEmpty()) {
                                            nearbyComponents.take(2).forEach { nearComponent ->
                                                // Check if connection already exists
                                                val existingConnection = connections.find { 
                                                    (it.fromComponent == wireId && it.toComponent == nearComponent.id) ||
                                                    (it.fromComponent == nearComponent.id && it.toComponent == wireId)
                                                }
                                                
                                                if (existingConnection == null) {
                                                    // Create new connection
                                                    val newConnection = CircuitConnection(
                                                        id = "connection_${connections.size + 1}",
                                                        fromComponent = wireId,
                                                        toComponent = nearComponent.id,
                                                        isActive = true
                                                    )
                                                    connections = connections + newConnection
                                                    playSound(CircuitSoundType.CONNECT)
                                                }
                                            }
                                        }
                                        
                                        // Don't reset selection state so user can place multiple wires
                                        // selectedComponentType = null
                                        // isPlacingComponent = false
                                        
                                        // Simulate circuit after adding wire and connections
                                        components = simulateCircuit(components, connections)
                                    }
                                    else -> {
                                        // Place other components normally
                                        val componentId = "${selectedComponentType.toString().lowercase()}_${System.currentTimeMillis()}"
                                        val newComponent = CircuitComponent(
                                            id = componentId,
                                            type = selectedComponentType!!,
                                            position = offset,
                                            rotation = 0,
                                            isOn = selectedComponentType == CircuitComponentType.BATTERY
                                        )
                                        
                                        components = components + newComponent
                                        playSound(CircuitSoundType.PLACE)
                                        
                                        // Reset selection state for non-wire components
                                        selectedComponentType = null
                                        isPlacingComponent = false
                                        
                                        // Simulate circuit after adding component
                                        components = simulateCircuit(components, connections)
                                    }
                                }
                            } else {
                                // Handle component selection and switch toggling
                                val tappedComponent = components.firstOrNull { component ->
                                    val hitRadius = 60f
                                    (component.position - offset).getDistance() < hitRadius
                                }
                                
                                if (tappedComponent != null) {
                                    selectedComponent = tappedComponent
                                    
                                    if (tappedComponent.type == CircuitComponentType.SWITCH) {
                                        components = components.map {
                                            if (it == tappedComponent) {
                                                it.copy(isOn = !it.isOn)
                                            } else {
                                                it
                                            }
                                        }
                                        playSound(CircuitSoundType.CLICK)
                                        components = simulateCircuit(components, connections)
                                    }
                                } else {
                                    selectedComponent = null
                                }
                            }
                        }
                    }
            ) {
                // Draw connections first
                connections.forEach { connection ->
                    val fromComponent = components.find { it.id == connection.fromComponent }
                    val toComponent = components.find { it.id == connection.toComponent }
                    
                    if (fromComponent != null && toComponent != null) {
                        drawConnection(fromComponent, toComponent, connection.isActive)
                    }
                }
                
                // Draw components on top
                components.forEach { component ->
                    drawComponent(component)
                }
                
                // Draw placement preview when placing components
                if (isPlacingComponent && selectedComponentType != null) {
                    // Show a preview circle where the component will be placed
                    drawCircle(
                        color = Color(0xFF4CAF50).copy(alpha = 0.3f),
                        radius = 40f,
                        center = center
                    )
                    
                    // For wires, show connection radius
                    if (selectedComponentType == CircuitComponentType.WIRE) {
                        drawCircle(
                            color = Color(0xFF4CAF50).copy(alpha = 0.1f),
                            radius = 80f,
                            center = center
                        )
                    }
                }
            }
            
            // Add a label to make it clear this is the circuit board
            Text(
                text = "Circuit Board",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
                    .background(
                        color = Color(0xFF455A64).copy(alpha = 0.7f),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
            
            // Feedback message
            feedback?.let { fb ->
                Card(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (fb.isSuccess) Color(0xFF4CAF50) else Color(0xFFF44336)
                    )
                ) {
                    Text(
                        text = fb.message,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            // Add a hint button
            Button(
                onClick = { showHint = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6B4EFF)
                ),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = "Hint",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Hint", fontSize = 12.sp)
            }
        }
        
        // Components section - simplified to match the reference image
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFF37474F)
        ) {
            Column(
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(
                    text = "Components",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
                
                // Component buttons in a single row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    currentLevel.availableComponents.forEach { componentType ->
                        Button(
                            onClick = {
                                selectedComponentType = componentType
                                isPlacingComponent = true
                                showPlacementDialog = false
                            },
                            modifier = Modifier
                                .height(56.dp)
                                .width(100.dp)
                                .padding(4.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedComponentType == componentType) 
                                    Color(0xFF4CAF50) else Color(0xFF5C6BC0),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(28.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = when (componentType) {
                                        CircuitComponentType.WIRE -> Icons.Filled.LinearScale
                                        CircuitComponentType.BATTERY -> Icons.Default.BatteryFull
                                        CircuitComponentType.BULB -> Icons.Default.Lightbulb
                                        CircuitComponentType.SWITCH -> Icons.Default.ToggleOn
                                        CircuitComponentType.RESISTOR -> Icons.Default.Tune
                                        CircuitComponentType.CIRCUIT_BREAKER -> Icons.Default.ElectricBolt
                                    },
                                    contentDescription = componentType.name,
                                    modifier = Modifier.size(32.dp)
                                )
                                
                                Text(
                                    text = when (componentType) {
                                        CircuitComponentType.WIRE -> "Wire"
                                        CircuitComponentType.BATTERY -> "Battery"
                                        CircuitComponentType.BULB -> "Bulb"
                                        CircuitComponentType.SWITCH -> "Switch"
                                        CircuitComponentType.RESISTOR -> "Resistor"
                                        CircuitComponentType.CIRCUIT_BREAKER -> "Breaker"
                                    },
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
        
        // Dialog for component placement instructions
        if (showPlacementDialog) {
            AlertDialog(
                onDismissRequest = { 
                    showPlacementDialog = false
                    selectedComponentType = null
                },
                title = { 
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = when (selectedComponentType) {
                                CircuitComponentType.WIRE -> Icons.Filled.LinearScale
                                CircuitComponentType.SWITCH -> Icons.Default.ToggleOn
                                CircuitComponentType.RESISTOR -> Icons.Default.Tune
                                CircuitComponentType.CIRCUIT_BREAKER -> Icons.Default.ElectricBolt
                                else -> Icons.Default.Add
                            },
                            contentDescription = null,
                            tint = Color(0xFF5C6BC0),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Place ${selectedComponentType?.name?.lowercase()?.replaceFirstChar { it.uppercase() } ?: ""}"
                        )
                    }
                },
                text = { 
                    Column {
                        Text("Tap on the circuit board to place the component.")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "You need to connect components to create a working circuit.",
                            fontStyle = FontStyle.Italic
                        )
                        
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .padding(top = 16.dp)
                                .background(Color(0xFF263238), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.BatteryFull,
                                    contentDescription = null,
                                    tint = Color(0xFFFFC107),
                                    modifier = Modifier.size(32.dp)
                                )
                                
                                Icon(
                                    imageVector = when (selectedComponentType) {
                                        CircuitComponentType.WIRE -> Icons.Filled.LinearScale
                                        CircuitComponentType.SWITCH -> Icons.Default.ToggleOn
                                        CircuitComponentType.RESISTOR -> Icons.Default.Tune
                                        CircuitComponentType.CIRCUIT_BREAKER -> Icons.Default.ElectricBolt
                                        else -> Icons.Default.Add
                                    },
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                                
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = null,
                                    tint = Color(0xFFFFEB3B),
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { 
                            showPlacementDialog = false
                            selectedComponentType = null
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF5C6BC0)
                        )
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
        
        // How to Play dialog
        if (showHowToPlay) {
            AlertDialog(
                onDismissRequest = { showHowToPlay = false },
                title = { Text("How to Play Circuit Breaker") },
                text = { 
                    Column {
                        Text("Welcome to Circuit Breaker! Here's how to play:", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("1. Tap on components in the palette to add them to the board")
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("2. Tap between components to connect them")
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("3. Tap on a switch to turn it on or off")
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("4. Complete each level's goal to earn points!")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Need help? Tap the Hint button anytime!", fontWeight = FontWeight.Bold)
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showHowToPlay = false }
                    ) {
                        Text("Let's Play!")
                    }
                }
            )
        }
        
        // Hint dialog
        if (showHint) {
            AlertDialog(
                onDismissRequest = { showHint = false },
                title = { Text("Circuit Hint") },
                text = { 
                    Text(getHintForLevel(currentLevel.id))
                },
                confirmButton = {
                    Button(
                        onClick = { showHint = false }
                    ) {
                        Text("Got it!")
                    }
                }
            )
        }
        
        // Add a more detailed wire tutorial dialog
        if (showWireTutorial) {
            AlertDialog(
                onDismissRequest = { showWireTutorial = false },
                title = { 
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LinearScale,
                            contentDescription = null,
                            tint = Color(0xFF5C6BC0)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("How to Connect Components")
                    }
                },
                text = { 
                    Column {
                        Text("To create a working circuit, you need to connect components:", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text("1. Tap between two components to create a connection")
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Text("2. When components are properly connected, they will glow green")
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Text("3. To complete the circuit, connect the battery to the bulb using wires")
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Text("4. Tap on a switch to turn it on or off")
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Add a visual example
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .background(Color(0xFF263238), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Battery
                                Icon(
                                    imageVector = Icons.Default.BatteryFull,
                                    contentDescription = null,
                                    tint = Color(0xFFFFC107),
                                    modifier = Modifier.size(40.dp)
                                )
                                
                                // Wire
                                Icon(
                                    imageVector = Icons.Default.LinearScale,
                                    contentDescription = null,
                                    tint = Color(0xFF4CAF50),
                                    modifier = Modifier.size(40.dp)
                                )
                                
                                // Bulb
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = null,
                                    tint = Color(0xFFFFEB3B),
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                            
                            // Connection lines
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawLine(
                                    color = Color(0xFF4CAF50),
                                    start = Offset(size.width * 0.25f, size.height * 0.5f),
                                    end = Offset(size.width * 0.5f, size.height * 0.5f),
                                    strokeWidth = 4f
                                )
                                drawLine(
                                    color = Color(0xFF4CAF50),
                                    start = Offset(size.width * 0.5f, size.height * 0.5f),
                                    end = Offset(size.width * 0.75f, size.height * 0.5f),
                                    strokeWidth = 4f
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Try it now! Tap between the battery and wire, then between the wire and bulb.", fontWeight = FontWeight.Bold)
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showWireTutorial = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50)
                        )
                    ) {
                        Text("Let's Try It!")
                    }
                }
            )
        }
    }
}

@Composable
private fun ComponentPaletteCard(
    componentType: CircuitComponentType,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .size(80.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE0E0E0)
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = when (componentType) {
                        CircuitComponentType.WIRE -> Icons.Filled.LinearScale
                        CircuitComponentType.BATTERY -> Icons.Default.BatteryFull
                        CircuitComponentType.BULB -> Icons.Default.Lightbulb
                        CircuitComponentType.SWITCH -> Icons.Default.ToggleOn
                        CircuitComponentType.RESISTOR -> Icons.Default.Tune
                        CircuitComponentType.CIRCUIT_BREAKER -> Icons.Default.ElectricBolt
                    },
                    contentDescription = componentType.name,
                    modifier = Modifier.size(40.dp),
                    tint = Color.Black
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = when (componentType) {
                        CircuitComponentType.WIRE -> "Wire"
                        CircuitComponentType.BATTERY -> "Battery"
                        CircuitComponentType.BULB -> "Bulb"
                        CircuitComponentType.SWITCH -> "Switch"
                        CircuitComponentType.RESISTOR -> "Resistor"
                        CircuitComponentType.CIRCUIT_BREAKER -> "Breaker"
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
private fun ComponentButton(
    componentType: CircuitComponentType,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .height(56.dp)
            .width(100.dp)
            .padding(4.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF5C6BC0),
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(28.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = when (componentType) {
                    CircuitComponentType.WIRE -> Icons.Filled.LinearScale
                    CircuitComponentType.BATTERY -> Icons.Default.BatteryFull
                    CircuitComponentType.BULB -> Icons.Default.Lightbulb
                    CircuitComponentType.SWITCH -> Icons.Default.ToggleOn
                    CircuitComponentType.RESISTOR -> Icons.Default.Tune
                    CircuitComponentType.CIRCUIT_BREAKER -> Icons.Default.ElectricBolt
                },
                contentDescription = componentType.name,
                modifier = Modifier.size(32.dp)
            )
            
            Text(
                text = when (componentType) {
                    CircuitComponentType.WIRE -> "Wire"
                    CircuitComponentType.BATTERY -> "Battery"
                    CircuitComponentType.BULB -> "Bulb"
                    CircuitComponentType.SWITCH -> "Switch"
                    CircuitComponentType.RESISTOR -> "Resistor"
                    CircuitComponentType.CIRCUIT_BREAKER -> "Breaker"
                },
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}

// Helper functions for circuit simulation
private fun DrawScope.drawCircuitGrid(size: Size) {
    val gridSize = 60f
    val strokeWidth = 2f  // Increased from 1.5f
    
    // Draw horizontal grid lines
    for (y in 0..(size.height / gridSize).toInt()) {
        drawLine(
            color = Color(0xFF546E7A).copy(alpha = 0.6f),  // Increased opacity
            start = Offset(0f, y * gridSize),
            end = Offset(size.width, y * gridSize),
            strokeWidth = strokeWidth
        )
    }
    
    // Draw vertical grid lines
    for (x in 0..(size.width / gridSize).toInt()) {
        drawLine(
            color = Color(0xFF546E7A).copy(alpha = 0.6f),  // Increased opacity
            start = Offset(x * gridSize, 0f),
            end = Offset(x * gridSize, size.height),
            strokeWidth = strokeWidth
        )
    }
    
    // Draw grid intersection points for better visibility
    for (x in 0..(size.width / gridSize).toInt()) {
        for (y in 0..(size.height / gridSize).toInt()) {
            drawCircle(
                color = Color(0xFF78909C).copy(alpha = 0.5f),
                radius = 2f,
                center = Offset(x * gridSize, y * gridSize)
            )
        }
    }
    
    // Draw a subtle circuit pattern in the background
    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 12f), 0f)
    
    // Horizontal circuit lines
    drawLine(
        color = Color(0xFF4FC3F7).copy(alpha = 0.2f),
        start = Offset(0f, size.height * 0.25f),
        end = Offset(size.width, size.height * 0.25f),
        strokeWidth = 3f,
        pathEffect = pathEffect
    )
    
    drawLine(
        color = Color(0xFF4FC3F7).copy(alpha = 0.2f),
        start = Offset(0f, size.height * 0.75f),
        end = Offset(size.width, size.height * 0.75f),
        strokeWidth = 3f,
        pathEffect = pathEffect
    )
    
    // Vertical circuit lines
    drawLine(
        color = Color(0xFF4FC3F7).copy(alpha = 0.2f),
        start = Offset(size.width * 0.33f, 0f),
        end = Offset(size.width * 0.33f, size.height),
        strokeWidth = 3f,
        pathEffect = pathEffect
    )
    
    drawLine(
        color = Color(0xFF4FC3F7).copy(alpha = 0.2f),
        start = Offset(size.width * 0.67f, 0f),
        end = Offset(size.width * 0.67f, size.height),
        strokeWidth = 3f,
        pathEffect = pathEffect
    )
}

private fun DrawScope.drawComponent(component: CircuitComponent) {
    withTransform({
        translate(left = component.position.x, top = component.position.y)
        rotate(degrees = component.rotation.toFloat())
    }) {
        val size = when (component.type) {
            CircuitComponentType.BATTERY, CircuitComponentType.BULB -> 150f
            else -> 100f
        }
        
        if (component.isConnected) {
            drawCircle(
                color = Color(0xFF4CAF50).copy(alpha = 0.2f),
                radius = size * 0.8f
            )
        }
        
        when (component.type) {
            CircuitComponentType.WIRE -> {
                drawLine(
                    color = if (component.isConnected) Color(0xFF4CAF50) else Color(0xFFBDBDBD),
                    start = Offset(-size/2f, 0f),
                    end = Offset(size/2f, 0f),
                    strokeWidth = 10f
                )
                
                drawCircle(
                    color = if (component.isConnected) Color(0xFF4CAF50) else Color(0xFFBDBDBD),
                    radius = 12f,
                    center = Offset(-size/2f, 0f)
                )
                
                drawCircle(
                    color = if (component.isConnected) Color(0xFF4CAF50) else Color(0xFFBDBDBD),
                    radius = 12f,
                    center = Offset(size/2f, 0f)
                )
                
                if (component.isConnected) {
                    val arrowSize = 20f
                    drawLine(
                        color = Color(0xFF4CAF50),
                        start = Offset(0f, -arrowSize),
                        end = Offset(arrowSize, 0f),
                        strokeWidth = 4f
                    )
                    drawLine(
                        color = Color(0xFF4CAF50),
                        start = Offset(arrowSize, 0f),
                        end = Offset(0f, arrowSize),
                        strokeWidth = 4f
                    )
                }
            }
            CircuitComponentType.BATTERY -> {
                // Draw background glow for better visibility
                drawCircle(
                    color = Color(0xFFFFC107).copy(alpha = 0.3f),
                    radius = size/1.5f
                )
                
                // Draw battery with improved visuals
                drawRect(
                    color = Color(0xFFFFC107),
                    topLeft = Offset(-size/3f, -size/6f),
                    size = Size(2*size/3f, size/3f)
                )
                
                // Draw positive terminal
                drawLine(
                    color = Color.Black,
                    start = Offset(size/6f, -size/6f - 15f),
                    end = Offset(size/6f, -size/6f + 15f),
                    strokeWidth = 6f
                )
                drawLine(
                    color = Color.Black,
                    start = Offset(size/6f - 15f, -size/6f),
                    end = Offset(size/6f + 15f, -size/6f),
                    strokeWidth = 6f
                )
                
                // Draw negative terminal
                drawLine(
                    color = Color.Black,
                    start = Offset(-size/6f - 15f, -size/6f),
                    end = Offset(-size/6f + 15f, -size/6f),
                    strokeWidth = 6f
                )
                
                // Draw connections with thicker lines
                drawLine(
                    color = if (component.isConnected) Color(0xFF4CAF50) else Color(0xFFBDBDBD),
                    start = Offset(-size/2f, 0f),
                    end = Offset(-size/3f, 0f),
                    strokeWidth = 10f
                )
                drawLine(
                    color = if (component.isConnected) Color(0xFF4CAF50) else Color(0xFFBDBDBD),
                    start = Offset(size/3f, 0f),
                    end = Offset(size/2f, 0f),
                    strokeWidth = 10f
                )
                
                // Add "BATTERY" label
                val paint1 = android.graphics.Paint()
                paint1.color = android.graphics.Color.WHITE
                paint1.textSize = 24f
                paint1.textAlign = android.graphics.Paint.Align.CENTER
                paint1.isFakeBoldText = true
                
                drawContext.canvas.nativeCanvas.drawText(
                    "BATTERY",
                    0f, size/2f + 20f,
                    paint1
                )
            }
            CircuitComponentType.BULB -> {
                // Draw background glow when on
                if (component.isOn) {
                    // Animated glow effect
                    drawCircle(
                        color = Color(0xFFFFEB3B).copy(alpha = 0.7f),
                        radius = size/1.2f
                    )
                    
                    // Rays of light
                    val rayLength = size/1.5f
                    for (angle in 0 until 360 step 45) {
                        val radians = angle * Math.PI / 180
                        val startX = 0f
                        val startY = 0f
                        val endX = (rayLength * cos(radians)).toFloat()
                        val endY = (rayLength * sin(radians)).toFloat()
                        
                        drawLine(
                            color = Color(0xFFFFEB3B).copy(alpha = 0.5f),
                            start = Offset(startX, startY),
                            end = Offset(endX, endY),
                            strokeWidth = 3f
                        )
                    }
                }
                
                // Draw bulb with improved visuals
                drawCircle(
                    color = if (component.isOn) Color(0xFFFFEB3B) else Color(0xFFE0E0E0),
                    radius = size/3f
                )
                
                // Draw filament
                drawLine(
                    color = if (component.isOn) Color(0xFFFFEB3B) else Color(0xFF9E9E9E),
                    start = Offset(-size/6f, 0f),
                    end = Offset(size/6f, 0f),
                    strokeWidth = 4f
                )
                
                // Draw base
                drawRect(
                    color = Color(0xFF9E9E9E),
                    topLeft = Offset(-size/6f, size/3f - size/12f),
                    size = Size(size/3f, size/6f)
                )
                
                // Draw connections with thicker lines
                drawLine(
                    color = if (component.isConnected) Color(0xFF4CAF50) else Color(0xFFBDBDBD),
                    start = Offset(-size/2f, 0f),
                    end = Offset(-size/3f, 0f),
                    strokeWidth = 10f
                )
                drawLine(
                    color = if (component.isConnected) Color(0xFF4CAF50) else Color(0xFFBDBDBD),
                    start = Offset(size/3f, 0f),
                    end = Offset(size/2f, 0f),
                    strokeWidth = 10f
                )
                
                // Add "BULB" label
                val paint2 = android.graphics.Paint()
                paint2.color = android.graphics.Color.WHITE
                paint2.textSize = 24f
                paint2.textAlign = android.graphics.Paint.Align.CENTER
                paint2.isFakeBoldText = true
                
                drawContext.canvas.nativeCanvas.drawText(
                    "BULB",
                    0f, size/2f + 20f,
                    paint2
                )
            }
            CircuitComponentType.SWITCH -> {
                // Draw switch with improved visuals
                drawRect(
                    color = Color(0xFF2196F3),
                    topLeft = Offset(-size/3f, -size/6f),
                    size = Size(2*size/3f, size/3f)
                )
                
                // Draw switch lever
                val leverEndX = if (component.isOn) size/6f else -size/6f
                drawLine(
                    color = Color.White,
                    start = Offset(-size/6f, 0f),
                    end = Offset(leverEndX, -size/4f),
                    strokeWidth = 6f
                )
                
                // Draw ON/OFF text
                val textColor = Color.White
                val textSize = size/4f
                if (component.isOn) {
                    // Draw "ON" text
                    val paint3 = android.graphics.Paint()
                    paint3.color = android.graphics.Color.WHITE
                    paint3.textSize = 24f
                    paint3.textAlign = android.graphics.Paint.Align.CENTER
                    paint3.isFakeBoldText = true
                    
                    drawContext.canvas.nativeCanvas.drawText(
                        "ON",
                        0f, 0f,
                        paint3
                    )
                } else {
                    // Draw "OFF" text
                    val paint4 = android.graphics.Paint()
                    paint4.color = android.graphics.Color.WHITE
                    paint4.textSize = 24f
                    paint4.textAlign = android.graphics.Paint.Align.CENTER
                    paint4.isFakeBoldText = true
                    
                    drawContext.canvas.nativeCanvas.drawText(
                        "OFF",
                        0f, 0f,
                        paint4
                    )
                }
                
                // Draw connections with thicker lines
                drawLine(
                    color = if (component.isConnected) Color(0xFF4CAF50) else Color(0xFFBDBDBD),
                    start = Offset(-size/2f, 0f),
                    end = Offset(-size/3f, 0f),
                    strokeWidth = 10f
                )
                drawLine(
                    color = if (component.isConnected) Color(0xFF4CAF50) else Color(0xFFBDBDBD),
                    start = Offset(size/3f, 0f),
                    end = Offset(size/2f, 0f),
                    strokeWidth = 10f
                )
                
                // Add "SWITCH" label
                val paint5 = android.graphics.Paint()
                paint5.color = android.graphics.Color.WHITE
                paint5.textSize = 24f
                paint5.textAlign = android.graphics.Paint.Align.CENTER
                paint5.isFakeBoldText = true
                
                drawContext.canvas.nativeCanvas.drawText(
                    "SWITCH",
                    0f, size/2f + 20f,
                    paint5
                )
            }
            CircuitComponentType.RESISTOR -> {
                // Draw resistor with improved visibility
                // Draw background for better visibility
                drawCircle(
                    color = Color(0xFFF44336).copy(alpha = 0.2f),
                    radius = size/2f
                )
                
                // Draw resistor body
                drawRect(
                    color = Color(0xFFF44336),
                    topLeft = Offset(-size/3f, -size/6f),
                    size = Size(2*size/3f, size/3f),
                    style = Stroke(width = 6f)
                )
                
                // Draw zigzag pattern inside
                val zigzagWidth = size/3f
                val zigzagHeight = size/8f
                val zigzagSteps = 4
                val stepWidth = zigzagWidth / zigzagSteps
                
                var lastX = -zigzagWidth/2f
                var lastY = 0f
                
                for (i in 1..zigzagSteps) {
                    val nextX = -zigzagWidth/2f + i * stepWidth
                    val nextY = if (i % 2 == 1) zigzagHeight else -zigzagHeight
                    
                    drawLine(
                        color = Color(0xFFF44336),
                        start = Offset(lastX, lastY),
                        end = Offset(nextX, nextY),
                        strokeWidth = 4f
                    )
                    
                    lastX = nextX
                    lastY = nextY
                }
                
                // Draw connections with thicker lines
                drawLine(
                    color = if (component.isConnected) Color(0xFF4CAF50) else Color(0xFFBDBDBD),
                    start = Offset(-size/2f, 0f),
                    end = Offset(-size/3f, 0f),
                    strokeWidth = 10f
                )
                drawLine(
                    color = if (component.isConnected) Color(0xFF4CAF50) else Color(0xFFBDBDBD),
                    start = Offset(size/3f, 0f),
                    end = Offset(size/2f, 0f),
                    strokeWidth = 10f
                )
                
                // Add "RESISTOR" label
                val paint6 = android.graphics.Paint()
                paint6.color = android.graphics.Color.WHITE
                paint6.textSize = 24f
                paint6.textAlign = android.graphics.Paint.Align.CENTER
                paint6.isFakeBoldText = true
                
                drawContext.canvas.nativeCanvas.drawText(
                    "RESISTOR",
                    0f, size/2f + 20f,
                    paint6
                )
            }
            CircuitComponentType.CIRCUIT_BREAKER -> {
                // Draw circuit breaker with improved visuals
                // Draw background for better visibility
                drawCircle(
                    color = if (component.isTripped) Color(0xFFF44336).copy(alpha = 0.3f) else Color(0xFF4CAF50).copy(alpha = 0.3f),
                    radius = size/2f
                )
                
                val breakerColor = if (component.isTripped) Color(0xFFF44336) else Color(0xFF4CAF50)
                
                // Draw main body with rounded corners
                drawRoundRect(
                    color = breakerColor,
                    topLeft = Offset(-size/3f, -size/6f),
                    size = Size(2*size/3f, size/3f),
                    cornerRadius = CornerRadius(size/12f, size/12f)
                )
                
                // Draw border for better definition
                drawRoundRect(
                    color = Color.White,
                    topLeft = Offset(-size/3f, -size/6f),
                    size = Size(2*size/3f, size/3f),
                    cornerRadius = CornerRadius(size/12f, size/12f),
                    style = Stroke(width = 4f)
                )
                
                // Draw connections with thicker lines
                drawLine(
                    color = if (component.isConnected && !component.isTripped) Color(0xFF4CAF50) else Color(0xFFBDBDBD),
                    start = Offset(-size/2f, 0f),
                    end = Offset(-size/3f, 0f),
                    strokeWidth = 10f
                )
                drawLine(
                    color = if (component.isConnected && !component.isTripped) Color(0xFF4CAF50) else Color(0xFFBDBDBD),
                    start = Offset(size/3f, 0f),
                    end = Offset(size/2f, 0f),
                    strokeWidth = 10f
                )
                
                // Draw reset button with improved visibility
                if (component.isTripped) {
                    drawCircle(
                        color = Color.White,
                        radius = size/8f,
                        center = Offset(0f, 0f)
                    )
                    drawLine(
                        color = Color.Red,
                        start = Offset(-size/12f, 0f),
                        end = Offset(size/12f, 0f),
                        strokeWidth = 6f
                    )
                    drawLine(
                        color = Color.Red,
                        start = Offset(0f, -size/12f),
                        end = Offset(0f, size/12f),
                        strokeWidth = 6f
                    )
                    
                    // Draw "TRIPPED" text
                    val paint7 = android.graphics.Paint()
                    paint7.color = android.graphics.Color.WHITE
                    paint7.textSize = 20f
                    paint7.textAlign = android.graphics.Paint.Align.CENTER
                    paint7.isFakeBoldText = true
                    
                    drawContext.canvas.nativeCanvas.drawText(
                        "TRIPPED",
                        0f, 0f,
                        paint7
                    )
                } else {
                    // Draw status indicator when not tripped
                    drawCircle(
                        color = Color(0xFF4CAF50),
                        radius = size/8f,
                        center = Offset(0f, 0f)
                    )
                    
                    // Draw "OK" text
                    val paint8 = android.graphics.Paint()
                    paint8.color = android.graphics.Color.WHITE
                    paint8.textSize = 20f
                    paint8.textAlign = android.graphics.Paint.Align.CENTER
                    paint8.isFakeBoldText = true
                    
                    drawContext.canvas.nativeCanvas.drawText(
                        "OK",
                        0f, 0f,
                        paint8
                    )
                }
                
                // Add "BREAKER" label
                val paint9 = android.graphics.Paint()
                paint9.color = android.graphics.Color.WHITE
                paint9.textSize = 24f
                paint9.textAlign = android.graphics.Paint.Align.CENTER
                paint9.isFakeBoldText = true
                
                drawContext.canvas.nativeCanvas.drawText(
                    "BREAKER",
                    0f, size/2f + 20f,
                    paint9
                )
            }
        }
    }
}

private fun DrawScope.drawConnection(fromComponent: CircuitComponent, toComponent: CircuitComponent, isActive: Boolean) {
    // Draw thicker connection line with glow effect for active connections
    if (isActive) {
        // Draw glow effect for active connections
        drawLine(
            color = Color(0xFF4CAF50).copy(alpha = 0.3f),
            start = fromComponent.position,
            end = toComponent.position,
            strokeWidth = 12f
        )
    }
    
    // Draw main connection line
    drawLine(
        color = if (isActive) Color(0xFF4CAF50) else Color(0xFFBDBDBD),
        start = fromComponent.position,
        end = toComponent.position,
        strokeWidth = 6f  // Increased thickness
    )
    
    // Draw connection points at both ends
    drawCircle(
        color = if (isActive) Color(0xFF4CAF50) else Color(0xFFBDBDBD),
        radius = 10f,
        center = fromComponent.position
    )
    drawCircle(
        color = if (isActive) Color(0xFF4CAF50) else Color(0xFFBDBDBD),
        radius = 10f,
        center = toComponent.position
    )
    
    // Draw animated dots along the connection if active
    if (isActive) {
        val distance = (toComponent.position - fromComponent.position).getDistance()
        val direction = (toComponent.position - fromComponent.position) / distance
        
        // Draw 3 dots along the connection
        for (i in 1..3) {
            val position = fromComponent.position + direction * distance * (i / 4f)
            drawCircle(
                color = Color(0xFF4CAF50),
                radius = 4f,
                center = position
            )
        }
    }
}

private fun snapToGrid(position: Offset): Offset {
    val gridSize = 60f // Match the grid size used in drawCircuitGrid
    return Offset(
        (position.x / gridSize).roundToInt() * gridSize,
        (position.y / gridSize).roundToInt() * gridSize
    )
}

private fun simulateCircuit(
    components: List<CircuitComponent>,
    connections: List<CircuitConnection>
): List<CircuitComponent> {
    val updatedComponents = components.map { it.copy() }
    
    // Reset all component states
    updatedComponents.forEach { component ->
        component.isOn = false
        component.isConnected = false
        component.currentFlow = 0f
    }
    
    // Find all batteries
    val batteries = updatedComponents.filter { it.type == CircuitComponentType.BATTERY }
    
    // For each battery, trace the circuit
    batteries.forEach { battery ->
        battery.isOn = true
        battery.isConnected = true
        
        // Create a map of component connections
        val connectionMap = mutableMapOf<String, MutableList<String>>()
        connections.forEach { connection ->
            connectionMap.getOrPut(connection.fromComponent) { mutableListOf() }.add(connection.toComponent)
            connectionMap.getOrPut(connection.toComponent) { mutableListOf() }.add(connection.fromComponent)
        }
        
        // Trace the circuit using BFS
        val visited = mutableSetOf<String>()
        val queue = ArrayDeque<String>()
        queue.add(battery.id)
        
        while (queue.isNotEmpty()) {
            val currentId = queue.removeFirst()
            if (currentId in visited) continue
            visited.add(currentId)
            
            val currentComponent = updatedComponents.find { it.id == currentId } ?: continue
            currentComponent.isConnected = true
            
            // If it's a switch that's off, don't continue the circuit through this path
            if (currentComponent.type == CircuitComponentType.SWITCH && !currentComponent.isOn) {
                continue
            }
            
            // If it's a bulb, turn it on
            if (currentComponent.type == CircuitComponentType.BULB) {
                currentComponent.isOn = true
            }
            
            // Add connected components to the queue
            connectionMap[currentId]?.forEach { connectedId ->
                if (connectedId !in visited) {
                    queue.add(connectedId)
                }
            }
        }
    }
    
    return updatedComponents
}

private fun createConnection(
    components: List<CircuitComponent>,
    connections: List<CircuitConnection>,
    tapPosition: Offset
): Pair<List<CircuitComponent>, List<CircuitConnection>> {
    // Create mutable copies that we can modify
    val updatedComponents = components.toMutableList()
    val updatedConnectionsList = mutableListOf<CircuitConnection>()
    updatedConnectionsList.addAll(connections)
    
    // Find components near the tap position
    val nearbyComponents = components.filter { component ->
        (component.position - tapPosition).getDistance() < 120f // Increased detection radius
    }
    
    if (nearbyComponents.size >= 2) {
        // Find the two closest components
        val sortedByDistance = nearbyComponents.sortedBy { 
            (it.position - tapPosition).getDistance() 
        }
        
        val comp1 = sortedByDistance[0]
        val comp2 = sortedByDistance[1]
        
        // Check if connection already exists
        val existingConnection = connections.find { 
            (it.fromComponent == comp1.id && it.toComponent == comp2.id) ||
            (it.fromComponent == comp2.id && it.toComponent == comp1.id)
        }
        
        if (existingConnection == null) {
            // Create new connection
            val newConnection = CircuitConnection(
                id = "connection_${connections.size + 1}",
                fromComponent = comp1.id,
                toComponent = comp2.id,
                isActive = true
            )
            // Add the new connection to our mutable list
            updatedConnectionsList.add(newConnection)
            
            // Play connection sound
            playSound(CircuitSoundType.CONNECT)
            
            // Log the connection for debugging
            Log.d("CircuitBreaker", "Created connection from ${comp1.type} to ${comp2.type}")
        } else {
            // Remove the existing connection
            updatedConnectionsList.remove(existingConnection)
            
            // Play disconnection sound
            playSound(CircuitSoundType.DISCONNECT)
        }
        
        // Simulate circuit with the updated connections
        return Pair(simulateCircuit(components, updatedConnectionsList), updatedConnectionsList)
    }
    
    // If no connection was made, return the original components and connections
    return Pair(components, connections)
}

private fun playSound(soundType: CircuitSoundType) {
    // Log the sound being played instead of actually playing it
    Log.d("CircuitBreaker", "Playing sound: $soundType")
}

enum class CircuitSoundType {
    CONNECT,
    DISCONNECT,
    CLICK,
    PLACE
}

private fun getHintForLevel(levelId: Int): String {
    return when (levelId) {
        1 -> "To complete this level:\n\n1. Tap between the battery and wire to connect them\n2. Tap between the wire and bulb to connect them\n\nWhen the circuit is complete, the bulb will light up!"
        2 -> "For this level:\n\n1. Add a switch by tapping the Switch button\n2. Place the switch between the battery and bulb\n3. Connect all components with wires\n4. Tap the switch to turn it on and complete the circuit"
        3 -> "For this level:\n\n1. Add a circuit breaker to protect the circuit\n2. Place the circuit breaker in series with the bulb\n3. Add a resistor to limit the current flow\n4. Connect all components properly\n\nIf too much current flows, the circuit breaker will trip!"
        else -> "Experiment with different component arrangements to complete the circuit. Try adding resistors to limit current flow and switches to control when the circuit is active."
    }
}

// Add a function to check if the level is complete
private fun isLevelComplete(components: List<CircuitComponent>, levelId: Int): Boolean {
    // Check if all bulbs are on
    val allBulbsOn = components.filter { it.type == CircuitComponentType.BULB }
        .all { it.isOn }
    
    // Additional level-specific checks
    val additionalChecks = when (levelId) {
        1 -> true // Just need the bulb to be on
        2 -> components.any { it.type == CircuitComponentType.SWITCH && it.isOn } // Need a switch that's on
        3 -> components.any { it.type == CircuitComponentType.CIRCUIT_BREAKER } && 
             components.any { it.type == CircuitComponentType.RESISTOR } // Need both circuit breaker and resistor
        else -> true
    }
    
    return allBulbsOn && additionalChecks
} 