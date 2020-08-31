package com.testtube.gstreporter.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PartyDao {

    @Query("SELECT * FROM Party")
    fun getAll(): List<Party>

    @Query("SELECT * FROM Party WHERE name LIKE :name LIMIT 10")
    fun findByName(name: String): List<Party>

    @Insert
    fun insert(vararg party: Party)

    @Delete
    fun delete(party: Party)

}