package com.example.simplydo.localDatabase

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.simplydo.model.TodoModel

@Dao
interface TodoDAO {

    /*
    * Application Purpose
    * */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(todoModel: TodoModel): Long

    @Query("SELECT * FROM todoList ORDER BY createdAt ASC")
    fun getAllTodo(): LiveData<List<TodoModel>>


    @Query("DELETE FROM todoList WHERE dtId =:id")
    fun deleteTaskById(id: Long)

    @Query("SELECT * FROM todoList WHERE synchronize ='0'")
    fun getNotSynchronizedTodoData(): List<TodoModel>


    /*Cloud Purpose
    * @getAllInDescOrder() to get the latest todoTask and sync with database
    *
    * @updateSynchronizedTodoDataById() update the synced to true */
    @Query("SELECT * FROM todoList WHERE eventDate =:eventDate AND createdAt =:createdAt")
    fun getTodoByCreatedDateEventDate(eventDate: String, createdAt: String): TodoModel?

    @Query("UPDATE todoList SET synchronize ='1' WHERE dtId =:id")
    fun updateSynchronizedTodoDataById(id: Long)

    @Query("SELECT * FROM todoList WHERE eventDate =:eventDate ")
    fun getTodoByEnetDate(eventDate: String): List<TodoModel>
}