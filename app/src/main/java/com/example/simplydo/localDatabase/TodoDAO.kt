package com.example.simplydo.localDatabase

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.simplydo.model.TodoList

@Dao
interface TodoDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(todoList: TodoList)

    @Query("SELECT * FROM todoList")
    fun getAllTodo(): LiveData<List<TodoList>>

    @Query("SELECT * FROM todoList")
    fun getAll(): List<TodoList>

    @Query("DELETE FROM todoList WHERE dtId =:id")
    fun deleteTaskById(id: Int)
}