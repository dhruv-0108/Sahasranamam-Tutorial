package com.stotra.sahasranamam.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.stotra.sahasranamam.data.local.dao.PadaDao
import com.stotra.sahasranamam.data.local.dao.ShlokaDao
import com.stotra.sahasranamam.data.local.dao.StotraDao
import com.stotra.sahasranamam.data.local.dao.UserSrsProgressDao
import com.stotra.sahasranamam.data.local.entity.PadaEntity
import com.stotra.sahasranamam.data.local.entity.ShlokaEntity
import com.stotra.sahasranamam.data.local.entity.StotraEntity
import com.stotra.sahasranamam.data.local.entity.UserSrsProgressEntity

@Database(
    entities = [
        StotraEntity::class,
        ShlokaEntity::class,
        PadaEntity::class,
        UserSrsProgressEntity::class
    ],
    version = 8,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun stotraDao(): StotraDao
    abstract fun shlokaDao(): ShlokaDao
    abstract fun padaDao(): PadaDao
    abstract fun userSrsProgressDao(): UserSrsProgressDao
}
