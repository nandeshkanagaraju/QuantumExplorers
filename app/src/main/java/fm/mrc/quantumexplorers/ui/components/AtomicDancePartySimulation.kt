package fm.mrc.quantumexplorers.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.*
import kotlin.random.Random
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate

// Data classes for our simulation
data class Atom(
    val id: Int,
    val element: String,
    var position: Offset,
    var velocity: Offset,
    var mass: Float,
    var charge: Float,
    var radius: Float,
    var color: Color,
    var isUserControlled: Boolean = false
)

data class Molecule(
    val id: Int,
    val atoms: List<Atom>,
    var bonds: List<Bond>,
    var position: Offset,
    var velocity: Offset,
    var energy: Float,
    var lifespan: Float = 100f
)

data class Bond(
    val atom1: Int,
    val atom2: Int,
    var strength: Float,
    var type: BondType
)

enum class BondType {
    SINGLE, DOUBLE, TRIPLE, IONIC
}

data class CollisionEffect(
    val position: Offset,
    var radius: Float,
    var alpha: Float,
    var color: Color
)

@Composable
fun AtomicDancePartySimulation(
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val textMeasurer = rememberTextMeasurer()
    
    // State for simulation
    var atoms by remember { mutableStateOf(createInitialAtoms()) }
    var molecules by remember { mutableStateOf(listOf<Molecule>()) }
    var collisionEffects by remember { mutableStateOf(listOf<CollisionEffect>()) }
    var isSimulationRunning by remember { mutableStateOf(true) }
    var temperature by remember { mutableStateOf(0.5f) }
    var energyLevel by remember { mutableStateOf(0f) }
    var beatSync by remember { mutableStateOf(0f) }
    var selectedAtomType by remember { mutableStateOf("H") }
    var draggedAtomId by remember { mutableStateOf<Int?>(null) }
    var moleculeFormationMessage by remember { mutableStateOf<String?>(null) }
    
    // Sound effects state
    var lastCollisionTime by remember { mutableStateOf(0L) }
    
    // Physics simulation loop
    LaunchedEffect(isSimulationRunning, temperature) {
        while (isSimulationRunning) {
            // Update physics
            atoms = updateAtomPhysics(atoms, temperature)
            molecules = updateMoleculePhysics(molecules)
            
            // Check for collisions and molecule formation
            val collisionResults = checkCollisions(atoms, molecules)
            atoms = collisionResults.first
            val newCollisions = collisionResults.second
            val newMolecule = collisionResults.third
            
            // Add new molecule if formed
            if (newMolecule != null) {
                molecules = molecules + newMolecule
                // Remove atoms that are now part of the molecule
                atoms = atoms.filter { atom -> 
                    newMolecule.atoms.none { it.id == atom.id } 
                }
                
                // Show molecule formation message
                moleculeFormationMessage = getMoleculeFormula(newMolecule)
                delay(2000) // Show message for 2 seconds
                moleculeFormationMessage = null
            }
            
            // Create new collision effects
            if (newCollisions.isNotEmpty()) {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastCollisionTime > 300) { // Limit sound frequency
                    lastCollisionTime = currentTime
                    // In a real implementation, we would play a sound here
                    // playCollisionSound(newCollisions)
                }
                
                collisionEffects = collisionEffects + newCollisions.map { collision ->
                    CollisionEffect(
                        position = collision,
                        radius = 10f,
                        alpha = 1f,
                        color = Color(
                            Random.nextFloat(),
                            Random.nextFloat(),
                            Random.nextFloat(),
                            1f
                        )
                    )
                }
            }
            
            // Update collision effects
            collisionEffects = collisionEffects.mapNotNull { effect ->
                val newEffect = effect.copy(
                    radius = effect.radius + 2f,
                    alpha = effect.alpha - 0.05f
                )
                if (newEffect.alpha > 0) newEffect else null
            }
            
            // Update energy level based on atom velocities
            energyLevel = calculateSystemEnergy(atoms, molecules)
            
            // Update beat sync (for visual and audio effects)
            beatSync = (beatSync + 0.05f) % 1f
            
            delay(16) // ~60 FPS
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        // 3D Rendering Canvas
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color(0xFF121212))
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                // Find if user clicked on an atom
                                draggedAtomId = atoms.find { atom ->
                                    val distance = (atom.position - offset).getDistance()
                                    distance < atom.radius
                                }?.id
                                
                                // If not clicking on an atom, create a new one
                                if (draggedAtomId == null) {
                                    val newAtom = createAtom(
                                        id = atoms.size + molecules.flatMap { it.atoms }.size,
                                        element = selectedAtomType,
                                        position = offset,
                                        isUserControlled = true
                                    )
                                    atoms = atoms + newAtom
                                    draggedAtomId = newAtom.id
                                }
                            },
                            onDrag = { change, _ ->
                                // Move the dragged atom
                                atoms = atoms.map { atom ->
                                    if (atom.id == draggedAtomId) {
                                        atom.copy(
                                            position = change.position,
                                            isUserControlled = true
                                        )
                                    } else {
                                        atom
                                    }
                                }
                            },
                            onDragEnd = {
                                // Release the atom
                                atoms = atoms.map { atom ->
                                    if (atom.id == draggedAtomId) {
                                        atom.copy(isUserControlled = false)
                                    } else {
                                        atom
                                    }
                                }
                                draggedAtomId = null
                            },
                            onDragCancel = {
                                draggedAtomId = null
                            }
                        )
                    }
            ) {
                // Draw background effects
                drawBackgroundEffects(beatSync)
                
                // Draw molecules
                molecules.forEach { molecule ->
                    drawMolecule(molecule, atoms)
                }
                
                // Draw atoms
                atoms.forEach { atom ->
                    drawAtom(atom, beatSync)
                }
                
                // Draw collision effects
                collisionEffects.forEach { effect ->
                    drawCollisionEffect(effect)
                }
                
                // Draw energy indicator
                drawEnergyIndicator(energyLevel)
            }
            
            // Overlay information
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
            ) {
                Text(
                    "Atoms: ${atoms.size}",
                    color = Color.White,
                    fontSize = 14.sp
                )
                Text(
                    "Molecules: ${molecules.size}",
                    color = Color.White,
                    fontSize = 14.sp
                )
                Text(
                    "Energy: ${energyLevel.toInt()} eV",
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
            
            // Molecule formation message
            moleculeFormationMessage?.let { message ->
                Card(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF6B4EFF)
                    )
                ) {
                    Text(
                        text = "Molecule Formed: $message",
                        modifier = Modifier.padding(16.dp),
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Controls and Information Panel
        ControlPanel(
            temperature = temperature,
            onTemperatureChange = { temperature = it },
            selectedAtomType = selectedAtomType,
            onAtomTypeSelected = { selectedAtomType = it },
            isSimulationRunning = isSimulationRunning,
            onPlayPauseToggle = { isSimulationRunning = !isSimulationRunning },
            onReset = {
                atoms = createInitialAtoms()
                molecules = listOf()
                collisionEffects = listOf()
            }
        )
    }
}

@Composable
private fun ControlPanel(
    temperature: Float,
    onTemperatureChange: (Float) -> Unit,
    selectedAtomType: String,
    onAtomTypeSelected: (String) -> Unit,
    isSimulationRunning: Boolean,
    onPlayPauseToggle: () -> Unit,
    onReset: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1E1E1E))
            .padding(16.dp)
    ) {
        // Temperature control
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Temperature:",
                color = Color.White,
                modifier = Modifier.width(100.dp)
            )
            Slider(
                value = temperature,
                onValueChange = onTemperatureChange,
                modifier = Modifier.weight(1f),
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFF6B4EFF),
                    activeTrackColor = Color(0xFF6B4EFF)
                )
            )
            Text(
                "${(temperature * 100).toInt()}°",
                color = Color.White,
                modifier = Modifier.width(40.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Atom type selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            AtomButton(
                element = "H",
                color = Color(0xFF1E88E5),
                isSelected = selectedAtomType == "H",
                onClick = { onAtomTypeSelected("H") }
            )
            AtomButton(
                element = "O",
                color = Color(0xFFE53935),
                isSelected = selectedAtomType == "O",
                onClick = { onAtomTypeSelected("O") }
            )
            AtomButton(
                element = "C",
                color = Color(0xFF424242),
                isSelected = selectedAtomType == "C",
                onClick = { onAtomTypeSelected("C") }
            )
            AtomButton(
                element = "N",
                color = Color(0xFF7B1FA2),
                isSelected = selectedAtomType == "N",
                onClick = { onAtomTypeSelected("N") }
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Control buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = onPlayPauseToggle,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6B4EFF)
                )
            ) {
                Text(if (isSimulationRunning) "Pause" else "Play")
            }
            
            Button(
                onClick = onReset,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6B4EFF)
                )
            ) {
                Text("Reset")
            }
        }
    }
}

@Composable
private fun AtomButton(
    element: String,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) color else Color.DarkGray
        ),
        modifier = Modifier.size(48.dp)
    ) {
        Text(
            text = element,
            color = Color.White
        )
    }
}

private fun DrawScope.drawAtom(atom: Atom, beatSync: Float) {
    // Draw atom glow effect
    drawCircle(
        color = atom.color.copy(alpha = 0.3f),
        radius = atom.radius * (1.2f + sin(beatSync * 2 * PI.toFloat()) * 0.1f),
        center = atom.position
    )
    
    // Draw atom
    drawCircle(
        color = atom.color,
        radius = atom.radius,
        center = atom.position
    )
    
    // Draw element symbol background
    drawCircle(
        color = Color.White.copy(alpha = 0.8f),
        radius = atom.radius * 0.6f,
        center = atom.position
    )
    
    // Draw element text in center using simple shapes
    // For H, O, C, N elements
    when (atom.element) {
        "H" -> {
            // Draw H using lines
            val lineLength = atom.radius * 0.4f
            drawLine(
                color = Color.Black,
                start = Offset(atom.position.x - lineLength/2, atom.position.y),
                end = Offset(atom.position.x + lineLength/2, atom.position.y),
                strokeWidth = atom.radius * 0.1f
            )
            drawLine(
                color = Color.Black,
                start = Offset(atom.position.x - lineLength/2, atom.position.y - lineLength/2),
                end = Offset(atom.position.x - lineLength/2, atom.position.y + lineLength/2),
                strokeWidth = atom.radius * 0.1f
            )
            drawLine(
                color = Color.Black,
                start = Offset(atom.position.x + lineLength/2, atom.position.y - lineLength/2),
                end = Offset(atom.position.x + lineLength/2, atom.position.y + lineLength/2),
                strokeWidth = atom.radius * 0.1f
            )
        }
        "O" -> {
            // Draw O as a circle
            drawCircle(
                color = Color.Black,
                radius = atom.radius * 0.3f,
                center = atom.position,
                style = Stroke(width = atom.radius * 0.1f)
            )
        }
        "C" -> {
            // Draw C as a curved line
            val path = Path().apply {
                val radius = atom.radius * 0.3f
                moveTo(atom.position.x + radius, atom.position.y)
                arcTo(
                    rect = androidx.compose.ui.geometry.Rect(
                        left = atom.position.x - radius,
                        top = atom.position.y - radius,
                        right = atom.position.x + radius,
                        bottom = atom.position.y + radius
                    ),
                    startAngleDegrees = 0f,
                    sweepAngleDegrees = 270f,
                    forceMoveTo = false
                )
            }
            drawPath(
                path = path,
                color = Color.Black,
                style = Stroke(width = atom.radius * 0.1f)
            )
        }
        "N" -> {
            // Draw N using lines
            val lineLength = atom.radius * 0.4f
            drawLine(
                color = Color.Black,
                start = Offset(atom.position.x - lineLength/2, atom.position.y - lineLength/2),
                end = Offset(atom.position.x - lineLength/2, atom.position.y + lineLength/2),
                strokeWidth = atom.radius * 0.1f
            )
            drawLine(
                color = Color.Black,
                start = Offset(atom.position.x - lineLength/2, atom.position.y - lineLength/2),
                end = Offset(atom.position.x + lineLength/2, atom.position.y + lineLength/2),
                strokeWidth = atom.radius * 0.1f
            )
            drawLine(
                color = Color.Black,
                start = Offset(atom.position.x + lineLength/2, atom.position.y - lineLength/2),
                end = Offset(atom.position.x + lineLength/2, atom.position.y + lineLength/2),
                strokeWidth = atom.radius * 0.1f
            )
        }
    }
}

private fun DrawScope.drawMolecule(molecule: Molecule, allAtoms: List<Atom>) {
    // Find the atoms in this molecule
    val moleculeAtoms = molecule.atoms
    
    // Draw bonds
    molecule.bonds.forEach { bond ->
        val atom1 = moleculeAtoms.find { it.id == bond.atom1 }
        val atom2 = moleculeAtoms.find { it.id == bond.atom2 }
        
        if (atom1 != null && atom2 != null) {
            // Draw bond line
            when (bond.type) {
                BondType.SINGLE -> {
                    drawLine(
                        color = Color.White.copy(alpha = 0.7f),
                        start = atom1.position,
                        end = atom2.position,
                        strokeWidth = 2f
                    )
                }
                BondType.DOUBLE -> {
                    // Calculate perpendicular offset for double bond
                    val direction = (atom2.position - atom1.position).normalize()
                    val perpendicular = Offset(-direction.y, direction.x) * 3f
                    
                    // Draw first line
                    drawLine(
                        color = Color.White.copy(alpha = 0.7f),
                        start = atom1.position + perpendicular,
                        end = atom2.position + perpendicular,
                        strokeWidth = 2f
                    )
                    
                    // Draw second line
                    drawLine(
                        color = Color.White.copy(alpha = 0.7f),
                        start = atom1.position - perpendicular,
                        end = atom2.position - perpendicular,
                        strokeWidth = 2f
                    )
                }
                BondType.TRIPLE -> {
                    // Calculate perpendicular offset for triple bond
                    val direction = (atom2.position - atom1.position).normalize()
                    val perpendicular = Offset(-direction.y, direction.x) * 4f
                    
                    // Draw first line
                    drawLine(
                        color = Color.White.copy(alpha = 0.7f),
                        start = atom1.position + perpendicular,
                        end = atom2.position + perpendicular,
                        strokeWidth = 2f
                    )
                    
                    // Draw second line (center)
                    drawLine(
                        color = Color.White.copy(alpha = 0.7f),
                        start = atom1.position,
                        end = atom2.position,
                        strokeWidth = 2f
                    )
                    
                    // Draw third line
                    drawLine(
                        color = Color.White.copy(alpha = 0.7f),
                        start = atom1.position - perpendicular,
                        end = atom2.position - perpendicular,
                        strokeWidth = 2f
                    )
                }
                BondType.IONIC -> {
                    // Draw dotted line for ionic bond
                    val distance = (atom2.position - atom1.position).getDistance()
                    val direction = (atom2.position - atom1.position).normalize()
                    val dotCount = (distance / 5f).toInt()
                    
                    for (i in 0 until dotCount) {
                        val t = i.toFloat() / dotCount
                        val dotPosition = atom1.position + direction * distance * t
                        drawCircle(
                            color = Color.White.copy(alpha = 0.7f),
                            radius = 1f,
                            center = dotPosition
                        )
                    }
                }
            }
        }
    }
    
    // Draw molecule formula at the center of the molecule
    val moleculeCenter = moleculeAtoms.fold(Offset.Zero) { acc, atom ->
        acc + atom.position
    } / moleculeAtoms.size.toFloat()
    
    // Get molecule formula
    val formula = getMoleculeFormula(molecule)
    
    // Draw formula background
    drawCircle(
        color = Color(0xFF6B4EFF).copy(alpha = 0.7f),
        radius = 30f,
        center = moleculeCenter
    )
    
    // Draw formula text using simple shapes
    // Just draw a placeholder for the formula
    drawCircle(
        color = Color.White,
        radius = 20f,
        center = moleculeCenter
    )
    
    // Draw atoms in the molecule
    moleculeAtoms.forEach { atom ->
        drawAtom(atom, 0f)
    }
}

private fun DrawScope.drawCollisionEffect(effect: CollisionEffect) {
    drawCircle(
        color = effect.color.copy(alpha = effect.alpha),
        radius = effect.radius,
        center = effect.position,
        style = Stroke(width = 2f)
    )
}

private fun DrawScope.drawBackgroundEffects(beatSync: Float) {
    // Draw pulsing background
    val baseColor = Color(0xFF121212)
    val accentColor = Color(0xFF6B4EFF).copy(alpha = 0.05f + 0.05f * sin(beatSync * 2 * PI.toFloat()))
    
    drawRect(
        color = baseColor,
        size = size
    )
    
    // Draw grid lines
    val gridSpacing = 50f
    val gridAlpha = 0.1f + 0.05f * sin(beatSync * 2 * PI.toFloat())
    
    // Vertical lines
    for (x in 0..(size.width / gridSpacing).toInt()) {
        drawLine(
            color = Color.Cyan.copy(alpha = gridAlpha),
            start = Offset(x * gridSpacing, 0f),
            end = Offset(x * gridSpacing, size.height),
            strokeWidth = 1f
        )
    }
    
    // Horizontal lines
    for (y in 0..(size.height / gridSpacing).toInt()) {
        drawLine(
            color = Color.Cyan.copy(alpha = gridAlpha),
            start = Offset(0f, y * gridSpacing),
            end = Offset(size.width, y * gridSpacing),
            strokeWidth = 1f
        )
    }
}

private fun DrawScope.drawEnergyIndicator(energy: Float) {
    val maxEnergy = 100f
    val normalizedEnergy = (energy / maxEnergy).coerceIn(0f, 1f)
    
    // Draw energy bar
    val barWidth = 100f
    val barHeight = 10f
    val barPosition = Offset(size.width - barWidth - 20f, 20f)
    
    // Background
    drawRect(
        color = Color.DarkGray,
        topLeft = barPosition,
        size = androidx.compose.ui.geometry.Size(barWidth, barHeight)
    )
    
    // Filled portion
    drawRect(
        color = when {
            normalizedEnergy < 0.3f -> Color.Green
            normalizedEnergy < 0.7f -> Color.Yellow
            else -> Color.Red
        },
        topLeft = barPosition,
        size = androidx.compose.ui.geometry.Size(barWidth * normalizedEnergy, barHeight)
    )
}

private fun createInitialAtoms(): List<Atom> {
    val atoms = mutableListOf<Atom>()
    
    // Create some random atoms
    repeat(5) { i ->
        atoms.add(
            createAtom(
                id = i,
                element = listOf("H", "O", "C", "N").random(),
                position = Offset(
                    Random.nextFloat() * 800f + 100f,
                    Random.nextFloat() * 400f + 100f
                )
            )
        )
    }
    
    return atoms
}

private fun createAtom(
    id: Int,
    element: String,
    position: Offset,
    isUserControlled: Boolean = false
): Atom {
    return when (element) {
        "H" -> Atom(
            id = id,
            element = "H",
            position = position,
            velocity = if (isUserControlled) Offset.Zero else Offset(
                Random.nextFloat() * 4f - 2f,
                Random.nextFloat() * 4f - 2f
            ),
            mass = 1f,
            charge = 1f,
            radius = 30f,
            color = Color(0xFF1E88E5),
            isUserControlled = isUserControlled
        )
        "O" -> Atom(
            id = id,
            element = "O",
            position = position,
            velocity = if (isUserControlled) Offset.Zero else Offset(
                Random.nextFloat() * 3f - 1.5f,
                Random.nextFloat() * 3f - 1.5f
            ),
            mass = 16f,
            charge = -2f,
            radius = 40f,
            color = Color(0xFFE53935),
            isUserControlled = isUserControlled
        )
        "C" -> Atom(
            id = id,
            element = "C",
            position = position,
            velocity = if (isUserControlled) Offset.Zero else Offset(
                Random.nextFloat() * 2f - 1f,
                Random.nextFloat() * 2f - 1f
            ),
            mass = 12f,
            charge = 0f,
            radius = 35f,
            color = Color(0xFF424242),
            isUserControlled = isUserControlled
        )
        "N" -> Atom(
            id = id,
            element = "N",
            position = position,
            velocity = if (isUserControlled) Offset.Zero else Offset(
                Random.nextFloat() * 2.5f - 1.25f,
                Random.nextFloat() * 2.5f - 1.25f
            ),
            mass = 14f,
            charge = -3f,
            radius = 35f,
            color = Color(0xFF7B1FA2),
            isUserControlled = isUserControlled
        )
        else -> Atom(
            id = id,
            element = "H",
            position = position,
            velocity = if (isUserControlled) Offset.Zero else Offset(
                Random.nextFloat() * 4f - 2f,
                Random.nextFloat() * 4f - 2f
            ),
            mass = 1f,
            charge = 1f,
            radius = 30f,
            color = Color(0xFF1E88E5),
            isUserControlled = isUserControlled
        )
    }
}

private fun updateAtomPhysics(atoms: List<Atom>, temperature: Float): List<Atom> {
    return atoms.map { atom ->
        if (atom.isUserControlled) {
            // Don't update physics for user-controlled atoms
            atom
        } else {
            // Apply velocity
            val newPosition = atom.position + atom.velocity * temperature * 2f
            
            // Apply boundary conditions
            val boundedPosition = Offset(
                newPosition.x.coerceIn(atom.radius, 1000f - atom.radius),
                newPosition.y.coerceIn(atom.radius, 600f - atom.radius)
            )
            
            // Bounce off walls
            val newVelocity = Offset(
                if (boundedPosition.x <= atom.radius || boundedPosition.x >= 1000f - atom.radius) {
                    -atom.velocity.x * 0.9f
                } else {
                    atom.velocity.x
                },
                if (boundedPosition.y <= atom.radius || boundedPosition.y >= 600f - atom.radius) {
                    -atom.velocity.y * 0.9f
                } else {
                    atom.velocity.y
                }
            )
            
            atom.copy(
                position = boundedPosition,
                velocity = newVelocity
            )
        }
    }
}

private fun updateMoleculePhysics(molecules: List<Molecule>): List<Molecule> {
    return molecules.mapNotNull { molecule ->
        // Update molecule position based on velocity
        val newPosition = molecule.position + molecule.velocity
        
        // Apply boundary conditions and bounce
        val boundedPosition = Offset(
            newPosition.x.coerceIn(50f, 950f),
            newPosition.y.coerceIn(50f, 550f)
        )
        
        val newVelocity = Offset(
            if (boundedPosition.x <= 50f || boundedPosition.x >= 950f) {
                -molecule.velocity.x * 0.9f
            } else {
                molecule.velocity.x
            },
            if (boundedPosition.y <= 50f || boundedPosition.y >= 550f) {
                -molecule.velocity.y * 0.9f
            } else {
                molecule.velocity.y
            }
        )
        
        // Decrease lifespan
        val newLifespan = molecule.lifespan - 0.1f
        
        // Return null if lifespan is over (molecule breaks apart)
        if (newLifespan <= 0) {
            null
        } else {
            molecule.copy(
                position = boundedPosition,
                velocity = newVelocity,
                lifespan = newLifespan
            )
        }
    }
}

private fun checkCollisions(
    atoms: List<Atom>,
    molecules: List<Molecule>
): Triple<List<Atom>, List<Offset>, Molecule?> {
    val collisionPositions = mutableListOf<Offset>()
    val updatedAtoms = atoms.toMutableList()
    var newMolecule: Molecule? = null
    
    // Check atom-atom collisions
    for (i in atoms.indices) {
        for (j in i + 1 until atoms.size) {
            val atom1 = atoms[i]
            val atom2 = atoms[j]
            
            // Skip user-controlled atoms
            if (atom1.isUserControlled || atom2.isUserControlled) {
                continue
            }
            
            val distance = (atom1.position - atom2.position).getDistance()
            val minDistance = atom1.radius + atom2.radius
            
            if (distance < minDistance) {
                // Collision detected!
                collisionPositions.add((atom1.position + atom2.position) / 2f)
                
                // Calculate new velocities (elastic collision)
                val totalMass = atom1.mass + atom2.mass
                val massRatio1 = 2 * atom2.mass / totalMass
                val massRatio2 = 2 * atom1.mass / totalMass
                
                val collisionVector = (atom1.position - atom2.position).normalize()
                val dot1 = atom1.velocity.x * collisionVector.x + atom1.velocity.y * collisionVector.y
                val dot2 = atom2.velocity.x * collisionVector.x + atom2.velocity.y * collisionVector.y
                
                val v1 = atom1.velocity - collisionVector * dot1 * massRatio1
                val v2 = atom2.velocity - collisionVector * dot2 * massRatio2
                
                // Update atoms with new velocities
                updatedAtoms[i] = atom1.copy(velocity = v1)
                updatedAtoms[j] = atom2.copy(velocity = v2)
                
                // Check if atoms can form a molecule
                val (canForm, bondType) = canFormMolecule(atom1, atom2)
                if (canForm && Random.nextFloat() < 0.1f) { // 10% chance to form a molecule on collision
                    // Create a new molecule
                    newMolecule = createMolecule(atom1, atom2, bondType)
                    break
                }
            }
        }
        if (newMolecule != null) break
    }
    
    return Triple(updatedAtoms, collisionPositions, newMolecule)
}

private fun canFormMolecule(atom1: Atom, atom2: Atom): Pair<Boolean, BondType> {
    // Rules for molecule formation with bond types
    return when {
        atom1.element == "H" && atom2.element == "H" -> Pair(true, BondType.SINGLE)
        atom1.element == "H" && atom2.element == "O" -> Pair(true, BondType.SINGLE)
        atom1.element == "O" && atom2.element == "H" -> Pair(true, BondType.SINGLE)
        atom1.element == "C" && atom2.element == "H" -> Pair(true, BondType.SINGLE)
        atom1.element == "H" && atom2.element == "C" -> Pair(true, BondType.SINGLE)
        atom1.element == "N" && atom2.element == "H" -> Pair(true, BondType.SINGLE)
        atom1.element == "H" && atom2.element == "N" -> Pair(true, BondType.SINGLE)
        atom1.element == "C" && atom2.element == "O" -> Pair(true, BondType.DOUBLE)
        atom1.element == "O" && atom2.element == "C" -> Pair(true, BondType.DOUBLE)
        atom1.element == "C" && atom2.element == "N" -> Pair(true, BondType.TRIPLE)
        atom1.element == "N" && atom2.element == "C" -> Pair(true, BondType.TRIPLE)
        atom1.element == "O" && atom2.element == "O" -> Pair(true, BondType.DOUBLE)
        atom1.element == "N" && atom2.element == "O" -> Pair(true, BondType.IONIC)
        atom1.element == "O" && atom2.element == "N" -> Pair(true, BondType.IONIC)
        else -> Pair(false, BondType.SINGLE)
    }
}

private fun createMolecule(atom1: Atom, atom2: Atom, bondType: BondType): Molecule {
    // Calculate molecule properties
    val moleculePosition = (atom1.position + atom2.position) / 2f
    val moleculeVelocity = (atom1.velocity + atom2.velocity) / 2f
    
    // Create bond
    val bond = Bond(
        atom1 = atom1.id,
        atom2 = atom2.id,
        strength = 1.0f,
        type = bondType
    )
    
    // Create molecule
    return Molecule(
        id = Random.nextInt(1000000),
        atoms = listOf(
            atom1.copy(
                position = atom1.position - moleculePosition + Offset(0f, 0f),
                isUserControlled = false
            ),
            atom2.copy(
                position = atom2.position - moleculePosition + Offset(0f, 0f),
                isUserControlled = false
            )
        ),
        bonds = listOf(bond),
        position = moleculePosition,
        velocity = moleculeVelocity,
        energy = 10f,
        lifespan = 100f + Random.nextFloat() * 100f
    )
}

private fun getMoleculeFormula(molecule: Molecule): String {
    // Count atoms by element
    val elementCounts = molecule.atoms.groupBy { it.element }
        .mapValues { it.value.size }
    
    // Build formula string
    val formula = StringBuilder()
    
    // Order elements by chemical convention: C, H, O, N, others
    val orderedElements = listOf("C", "H", "O", "N")
    
    // First add the ordered elements
    for (element in orderedElements) {
        val count = elementCounts[element] ?: 0
        if (count > 0) {
            formula.append(element)
            if (count > 1) {
                formula.append(count)
            }
        }
    }
    
    // Then add any other elements not in the ordered list
    elementCounts.forEach { (element, count) ->
        if (!orderedElements.contains(element)) {
            formula.append(element)
            if (count > 1) {
                formula.append(count)
            }
        }
    }
    
    return formula.toString()
}

private fun Offset.getDistance(): Float {
    return sqrt(x * x + y * y)
}

private fun Offset.normalize(): Offset {
    val distance = getDistance()
    return if (distance > 0) {
        Offset(x / distance, y / distance)
    } else {
        Offset(0f, 0f)
    }
}

// Add the missing calculateSystemEnergy function
private fun calculateSystemEnergy(atoms: List<Atom>, molecules: List<Molecule>): Float {
    var totalEnergy = 0f
    
    // Kinetic energy of atoms
    atoms.forEach { atom ->
        val speed = atom.velocity.getDistance()
        totalEnergy += 0.5f * atom.mass * speed * speed
    }
    
    // Energy of molecules
    molecules.forEach { molecule ->
        totalEnergy += molecule.energy
    }
    
    return totalEnergy
} 