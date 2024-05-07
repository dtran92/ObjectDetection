package com.dtran.scanner.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dtran.scanner.data.database.dao.CountryDao
import com.dtran.scanner.data.database.model.CountryEntity

@Database(
    entities = [CountryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract val countryDao: CountryDao

}