package com.example.todoapp.ui.home

import android.app.Application
import android.os.Parcelable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.db.AppDatabase
import com.example.todoapp.data.db.entity.Task
import com.example.todoapp.utils.StateDelegate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.parcelize.Parcelize

class HomeViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {
    @Parcelize
    data class State(
        val tasks: List<Task> = listOf(),
        val selectedTask: Task? = null,
        val keyword: String = ""
    ) : Parcelable

    private val taskDao = AppDatabase.invoke(application).TaskDao()
    private var state by StateDelegate(savedStateHandle, State())
    private var _uiState = MutableLiveData<State>()
    val uiState: LiveData<State> get() = _uiState

    fun getItems() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = taskDao.searchTask(state.keyword)
            withContext(Dispatchers.Main) {
                updateState(state.copy(tasks = result))
            }
        }
    }

    fun deleteItem(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            taskDao.delete(task)
            getItems()
        }
    }

    fun setTaskCompleted(task: Task, isCompleted: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            taskDao.updateTask(task.copy(isCompleted = isCompleted))
            getItems()
        }
    }

    fun setSelectedTask(task: Task?) {
        updateState(state.copy(selectedTask = task))
    }

    fun setKeyword(keyword: String) {
        updateState(state.copy(keyword = keyword))
        getItems()
    }

    private fun updateState(state: State) {
        this.state = state
        _uiState.value = this.state
    }
}