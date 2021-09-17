package com.drozdova.dancersbase.utils

import com.drozdova.dancersbase.database.Dancer

interface DancerListener {
    fun deleteDancer(dancer: Dancer)
    fun updateDancer(dancer: Dancer)
}