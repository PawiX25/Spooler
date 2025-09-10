package com.pawix25.spooler.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SpoolDao {

    @Query("SELECT * FROM spools ORDER BY name ASC")
    fun getAllSpools(): Flow<List<Spool>>

    @Query("SELECT * FROM spools WHERE id = :spoolId")
    fun getSpoolById(spoolId: Int): Flow<Spool>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpool(spool: Spool)

    @Update
    suspend fun updateSpool(spool: Spool)

    @Query("SELECT * FROM print_jobs WHERE spoolId = :spoolId ORDER BY date DESC")
    fun getPrintJobsForSpool(spoolId: Int): Flow<List<PrintJob>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrintJob(printJob: PrintJob)
}
