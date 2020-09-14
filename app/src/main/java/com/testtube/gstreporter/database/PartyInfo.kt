package com.testtube.gstreporter.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity()
data class PartyInfo(
    @PrimaryKey val gstId: String,
    @ColumnInfo val name: String
)