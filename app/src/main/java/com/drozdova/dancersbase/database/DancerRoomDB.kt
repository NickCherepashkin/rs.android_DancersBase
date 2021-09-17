package com.drozdova.dancersbase.database;

import android.content.Context
import androidx.room.Database;
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.drozdova.dancersbase.DATABASE_NAME
import com.drozdova.dancersbase.DATABASE_VERSION
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers


@Database(entities = [Dancer::class], version = DATABASE_VERSION)
abstract class DancerRoomDB : RoomDatabase() {
    abstract fun dancerDao(): DancerDao

    companion object {
        @Volatile
        private var INSTANCE: DancerRoomDB? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): DancerRoomDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        DancerRoomDB::class.java,
                        DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                        .addCallback(DancerDatabaseCallback(scope))
                        .build()
                INSTANCE = instance
                instance
            }
        }

        private class DancerDatabaseCallback(
                private val scope:CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                        scope.launch(Dispatchers.IO) {
                    populateDatabase(database.dancerDao())
                }
                }
            }
        }

        suspend fun populateDatabase(dancerDao: DancerDao) {
            var dancer = Dancer(0,"Дроздова Вероника", 2001, "A-Class", "Open Class")
            dancerDao.insert(dancer)
            dancer = Dancer(0,"Сузина Ирма", 2002, "Alliance", "Open Class")
            dancerDao.insert(dancer)
        }
    }
}
