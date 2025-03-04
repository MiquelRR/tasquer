package com.miquel.firebasetraining.presentation.home

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miquel.firebasetraining.data.Repository
import com.miquel.firebasetraining.data.TaskItem
import com.miquel.firebasetraining.data.localDateToDate
import kotlinx.coroutines.launch
import java.time.LocalDate

class TaskViewModel(private val repository: Repository = Repository.getInstance()) : ViewModel() {

    var allTasks by mutableStateOf<List<TaskItem>>(emptyList())
        private set
    var taskList by mutableStateOf<List<TaskItem>>(emptyList())
        private set
    var filter by mutableStateOf("all") // Estado del filtro, valores posibles: "all" o "today"
        private set

    var amount by mutableStateOf(0)
        private set

    @RequiresApi(Build.VERSION_CODES.O)
    fun getTasksForUser(userId: String) {
        viewModelScope.launch {
            allTasks = repository.getTasksForUser(userId)
            amount = allTasks.size
            taskList = applyFilter(allTasks) // Filtrar de acuerdo al estado actual
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getTasksAmountForUser(userId: String?){
        Log.d("TaskViewModel", "Obteniendo cantidad de tareas para el usuario: $userId -> $amount")
        if (userId != null){
        viewModelScope.launch {
        amount = repository.getTasksForUserLen(userId!!)
    }}}

    @RequiresApi(Build.VERSION_CODES.O)
    private fun applyFilter(tasks: List<TaskItem>): List<TaskItem> {
        return if (filter == "Today") {
            val tomorrow = localDateToDate( LocalDate.now().plusDays(1))
            Log.d("TaskViewModel", "Filtrando tareas para hoy: $tomorrow")
            tasks.filter { task ->
                task.date?.toDate()?.before(tomorrow) == true
            }
        } else {
            allTasks
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateFilter(newFilter: String) {
        Log.d("TaskViewModel", "Actualizando filtro a: $newFilter")
        filter = newFilter
        taskList = applyFilter(taskList) // Filtrar la lista actual
    }



}