package com.mytask.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.mytask.data.local.entity.ProjectEntity

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects") suspend fun getAll(): List<ProjectEntity>
    @Query("SELECT * FROM projects WHERE id = :id") suspend fun getById(id: Int): ProjectEntity?
    @Upsert suspend fun insert(project: ProjectEntity)
    @Query("DELETE FROM projects WHERE id = :id") suspend fun delete(id: Int)
    @Query("UPDATE projects SET progress = :progress WHERE id = :id") suspend fun updateProgress(id: Int, progress: Int)
    @Query("SELECT * FROM projects WHERE due_date > :now ORDER BY due_date ASC") suspend fun getUpcomingProjects(now: Long): List<ProjectEntity>
}