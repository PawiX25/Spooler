package com.pawix25.spooler.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SpoolRepository @Inject constructor(private val spoolDao: SpoolDao) {

    fun getAllSpools(): Flow<List<Spool>> = spoolDao.getAllSpools()

    fun getSpoolById(spoolId: Int): Flow<Spool> = spoolDao.getSpoolById(spoolId)

    suspend fun insertSpool(spool: Spool) {
        spoolDao.insertSpool(spool)
    }

    suspend fun updateSpool(spool: Spool) {
        spoolDao.updateSpool(spool)
    }

    fun getPrintJobsForSpool(spoolId: Int): Flow<List<PrintJob>> = spoolDao.getPrintJobsForSpool(spoolId)

    suspend fun insertPrintJob(printJob: PrintJob) {
        spoolDao.insertPrintJob(printJob)
    }
}
