package com.testtube.gstreporter.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query

@Dao
abstract class PartyDao : BaseDao<PartyInfo>() {

    @Query("SELECT * FROM PartyInfo")
    abstract fun getAll(): LiveData<List<PartyInfo>>
//
//    @Query("SELECT * FROM Party WHERE name LIKE :name LIMIT 10")
//    fun findByName(name: String): List<Party>

}