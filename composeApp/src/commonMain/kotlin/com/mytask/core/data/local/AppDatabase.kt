package com.mytask.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.ConstructedBy
import com.mytask.data.local.dao.AppConfigDao
import com.mytask.data.local.dao.AssignmentDao
import com.mytask.data.local.dao.ExamDao
import com.mytask.data.local.dao.ProjectDao
import com.mytask.data.local.entity.AppConfigEntity
import com.mytask.data.local.entity.AssignmentEntity
import com.mytask.data.local.entity.ExamEntity
import com.mytask.data.local.entity.ProjectEntity
import com.mytask.core.data.local.model.UserDao
import com.mytask.core.data.local.model.UserEntity

@Database(
    entities = [
        AssignmentEntity::class,
        ExamEntity::class,
        ProjectEntity::class,
        AppConfigEntity::class,
        UserEntity::class
    ],
    version = 6,
    exportSchema = false
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun assignmentDao(): AssignmentDao
    abstract fun examDao(): ExamDao
    abstract fun projectDao(): ProjectDao
    abstract fun appConfigDao(): AppConfigDao
    abstract fun userDao(): UserDao
}