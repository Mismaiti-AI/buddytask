package com.mytask.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mytask.core.di.ConstructedBy
import com.mytask.data.local.dao.AppConfigDao
import com.mytask.data.local.dao.AssignmentDao
import com.mytask.data.local.dao.ExamDao
import com.mytask.data.local.dao.ProjectDao
import com.mytask.data.local.entity.AppConfigEntity
import com.mytask.data.local.entity.AssignmentEntity
import com.mytask.data.local.entity.ExamEntity
import com.mytask.data.local.entity.ProjectEntity

@Database(
    entities = [
        AssignmentEntity::class,
        ExamEntity::class,
        ProjectEntity::class,
        AppConfigEntity::class
    ],
    version = 5,
    exportSchema = false
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract val assignmentDao: AssignmentDao
    abstract val examDao: ExamDao
    abstract val projectDao: ProjectDao
    abstract val appConfigDao: AppConfigDao
}