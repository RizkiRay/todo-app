package com.example.todoapp.ui.task.add

import android.app.Application
import android.os.Bundle
import android.os.Parcelable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.db.AppDatabase
import com.example.todoapp.data.db.entity.Task
import com.example.todoapp.utils.StateDelegate
import com.example.todoapp.utils.toDate
import com.example.todoapp.utils.toISODate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.parcelize.Parcelize
import java.util.Date

class AddTaskViewModel(application: Application, savedStateHandle: SavedStateHandle) :
    AndroidViewModel(application) {
    @Parcelize
    data class State(
        val date: Date? = null,
        val title: String = "",
        val task: Task? = null,
        val insertedTask: Task? = null,
        val isSaved: Boolean = false,
        val isUpdate: Boolean = false
    ) : Parcelable

    private val taskDao = AppDatabase.invoke(application).TaskDao()
    private var state by StateDelegate(savedStateHandle, State())
    private var _uiState = MutableLiveData<State>()
    val uiState: LiveData<State> get() = _uiState

    fun extractArguments(bundle: Bundle?) {
        val task = bundle?.getParcelable<Task>(KEY_TASK)
        updateState(
            state.copy(
                date = task?.dueDate?.toDate(),
                title = task?.name.orEmpty(),
                task = task
            )
        )
    }

    fun setDateTime(date: Date) {
        updateState(state.copy(date = date))
    }

    fun setTitle(title: String) {
        state = state.copy(title = title)
    }

    fun saveOrUpdateTask() {
        state.task?.let { updateTask() } ?: run { saveTask() }
    }

    private fun saveTask() {
        val task = Task(
            name = state.title,
            dueDate = state.date?.toISODate().orEmpty(),
            isCompleted = false
        )
        viewModelScope.launch(Dispatchers.IO) {
            val id = taskDao.insert(task)
            withContext(Dispatchers.Main) {
                updateState(state.copy(isSaved = true, insertedTask = task.copy(uid = id)))
            }
        }
    }

    private fun updateTask() {
        state.task?.let {
            val task = it.copy(name = state.title, dueDate = state.date?.toISODate().orEmpty())
            viewModelScope.launch(Dispatchers.IO) {
                taskDao.updateTask(task)
                withContext(Dispatchers.Main) {
                    updateState(state.copy(isSaved = true))
                }
            }
        }
    }

    private fun updateState(state: State) {
        this.state = state
        _uiState.value = this.state
    }

    companion object {
        const val KEY_TASK = "KEY_TASK"
    }
}