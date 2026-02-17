package com.mytask.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.mytask.data.local.entity.ExamEntity

@Dao
interface ExamDao {
    @Query("SELECT * FROM exams") suspend fun getAll(): List<ExamEntity>
    @Query("SELECT * FROM exams WHERE id = :id") suspend fun getById(id: Int): ExamEntity?
    @Upsert suspend fun insert(exam: ExamEntity)
    @Query("DELETE FROM exams WHERE id = :id") suspend fun delete(id: Int)
    @Query("SELECT * FROM exams WHERE exam_date > :now ORDER BY exam_date ASC") suspend fun getUpcomingExams(now: Long): List<ExamEntity>
}