package com.drozdova.dancersbase.database

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "dancer_table", indices = [Index(value = ["name", "year", "club", "league"], unique = true)])
data class Dancer(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "name", ) val name: String,
    @ColumnInfo(name = "year") val year: Int,
    @ColumnInfo(name = "club") val club: String,
    @ColumnInfo(name = "league") val league: String)
