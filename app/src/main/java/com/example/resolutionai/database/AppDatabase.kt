package com.example.resolutionai.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.resolutionai.database.dao.QrCodeDao
import com.example.resolutionai.database.data.QrCodeEntity

@Database(entities = [QrCodeEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun qrCodeDao(): QrCodeDao

    companion object {

        @Volatile
        var INSTANCE: AppDatabase? = null
        fun getDataBase(context: Context): AppDatabase{
            val tempInstance = INSTANCE
            if (tempInstance!=null)
                return tempInstance

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "stock_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }

}



