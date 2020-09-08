package com.testtube.gstreporter.database

import androidx.room.*

@Dao
abstract class BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insert(entity: T)

    @Update
    abstract fun update(entity: T)

    @Delete
    abstract fun delete(entity: T)
}