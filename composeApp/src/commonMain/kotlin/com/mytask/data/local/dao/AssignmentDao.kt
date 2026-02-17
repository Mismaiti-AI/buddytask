package com.mytask.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.mytask.data.local.entity.AssignmentEntity

@Dao
interface AssignmentDao {
    @Query("SELECT * FROM assignments") suspend fun getAll(): List<AssignmentEntity>
    @Query("SELECT * FROM assignments WHERE id = :id") suspend fun getById(id: Int): AssignmentEntity?
    @Upsert suspend fun insert(assignment: AssignmentEntity)
    @Query("DELETE FROM assignments WHERE id = :id") suspend fun delete(id: Int)
    @Query("UPDATE assignments SET completed = 1 WHERE id = :id") suspend fun markComplete(id: Int)
    @Query("SELECT * FROM assignments WHERE completed = 0 ORDER BY due_date ASC") suspend fun getPendingAssignments(): List<AssignmentEntity>
    @Query("SELECT * FROM assignments WHERE due_date > :now ORDER BY due_date ASC") suspend fun getUpcoming(now: Long): List<AssignmentEntity>
}