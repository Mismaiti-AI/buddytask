package com.mytask.core.data.local.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface UserDao {

    @Query("SELECT * FROM users")
    fun getAll(): List<UserEntity>

    // insert user
    @Upsert
    fun insert(user: UserEntity)

    @Query("DELETE FROM users WHERE id = :id")
    fun deleteUser(id: Int)

    @Query("SELECT * FROM users WHERE auth_provider = :provider AND provider_user_id = :providerUserId LIMIT 1")
    fun getByProviderId(provider: String, providerUserId: String): UserEntity?
}