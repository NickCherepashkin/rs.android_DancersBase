package com.drozdova.dancersbase.viewmodel

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import com.drozdova.dancersbase.R
import com.drozdova.dancersbase.database.Dancer
import com.drozdova.dancersbase.database.DancerRepository
import com.drozdova.dancersbase.database.DancerRoomDB
import com.drozdova.dancersbase.database.DancerSQLiteOpenHelper
import kotlinx.coroutines.launch

class DancerViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: DancerRepository
    var allDancers: LiveData<List<Dancer>>
    private var prefs: SharedPreferences

    init {
        val dancerRoomDao = DancerRoomDB.getDatabase(application, viewModelScope).dancerDao()
        val dancerCursorDao = DancerSQLiteOpenHelper(application.applicationContext)
        repository = DancerRepository(dancerRoomDao, dancerCursorDao)
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplication())
        refreshImpl()
        allDancers = repository.allDancers
    }

    fun insert(dancer: Dancer) = viewModelScope.launch {
        refreshImpl()
        repository.insert(dancer)
    }

    fun delete(dancer: Dancer, fieldsSortAfter: String) = viewModelScope.launch {
        refreshImpl()
        repository.delete(dancer)
        if (!repository.dbImplRoom) {
            allDancers = repository.sort(fieldsSortAfter)}
    }

    fun update(dancer: Dancer) = viewModelScope.launch {
        refreshImpl()
        repository.update(dancer)
    }

    fun sort(fields: String) {
        refreshImpl()
        allDancers = repository.sort(fields)
    }

    private fun refreshImpl(){
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplication())
        repository.dbImplRoom = prefs.getString("db_impl_multilist",null) == getApplication<Application>().resources.getString(
            R.string.room_impl_name)
    }


}