package com.wordscape.di

import com.wordscape.audio.TtsVoiceNarration
import com.wordscape.audio.VoiceNarration
import com.wordscape.domain.AdaptiveDifficultyEngine
import com.wordscape.domain.DummyAdaptiveDifficultyEngine
import com.wordscape.domain.DummyLearningAnalyticsEngine
import com.wordscape.domain.DummyWordRecommendationEngine
import com.wordscape.domain.LearningAnalyticsEngine
import com.wordscape.domain.WordRecommendationEngine
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindWordRecommendationEngine(
        impl: DummyWordRecommendationEngine
    ): WordRecommendationEngine

    @Binds
    @Singleton
    abstract fun bindLearningAnalyticsEngine(
        impl: DummyLearningAnalyticsEngine
    ): LearningAnalyticsEngine

    @Binds
    @Singleton
    abstract fun bindAdaptiveDifficultyEngine(
        impl: DummyAdaptiveDifficultyEngine
    ): AdaptiveDifficultyEngine

    @Binds
    @Singleton
    abstract fun bindVoiceNarration(
        impl: TtsVoiceNarration
    ): VoiceNarration
}
