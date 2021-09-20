package com.drozdova.dancersbase.database

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.sqlite.db.SimpleSQLiteQuery
import com.drozdova.dancersbase.DATABASE_TABLE_NAME

class DancerRepository (private val dancerRoomDao: DancerDao, private val dancerCursorDao: DancerSQLiteOpenHelper) {
    var dbImplRoom = true //Room = true, Cursor = false
    val allDancers: LiveData<List<Dancer>> = if(dbImplRoom){dancerRoomDao.getDancersByCreation()}else{dancerCursorDao.getDancersByCreation()}

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(dancer: Dancer) {
        if(dbImplRoom){dancerRoomDao.insert(dancer)}
        else{dancerCursorDao.insert(dancer)}
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(dancer: Dancer){
        if(dbImplRoom){dancerRoomDao.delete(dancer)}
        else{dancerCursorDao.delete(dancer)}
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun update(dancer: Dancer){
        if(dbImplRoom){dancerRoomDao.update(dancer)}
        else{dancerCursorDao.update(dancer)}
    }

    fun sort(orderFields: String): LiveData<List<Dancer>> {
        val query = SimpleSQLiteQuery(
            "SELECT * FROM $DATABASE_TABLE_NAME ORDER BY $orderFields")
        return if(dbImplRoom){
            dancerRoomDao.getSortedDancers(query)}
        else { dancerCursorDao.getSortedDancers(query)}
    }
}