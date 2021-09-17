package com.drozdova.dancersbase.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.drozdova.dancersbase.DATABASE_NAME
import com.drozdova.dancersbase.DATABASE_TABLE_NAME
import com.drozdova.dancersbase.DATABASE_VERSION
import com.drozdova.dancersbase.DEFAULT_QUERY
import java.sql.SQLException

private const val CREATE_TABLE_SQL =
    "CREATE TABLE IF NOT EXISTS $DATABASE_TABLE_NAME "+
            "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+
            "name TEXT NOT NULL, "+
            "year INTEGER NOT NULL, "+
            "club TEXT NOT NULL, "+
            "league TEXT NOT NULL); "
private const val CREATE_INDEX_SQL =
    "CREATE UNIQUE INDEX IF NOT EXISTS "+
            "'index_dancer_table_name_year_club_league' ON 'dancer_table' ('name', 'year', 'club', 'league')"

private const val LOG_TAG = "DancerSQLiteOpenHelper"

class DancerSQLiteOpenHelper (context: Context) : SQLiteOpenHelper(context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION), DancerDao {

        private val resultLiveData = MutableLiveData<List<Dancer>>()

        override fun onCreate(db: SQLiteDatabase) {
            try {
                db.execSQL(CREATE_TABLE_SQL)
                db.execSQL(CREATE_INDEX_SQL)
            } catch (exception: SQLException) {
                Log.e(LOG_TAG, "Ошибка при создании базы данных", exception)
            }
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            Log.d(LOG_TAG, "onUpgrade called")
        }

        private fun getCursor(query: SupportSQLiteQuery): Cursor {
            return readableDatabase.rawQuery(query.sql, null)
        }

        private fun getListOfDancers(query: SupportSQLiteQuery = SimpleSQLiteQuery(DEFAULT_QUERY)): List<Dancer> {
            val listOfDancers = mutableListOf<Dancer>()
            getCursor(query).use { cursor ->
                if (cursor.moveToFirst()) {
                    do {
                        listOfDancers.add(
                            Dancer(cursor.getInt(cursor.getColumnIndex("id")),
                                cursor.getString(cursor.getColumnIndex("name")),
                                cursor.getInt(cursor.getColumnIndex("year")),
                                cursor.getString(cursor.getColumnIndex("club")),
                                cursor.getString(cursor.getColumnIndex("league"))
                            ))
                    } while (cursor.moveToNext())
                }
            }
            return listOfDancers.toList()
        }

        override fun getDancersByCreation(): LiveData<List<Dancer>> {
            resultLiveData.postValue(getListOfDancers())
            return  resultLiveData
        }

        override fun getSortedDancers(query: SupportSQLiteQuery): LiveData<List<Dancer>> {
            resultLiveData.postValue(getListOfDancers(query))
            return  resultLiveData
        }
        override suspend fun insert(dancer:Dancer){
            val values = ContentValues().apply {
                put("name", dancer.name)
                put("year", dancer.year)
                put("club", dancer.club)
                put("league", dancer.league)
            }
            readableDatabase.insert(DATABASE_TABLE_NAME,null, values)
        }

        override suspend fun update(dancer:Dancer){
            val values = ContentValues().apply {
                put("name", dancer.name)
                put("year", dancer.year)
                put("club", dancer.club)
                put("league", dancer.league)
            }
            val selection = "id = ?"
            val selectionArgs = arrayOf(dancer.id.toString())
            readableDatabase.update(DATABASE_TABLE_NAME, values, selection, selectionArgs)
        }

        override suspend fun delete(dancer: Dancer) {
            val selection = "id = ?"
            val selectionArgs = arrayOf(dancer.id.toString())
            readableDatabase.delete(DATABASE_TABLE_NAME, selection, selectionArgs)
        }

        override suspend fun deleteAll() {
            readableDatabase.delete(DATABASE_TABLE_NAME, null, null)
        }
    }