package fm.mrc.quantumexplorers.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.delay

class GameTimer {
    fun createTimer(durationSeconds: Int): Flow<Int> = flow {
        var remainingSeconds = durationSeconds
        while (remainingSeconds > 0) {
            emit(remainingSeconds)
            delay(1000)
            remainingSeconds--
        }
        emit(0)
    }
} 