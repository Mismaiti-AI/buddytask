package com.mismaiti.core.data.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import com.mismaiti.core.data.local.model.UserDao
import com.mismaiti.core.data.local.model.UserEntity

@Database(
    entities = [
        UserEntity::class
        // Add more entities here
    ],
    version = 2,
    exportSchema = false
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase: RoomDatabase() {

    abstract val userDao: UserDao

    // Add more dao's here
}
