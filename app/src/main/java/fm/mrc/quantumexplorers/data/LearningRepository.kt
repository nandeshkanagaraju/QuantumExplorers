package fm.mrc.quantumexplorers.data

import fm.mrc.quantumexplorers.model.LearningUnit
import kotlinx.coroutines.flow.Flow

interface LearningRepository {
    fun getLearningUnits(): Flow<List<LearningUnit>>
    fun getUnitProgress(unitId: String): Float
} 