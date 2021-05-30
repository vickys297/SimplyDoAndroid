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

    @Query("SELECT COUNT(*) FROM todoList")
    fun getTotalTaskCount(): LiveData<Int>

    @Query("SELECT * FROM todoList WHERE eventDate  =  :eventDate OR  eventDate > :eventDate AND isCompleted  = '1' OR isCompleted ='0' ORDER BY createdAt ASC")
    fun getTodoForQuickView(eventDate: String): LiveData<List<TodoModel>>

    @Query("SELECT * FROM todoList WHERE eventDate =:eventDate ")
    fun getTodoByEventDate(eventDate: String): LiveData<List<TodoModel>>

    @Query("UPDATE todoList SET isCompleted = '1', synchronize ='0' , updatedAt =:updatedAt WHERE dtId =:dtId")
    fun completeTaskById(dtId: Long, updatedAt: String)

    @Query("DELETE FROM todoList WHERE dtId =:id")
    fun deleteTaskById(id: Long)

    @Query("SELECT * FROM todoList WHERE synchronize ='0'")
    fun getNotSynchronizedTodoData(): List<TodoModel>

    @Query("SELECT * FROM todoList WHERE CAST(eventDate as DATE) > CAST(:date as DATE)")
    fun getNextEventCountByDate(date: String): List<TodoModel>


    /*Cloud Purpose
    * @getAllInDescOrder() to get the latest todoTask and sync with database
    *
    * @updateSynchronizedTodoDataById() update the synced to true */
    @Query("SELECT * FROM todoList WHERE eventDate =:eventDate AND createdAt =:createdAt")
    fun getTodoByCreatedDateEventDate(eventDate: String, createdAt: String): TodoModel?

    @Query("UPDATE todoList SET synchronize ='1' WHERE dtId =:id")
    fun updateSynchronizedTodoDataById(id: Long)

    @Query("SELECT * FROM todoList WHERE synchronize ='0'")
    fun getAllTodoNotSynced(): LiveData<List<TodoModel>>



}