package com.drozdova.dancersbase.database

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.drozdova.dancersbase.DEFAULT_QUERY

@Dao
interface DancerDao {
    @Query(DEFAULT_QUERY)
    fun getDancersByCreation(): LiveData<List<Dancer>>

    @RawQuery(observedEntities = [Dancer::class])
    fun getSortedDancers(query: SupportSQLiteQuery): LiveData<List<Dancer>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(dancer: Dancer)

    @Update
    suspend fun update(dancer: Dancer)

    @Delete
    suspend fun delete(dancer: Dancer)

    @Query("DELETE FROM dancer_table")
    suspend fun deleteAll()
}