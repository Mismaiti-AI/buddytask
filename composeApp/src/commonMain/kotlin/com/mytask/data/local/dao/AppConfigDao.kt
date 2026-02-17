package com.mytask.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.mytask.data.local.entity.AppConfigEntity

@Dao
interface AppConfigDao {
    @Upsert suspend fun insert(config: AppConfigEntity)
    @Query("SELECT * FROM app_config ORDER BY updated_at DESC LIMIT 1") suspend fun getLatest(): AppConfigEntity?
    @Query("DELETE FROM app_config WHERE id = :id") suspend fun delete(id: Int)
}