package com.stotra.sahasranamam.di

import com.stotra.sahasranamam.data.repository.StotraRepositoryImpl
import com.stotra.sahasranamam.domain.repository.StotraRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindStotraRepository(
        stotraRepositoryImpl: StotraRepositoryImpl
    ): StotraRepository
}
