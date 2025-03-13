package fm.mrc.quantumexplorers.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fm.mrc.quantumexplorers.R
import fm.mrc.quantumexplorers.model.*
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import fm.mrc.quantumexplorers.viewmodel.LearningProgressViewModel
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalAnimationApi::class
)
@Composable
fun LearningZoneScreen(
    viewModel: LearningProgressViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val learningUnits by viewModel.learningUnits.collectAsState()
    var selectedCategory by remember { mutableStateOf<GameCategory?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    
    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Learning Zone") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            actions = {
                IconButton(onClick = { /* Handle search */ }) {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
            }
        )
        
        // Categories
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(GameCategory.values()) { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { 
                        selectedCategory = if (selectedCategory == category) null else category
                    },
                    label = { Text(category.toString()) },
                    leadingIcon = if (selectedCategory == category) {
                        {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = "Clear filter",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    } else null
                )
            }
        }
        
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(
                learningUnits.filter { unit ->
                    selectedCategory?.let { cat -> unit.category == cat } ?: true
                }
            ) { unit ->
                UnitCard(
                    unit = unit,
                    progress = viewModel.getUnitProgress(unit.id),
                    onClick = { /* Handle click */ },
                    viewModel = viewModel
                )
            }
        }
    }
}

@Composable
private fun UnitCard(
    unit: LearningUnit,
    progress: Float,
    onClick: () -> Unit,
    viewModel: LearningProgressViewModel
) {
    val safeProgress = remember(progress) {
        progress.coerceIn(0f, 1f)
    }
    val timeRemaining by viewModel.gameTimeRemaining.collectAsState(initial = null)
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .semantics {
                contentDescription = buildString {
                    append("Learning unit: ${unit.title}. ")
                    append("${unit.description}. ")
                    append("Difficulty level ${unit.difficulty}. ")
                    append("Estimated time ${unit.estimatedMinutes} minutes.")
                }
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = unit.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = unit.description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            
            LinearProgressIndicator(
                progress = safeProgress,
                modifier = Modifier.fillMaxWidth()
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(unit.difficulty) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Text(
                    text = "${(safeProgress * 100).toInt()}% Complete",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            timeRemaining?.let { remaining ->
                Text(
                    text = "Time remaining: ${remaining / 60}:${String.format("%02d", remaining % 60)}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            IconButton(
                onClick = { viewModel.readUnitDescription(unit) },
                modifier = Modifier.align(Alignment.End)
            ) {
                Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = "Read description aloud"
                )
            }
        }
    }
}

@Composable
private fun LearningContent(
    content: List<LearningContent>,
    onProgressUpdate: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        content.forEach { item ->
            when (item.type) {
                ContentType.TEXT -> {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = item.body,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                ContentType.IMAGE -> {
                    item.imageResId?.let { resId ->
                        Image(
                            painter = painterResource(resId),
                            contentDescription = item.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                    }
                }
                ContentType.CODE -> {
                    item.codeSnippet?.let { code ->
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                text = code,
                                modifier = Modifier.padding(8.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
                ContentType.INTERACTIVE -> {
                    // Placeholder for interactive content
                    Button(
                        onClick = { /* TODO: Launch interactive content */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(item.title)
                    }
                }
            }
        }
    }
} 