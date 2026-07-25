package com.stotra.sahasranamam.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.firestore.FirebaseFirestore
import com.stotra.sahasranamam.data.local.AppDatabase
import com.stotra.sahasranamam.data.local.dao.PadaDao
import com.stotra.sahasranamam.data.local.dao.ShlokaDao
import com.stotra.sahasranamam.data.local.dao.StotraDao
import com.stotra.sahasranamam.data.local.dao.UserSrsProgressDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "sahasranamam_database.db"
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    fun provideStotraDao(db: AppDatabase): StotraDao = db.stotraDao()

    @Provides
    fun provideShlokaDao(db: AppDatabase): ShlokaDao = db.shlokaDao()

    @Provides
    fun providePadaDao(db: AppDatabase): PadaDao = db.padaDao()

    @Provides
    fun provideUserSrsProgressDao(db: AppDatabase): UserSrsProgressDao = db.userSrsProgressDao()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()
}
