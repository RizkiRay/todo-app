package com.example.todoapp.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.todoapp.data.db.entity.Task

@Dao
interface TaskDao {
    @Query("SELECT * FROM task")
    fun getAll(): List<Task>

    @Query("SELECT * FROM task WHERE name LIKE '%' || :name || '%'")
    fun searchTask(name: String?): List<Task>

    @Insert
    fun insert(task: Task): Long

    @Update
    fun updateTask(task: Task)

    @Delete
    fun delete(task: Task)
}