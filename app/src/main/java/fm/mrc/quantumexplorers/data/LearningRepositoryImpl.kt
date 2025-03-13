package fm.mrc.quantumexplorers.data

import fm.mrc.quantumexplorers.model.LearningUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class LearningRepositoryImpl @Inject constructor() : LearningRepository {
    override fun getLearningUnits(): Flow<List<LearningUnit>> {
        // TODO: Replace with actual data source
        return flowOf(emptyList())
    }

    override fun getUnitProgress(unitId: String): Float {
        // TODO: Replace with actual progress tracking
        return 0f
    }
} 