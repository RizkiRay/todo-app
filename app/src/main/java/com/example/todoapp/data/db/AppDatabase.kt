package com.example.todoapp.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.todoapp.data.db.dao.TaskDao
import com.example.todoapp.data.db.entity.Task

@Database(
    entities = [Task::class],
    version = 1
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun TaskDao(): TaskDao
    companion object {
        @Volatile
        private var instance: AppDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK){
            instance ?: buildDatabase(context).also { instance = it}
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(context, AppDatabase::class.java, "todo.db").build()
    }
}