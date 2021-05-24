package com.example.simplydo.utli

import android.content.Context
import com.example.simplydo.localDatabase.AppDatabase
import com.example.simplydo.localDatabase.TodoDAO
import com.example.simplydo.model.TodoList
import java.util.concurrent.Callable
import java.util.concurrent.Executors


internal val TAG = Repository::class.java.canonicalName

class Repository private constructor(val context: Context, val appDatabase: AppDatabase) {


    private var db: TodoDAO = appDatabase.todoDao()



    companion object {

        @Volatile
        var instance: Repository? = null

        fun getInstance(context: Context, appDatabase: AppDatabase) =
            this.instance ?: synchronized(this) {
                this.instance ?: Repository(context, appDatabase).also { this.instance = it }
            }

    }

    fun registerUser() {

    }

    fun insertNewTodoTask(todoList: TodoList) {
        val thread = Thread {
            db.insert(todoList = todoList)
        }
        thread.start()
    }

    fun getTodoList(): ArrayList<TodoList> {
        val callable = Callable { db.getAll() }
        val future = Executors.newSingleThreadExecutor().submit(callable)
        return future!!.get() as ArrayList<TodoList>
    }

    fun deleteTaskByPosition(id: Int) {
        Thread {
            db.deleteTaskById(id)
        }.start()
    }



}