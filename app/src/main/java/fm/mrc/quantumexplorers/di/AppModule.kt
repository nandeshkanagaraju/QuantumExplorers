package fm.mrc.quantumexplorers.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import fm.mrc.quantumexplorers.data.LearningRepository
import fm.mrc.quantumexplorers.data.LearningRepositoryImpl
import fm.mrc.quantumexplorers.utils.AudioSupport
import fm.mrc.quantumexplorers.utils.GameTimer
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideLearningRepository(): LearningRepository {
        return LearningRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideAudioSupport(@ApplicationContext context: Context): AudioSupport {
        return AudioSupport(context)
    }

    @Provides
    @Singleton
    fun provideGameTimer(): GameTimer {
        return GameTimer()
    }
} 