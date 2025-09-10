package com.pawix25.spooler.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "print_jobs",
    indices = [Index(value = ["spoolId"])],
    foreignKeys = [
        ForeignKey(
            entity = Spool::class,
            parentColumns = ["id"],
            childColumns = ["spoolId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PrintJob(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val spoolId: Int,
    val weight: Float,
    val date: Date
)
