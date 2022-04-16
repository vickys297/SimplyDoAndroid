package com.example.simplydo.localDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.simplydo.localDatabase.dao.*
import com.example.simplydo.model.TagModel
import com.example.simplydo.model.TodoModel
import com.example.simplydo.model.WorkspaceModel
import com.example.simplydo.model.entity.WorkspaceGroupModel
import com.example.simplydo.model.privateWorkspace.WorkspaceGroupTaskModel
import com.example.simplydo.utlis.AppConstant

@Database(
    entities = [
        TodoModel::class,
        TagModel::class,
        WorkspaceModel::class,
        WorkspaceGroupModel::class,
        WorkspaceGroupTaskModel::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(ConverterHelper::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDAO
    abstract fun tagDao(): TagDAO
    abstract fun workspaceDao(): WorkspaceDAO
    abstract fun workspaceGroupDao(): WorkspaceGroupDAO
    abstract fun workspaceGroupTaskDao(): WorkspaceGroupTaskDAO

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context) =
            instance ?: synchronized(this) {
                instance ?: database(context).also { instance = it }
            }

        private fun database(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, AppConstant.DATABASE_NAME)
                .build()
        }
    }
}