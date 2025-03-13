package fm.mrc.quantumexplorers.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import fm.mrc.quantumexplorers.accessibility.AccessibilityManager
import fm.mrc.quantumexplorers.accessibility.TTSManager
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AccessibilityModule {
    
    @Provides
    @Singleton
    fun provideTTSManager(
        @ApplicationContext context: Context
    ): TTSManager {
        return TTSManager(context)
    }

    @Provides
    @Singleton
    fun provideAccessibilityManager(
        @ApplicationContext context: Context,
        ttsManager: TTSManager
    ): AccessibilityManager {
        return AccessibilityManager(context, ttsManager)
    }
} 