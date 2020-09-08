package com.testtube.gstreporter.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity()
data class Party(
    @PrimaryKey val uid: String,
    @ColumnInfo(name = "name") val name: String
)