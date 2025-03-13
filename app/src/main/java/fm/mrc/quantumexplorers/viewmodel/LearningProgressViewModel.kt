package fm.mrc.quantumexplorers.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import fm.mrc.quantumexplorers.model.LearningUnit
import fm.mrc.quantumexplorers.model.ExplorerProfile
import fm.mrc.quantumexplorers.model.GameCategory
import fm.mrc.quantumexplorers.model.LearningContent
import fm.mrc.quantumexplorers.model.ContentType
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel
import fm.mrc.quantumexplorers.data.LearningRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import fm.mrc.quantumexplorers.utils.AudioSupport
import fm.mrc.quantumexplorers.utils.GameTimer

@HiltViewModel
class LearningProgressViewModel @Inject constructor(
    private val repository: LearningRepository,
    private val audioSupport: fm.mrc.quantumexplorers.utils.AudioSupport,
    private val gameTimer: fm.mrc.quantumexplorers.utils.GameTimer
) : ViewModel() {
    private val _learningUnits = MutableStateFlow<List<LearningUnit>>(emptyList())
    val learningUnits: StateFlow<List<LearningUnit>> = repository
        .getLearningUnits()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _unitProgress = MutableStateFlow<Map<String, Float>>(emptyMap())
    val unitProgress = _unitProgress.asStateFlow()

    private val _userProfile = MutableStateFlow<ExplorerProfile?>(null)
    val userProfile = _userProfile.asStateFlow()

    private val _gameTimeRemaining = MutableStateFlow<Int?>(null)
    val gameTimeRemaining: StateFlow<Int?> = _gameTimeRemaining.asStateFlow()

    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress

    init {
        // Initialize with sample data
        _learningUnits.value = listOf(
            LearningUnit(
                id = "1",
                title = "Quantum Basics",
                description = "Introduction to quantum concepts",
                category = GameCategory.SCIENCE,
                content = listOf(
                    LearningContent(
                        type = ContentType.TEXT,
                        title = "Introduction",
                        body = "Basic quantum concepts..."
                    )
                ),
                difficulty = 1,
                estimatedMinutes = 30,
                icon = "⚛️"
            ),
            LearningUnit(
                id = "2",
                title = "Wave Functions",
                description = "Understanding wave mechanics",
                category = GameCategory.SCIENCE,
                content = listOf(
                    LearningContent(
                        type = ContentType.TEXT,
                        title = "Wave Basics",
                        body = "Understanding waves..."
                    )
                ),
                difficulty = 2,
                estimatedMinutes = 45,
                icon = "🌊"
            ),
            LearningUnit(
                id = "3",
                title = "Quantum Math",
                description = "Mathematical foundations",
                category = GameCategory.MATH,
                content = listOf(
                    LearningContent(
                        type = ContentType.TEXT,
                        title = "Math Basics",
                        body = "Quantum mathematics..."
                    )
                ),
                difficulty = 3,
                estimatedMinutes = 60,
                icon = "🔢"
            )
        )
    }

    fun getUnitProgress(unitId: String): Float {
        return repository.getUnitProgress(unitId)
    }
    
    fun updateUnitProgress(unitId: String, newProgress: Float) {
        viewModelScope.launch {
            _unitProgress.value = _unitProgress.value + (unitId to newProgress)
            checkAndUpdateAchievements(unitId, newProgress)
        }
    }
    
    private fun checkAndUpdateAchievements(unitId: String, progress: Float) {
        if (progress >= 1.0f) {
            // Unit completed - award achievements and update profile
            // TODO: Implement achievement system
        }
    }

    fun readUnitDescription(unit: LearningUnit) {
        audioSupport.speak(unit.description)
    }

    fun updateProgress(newProgress: Float) {
        _progress.value = newProgress.coerceIn(0f, 1f)
    }
} 