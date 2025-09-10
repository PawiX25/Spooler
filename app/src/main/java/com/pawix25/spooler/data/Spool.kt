
package com.pawix25.spooler.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "spools")
data class Spool(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val material: String,
    val color: String,
    val remainingWeight: Float,
    val totalWeight: Float
)
