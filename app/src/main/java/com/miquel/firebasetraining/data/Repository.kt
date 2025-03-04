package com.miquel.firebasetraining.data

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class Repository {
    val db= Firebase.firestore
    companion object {
        @Volatile
        private var INSTANCE: Repository? = null

        fun getInstance(): Repository {
            return INSTANCE ?: synchronized(this) {
                val instance = Repository()
                INSTANCE = instance
                instance
            }
        }
    }
    suspend fun updateTask(task: TaskItem) {
        try {
            db.collection("tasks")
                .document(task.id!!)
                .set(task)
                .await()
        } catch (exception: Exception) {
            Log.w("updateTask", "Error updating document ${task.id}.", exception)
        }
    }

    suspend fun getTasksForUser(userId: String): List<TaskItem> {
        return try {
            val result = db.collection("tasks")
                .whereEqualTo("assignedUser", userId)
                .get()
                .await()

            val tasks = mutableListOf<TaskItem>()
            for (document in result) {
                val task = document.toObject(TaskItem::class.java)
                task.id = document.id
                tasks.add(task)
            }

            tasks.sortedBy { it.date }
        } catch (exception: Exception) {
            Log.w("getTaskForUser", "Error getting documents.", exception)
            emptyList() // Return an empty list on error
        }
    }
    suspend fun getTasksForUserLen(userId: String): Int {
        return try {
            getTasksForUser(userId).size
        } catch (e: Exception){0}
    }

    suspend fun addTask(task: TaskItem): String? {
        try {
           val documentReference = db.collection("tasks")
                .add(task)
                .await()
            task.id= documentReference.id
            return documentReference.id
        } catch (exception: Exception) {
            Log.w("addTask", "Error adding document.", exception)

        }
        return task.id

    }

}