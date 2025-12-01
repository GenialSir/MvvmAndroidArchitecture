package com.genialsir.mvvmarchitecture.di



import com.genialsir.mvvmcommon.error.mapper.ErrorMapper
import com.genialsir.mvvmcommon.error.mapper.ErrorMapperSource
import com.genialsir.mvvmcommon.usecase.error.ErrorManager
import com.genialsir.mvvmcommon.usecase.error.ErrorUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// with @Module we Telling Dagger that, this is a Dagger module
@Module
@InstallIn(SingletonComponent::class)
abstract class ErrorModule {
    @Binds
    @Singleton
    abstract fun provideErrorFactoryImpl(errorManager: ErrorManager): ErrorUseCase

    @Binds
    @Singleton
    abstract fun provideErrorMapper(errorMapper: ErrorMapper): ErrorMapperSource
}
