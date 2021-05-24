package com.example.simplydo.localDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.simplydo.model.TodoList
import com.example.simplydo.utli.Constant

@Database(entities = [TodoList::class], version = 1, exportSchema = false)
@TypeConverters(ConverterHelper::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDAO

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context) =
            instance ?: synchronized(this) {
                instance ?: database(context).also { instance = it }
            }

        private fun database(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, Constant.DATABASE_NAME)
                .build()
        }


    }
}