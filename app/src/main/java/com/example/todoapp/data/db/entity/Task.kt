package com.example.todoapp.data.db.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class Task(
    @PrimaryKey(autoGenerate = true) val uid: Long = 0,
    @ColumnInfo val name: String,
    @ColumnInfo val dueDate: String,
    @ColumnInfo val isCompleted: Boolean
) : Parcelable
