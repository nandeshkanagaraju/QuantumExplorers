package fm.mrc.quantumexplorers.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import fm.mrc.quantumexplorers.utils.GameSpeechManager
import fm.mrc.quantumexplorers.utils.TextToSpeechManager
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TextToSpeechModule {
    @Binds
    @Singleton
    abstract fun bindTextToSpeechManager(
        gameSpeechManager: GameSpeechManager
    ): TextToSpeechManager
} 