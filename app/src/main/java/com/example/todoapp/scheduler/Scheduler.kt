package com.example.todoapp.scheduler

import com.example.todoapp.data.db.entity.Task

interface Scheduler {
    fun schedule(task: Task)
    fun cancel(task: Task)
}