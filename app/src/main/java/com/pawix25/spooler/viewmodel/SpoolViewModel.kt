package com.pawix25.spooler.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pawix25.spooler.data.PrintJob
import com.pawix25.spooler.data.Spool
import com.pawix25.spooler.data.SpoolRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SpoolViewModel @Inject constructor(private val repository: SpoolRepository) : ViewModel() {

    val spools: StateFlow<List<Spool>> = repository.getAllSpools()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun getSpool(id: Int): StateFlow<Spool> = repository.getSpoolById(id)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Spool(name = "", material = "", color = "", remainingWeight = 0f, totalWeight = 0f)
        )

    fun getPrintJobsForSpool(spoolId: Int): StateFlow<List<PrintJob>> = repository.getPrintJobsForSpool(spoolId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addSpool(spool: Spool) {
        viewModelScope.launch {
            repository.insertSpool(spool)
        }
    }

    fun addPrintJob(printJob: PrintJob) {
        viewModelScope.launch {
            // First insert the print job
            repository.insertPrintJob(printJob)
            
            // Then update the spool's remaining weight
            val spool = repository.getSpoolById(printJob.spoolId).first()
            val newRemainingWeight = (spool.remainingWeight - printJob.weight).let { weight ->
                when {
                    !weight.isFinite() -> 0f
                    weight < 0f -> 0f
                    else -> weight
                }
            }
            val updatedSpool = spool.copy(
                remainingWeight = newRemainingWeight
            )
            repository.updateSpool(updatedSpool)
        }
    }
}
