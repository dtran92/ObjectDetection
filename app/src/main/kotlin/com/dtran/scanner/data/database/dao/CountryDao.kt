package com.dtran.scanner.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.dtran.scanner.data.database.model.CountryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CountryDao : BaseDao<CountryEntity> {

    @Query("SELECT * FROM CountryEntity")
    fun getAll(): Flow<List<CountryEntity>>

//    @Query("DELETE FROM DummyEntity WHERE :id = id")
//    fun deleteBettingTicket(id: Int) : LiveData<BettingTicketEntity>

    @Query("DELETE FROM CountryEntity")
    fun deleteAll()

}