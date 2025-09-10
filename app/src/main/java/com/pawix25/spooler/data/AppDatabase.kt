package com.pawix25.spooler.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Spool::class, PrintJob::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun spoolDao(): SpoolDao

    companion object {
        const val DATABASE_NAME = "spooler-db"
    }
}
