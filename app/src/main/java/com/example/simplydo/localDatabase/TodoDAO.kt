package com.example.simplydo.localDatabase

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import com.example.simplydo.model.TodoModel

@Dao
interface TodoDAO {

    /*
    * Application Purpose
    * */


    //    get tasks from current date to upcoming date in quick view
    @Query("SELECT * FROM todoList WHERE eventDate >= :eventDate OR isCompleted = '0' ORDER BY eventDate ASC")
    fun getQuickTodoList(eventDate: Long): PagingSource<Int, TodoModel>

    @Query("SELECT * FROM todoList WHERE eventDate >= :startEventDate AND eventDate <= :endEventDate ORDER BY eventDate ASC")
    fun getSingleDayTodoList(
        startEventDate: Long,
        endEventDate: Long,
    ): PagingSource<Int, TodoModel>


    //      get next quick task count
    @Query("SELECT COUNT(*) FROM todoList WHERE eventDate >= :eventDate OR isCompleted = '0' ORDER BY eventDate ASC LIMIT :pageSize OFFSET :nextPageNumber")
    fun getQuickTodoListCount(eventDate: Long, nextPageNumber: Int, pageSize: Int): Int


    @Query("SELECT * FROM todoList WHERE dtId = :dtId")
    fun getTodoById(dtId: Long): TodoModel


    @Query("SELECT COUNT(*) FROM todoList WHERE eventDate >= :startEventDate AND  eventDate <= :endEventDate ORDER BY eventDate ASC LIMIT :pageSize OFFSET :nextPageNumber")
    fun getSingleDayTodoListCount(
        startEventDate: Long,
        endEventDate: Long,
        nextPageNumber: Int,
        pageSize: Int
    ): Int

    @Query("SELECT COUNT(*) FROM todoList WHERE eventDate >= :startEventDate AND  eventDate <= :endEventDate")
    fun getSingleDayTodoListTotalCount(
        startEventDate: Long,
        endEventDate: Long,
    ): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(todoModel: TodoModel): Long

    //    Get total task count
    @Query("SELECT COUNT(*) FROM todoList")
    fun getTotalTaskCount(): LiveData<Int>

    //    get past task with paging
    @Query("SELECT * FROM todoList WHERE eventDate < :currentTimeMillis LIMIT :pageSize OFFSET :nextPageNumber")
    fun getPastTaskPaging(
        nextPageNumber: Int,
        pageSize: Int,
        currentTimeMillis: String
    ): List<TodoModel>

    //    get past task count
    @Query("SELECT COUNT(*) FROM todoList WHERE eventDate < :currentTimeMillis LIMIT :pageSize OFFSET :nextPageNumber")
    fun getPastTaskCount(nextPageNumber: Int, pageSize: Int, currentTimeMillis: String): Long

    //    get task completed task
    @Query("SELECT * FROM todoList WHERE isCompleted = '1' LIMIT :pageSize OFFSET :nextPageNumber")
    fun getCompletedTask(nextPageNumber: Int, pageSize: Int): List<TodoModel>

    //    get task completed task count
    @Query("SELECT COUNT(*) FROM todoList WHERE isCompleted = '1' LIMIT :pageSize OFFSET :nextPageNumber")
    fun getCompletedTaskCount(nextPageNumber: Int, pageSize: Int): Long


    //    get task on current date
    @Query("SELECT * FROM todoList WHERE eventDate >= :starEventDate AND eventDate <= :endEventDate")
    fun getTodoByEventDate(starEventDate: Long, endEventDate: Long): LiveData<List<TodoModel>>

    //    complete task by dtid
    @Query("UPDATE todoList SET isCompleted = '1', synchronize ='0' , updatedAt =:updatedAt WHERE dtId =:dtId")
    fun completeTaskById(dtId: Long, updatedAt: String)

    //    delete task by dtid
    @Delete
    fun deleteTaskById(item: TodoModel): Int

    //    get not synced data with cloud server
    @Query("SELECT * FROM todoList WHERE synchronize ='0'")
    fun getNotSynchronizedTodoData(): List<TodoModel>

    //    get task count on upcoming event date
    @Query("SELECT * FROM todoList WHERE eventDate >= :currentEventDateMax limit 1")
    fun getNextEventCountByDate(currentEventDateMax: Long): List<TodoModel>


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