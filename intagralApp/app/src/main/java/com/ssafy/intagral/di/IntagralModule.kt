package com.ssafy.intagral.di

import com.ssafy.intagral.data.service.*
import com.ssafy.intagral.util.AuthInterceptor
import com.ssafy.intagral.util.ResponseInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CommonRepository {
    private val BASE_URL = "https://k7a304.p.ssafy.io"

    private val client: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor())
        .addInterceptor(AuthInterceptor())
        .addInterceptor(ResponseInterceptor())
        .build()

    @Singleton
    @Provides
    fun getCommonRepository(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}

@Module
@InstallIn(SingletonComponent::class)
object PresetServiceModule {

    @Singleton
    @Provides
    fun providePresetService(): PresetService = PresetService()
}
@Module
@InstallIn(SingletonComponent::class)
object UserServiceModule {

    @Singleton
    @Provides
    fun provideUserService(): UserService = UserService()
}

@Module
@InstallIn(SingletonComponent::class)
object HashtagServiceModule {

    @Singleton
    @Provides
    fun provideHashtagService(): HashtagService = HashtagService()
}

@Module
@InstallIn(SingletonComponent::class)
object SearchServiceModule {

    @Singleton
    @Provides
    fun provideSearchService(): SearchService = SearchService()
}

@Module
@InstallIn(SingletonComponent::class)
object PostServiceModule {

    @Singleton
    @Provides
    fun providePostService(): PostService = PostService()
}

@Module
@InstallIn(SingletonComponent::class)
object FollowServiceModule {

    @Singleton
    @Provides
    fun provideFollowService(): FollowService = FollowService()
}