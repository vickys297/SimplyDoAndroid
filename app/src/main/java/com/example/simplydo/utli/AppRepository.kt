package com.example.simplydo.utli

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.simplydo.api.API
import com.example.simplydo.localDatabase.AppDatabase
import com.example.simplydo.localDatabase.TodoDAO
import com.example.simplydo.model.CommonResponseModel
import com.example.simplydo.model.RequestDataFromCloudResponseModel
import com.example.simplydo.model.TodoModel
import com.example.simplydo.model.TodoPagingModel
import com.example.simplydo.network.NoConnectivityException
import com.example.simplydo.network.RetrofitServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import javax.inject.Inject
import kotlin.collections.HashMap


internal val TAG = AppRepository::class.java.canonicalName

class AppRepository @Inject private constructor(
    val context: Context,
    val appDatabase: AppDatabase
) {
    private var db: TodoDAO = appDatabase.todoDao()

    companion object {
        fun getInstance(context: Context, appDatabase: AppDatabase): AppRepository {
            return AppRepository(context, appDatabase)
        }
    }


    /*
    * Room Database CRUD
    * ***************************
    * insertNewTodoTask -> Insert new task
    * deleteTaskByPosition -> Delete task by id
    * getTodoByEventDate -> List tasks by date
    * */


    fun insertNewTodoTask(todoModel: TodoModel): Long {
        val callable = Callable { db.insert(todoModel = todoModel) }
        val future = Executors.newSingleThreadExecutor().submit(callable)
        return future!!.get()
    }


    fun deleteTaskByPosition(item: TodoModel) {
        Thread {
            Log.d(TAG, "deleteTaskByPosition: ${db.deleteTaskById(item)}")
        }.start()
//        deleteTodoFromCloud(id)
    }


    private fun deleteTodoFromCloud(id: Long) {

        val retrofitServices = RetrofitServices.getInstance(context).createService(API::class.java)
//        val deleteTodoById = retrofitServices.delete

    }

    fun uploadNewTodo(
        todoModel: TodoModel,
        user_key: String,
        todoListResponseModel: MutableLiveData<CommonResponseModel>,
        noNetworkMessage: MutableLiveData<String>,
    ) {
        val retrofit = RetrofitServices.getInstance(context).createService(API::class.java)
        val insertTodo = retrofit.newEntryTodo(todoModel, user_key)

        insertTodo.enqueue(object : Callback<CommonResponseModel> {
            override fun onResponse(
                call: Call<CommonResponseModel>,
                responseModel: Response<CommonResponseModel>,
            ) {
                val data = responseModel.body()
                data?.let {
                    todoListResponseModel.postValue(data)
                }
            }

            override fun onFailure(call: Call<CommonResponseModel>, t: Throwable) {
                if (t is NoConnectivityException) {
                    // show No Connectivity message to user or do whatever you want.
                    noNetworkMessage.postValue("No network connection")
                }
            }

        })

    }


    fun uploadDataToCloudDatabase(todoLists: ArrayList<TodoModel>) {

        val retrofitServices =
            RetrofitServices.getInstance(context).createService(API::class.java)
        val uploadToDatabase = retrofitServices.uploadDataToCloudDatabase(
            todoLists,
            AppPreference.getPreferences(AppConstant.USER_KEY, context)
        )

        uploadToDatabase.enqueue(object : Callback<CommonResponseModel> {
            override fun onResponse(
                call: Call<CommonResponseModel>,
                response: Response<CommonResponseModel>,
            ) {
                val data = response.body()
                data?.let {
                    if (data.result == AppConstant.API_RESULT_OK) {
                        updateSynchronizedTodoData(todoLists)
                    }
                }
            }

            override fun onFailure(call: Call<CommonResponseModel>, t: Throwable) {
                if (t is NoConnectivityException) {
                    throw NoConnectivityException()
                }
            }

        })


    }

    private fun updateSynchronizedTodoData(todoModels: ArrayList<TodoModel>) {
        for (todo in todoModels) {
            Thread {
                db.updateSynchronizedTodoDataById(todo.dtId)
            }.start()
        }
    }


    private fun bulkInsertIntoLocalappdata(data: ArrayList<TodoModel>) {
        if (data.isNotEmpty()) {
            data.forEach {
                val callable = Callable { db.insert(it) }
                Thread {
                    db.updateSynchronizedTodoDataById(it.dtId)
                }.start()

                val thread = Executors.newSingleThreadExecutor().submit(callable)
                Log.d(TAG, "New Bulk insert -> ${thread.get()}")
            }
        }
    }

    fun downloadTaskByDate(dateString: String) {

        val hashMap = HashMap<String, String>()
        hashMap["eventDate"] = dateString

        val retrofitServices = RetrofitServices.getInstance(context).createService(API::class.java)
        val syncFromCloud =
            retrofitServices.syncFromCloudByDate(
                AppPreference.getPreferences(
                    AppConstant.USER_KEY,
                    context = context
                ), hashMap
            )

        syncFromCloud.enqueue(object : Callback<RequestDataFromCloudResponseModel> {
            override fun onResponse(
                call: Call<RequestDataFromCloudResponseModel>,
                response: Response<RequestDataFromCloudResponseModel>,
            ) {
                val data = response.body()
                data?.let {
                    if (it.result == AppConstant.API_RESULT_OK) {
                        synDataWithLocalDatabase(it.data)
                    }
                }
            }

            override fun onFailure(call: Call<RequestDataFromCloudResponseModel>, t: Throwable) {

            }

        })
    }

    private fun synDataWithLocalDatabase(data: ArrayList<TodoModel>) {
        if (data.isNotEmpty()) {
            data.forEach {
                val callable =
                    Callable {
                        db.getTodoByCreatedDateEventDate(
                            AppFunctions.getDateStringFromMilliseconds(
                                it.eventDate,
                                AppConstant.DATE_PATTERN_EVENT_DATE
                            ), it.createdAt
                        )
                    }
                val executors = Executors.newSingleThreadExecutor().submit(callable)

                val todo = executors.get()

                if (todo == null) {
                    it.synchronize = true
                    Thread {
                        db.insert(it)
                    }.start()
                }
            }
        }
    }

    fun getNextTaskAvailability(
        currentEventDateMax: Long,
        nextAvailableDate: MutableLiveData<TodoModel>,
    ) {

        val response = Executors.newSingleThreadExecutor().submit(Callable {
            db.getNextEventCountByDate(currentEventDateMax = currentEventDateMax)
        }).get()
        nextAvailableDate.postValue(response)
    }

    fun completeTaskById(dtId: Long): Int {
        val callable = Callable {
            db.completeTaskById(
                dtId,
                AppFunctions.dateFormatter(AppConstant.DATE_PATTERN_ISO).format(Date().time)
            )
        }
        return Executors.newSingleThreadExecutor().submit(callable).get()!!
    }

    inner class PastOrderDatasource(val currentTimeMillis: Long) : PagingSource<Int, TodoModel>() {
        override fun getRefreshKey(state: PagingState<Int, TodoModel>): Int? {
            return state.anchorPosition?.let { anchorPosition ->
                val anchorPage = state.closestPageToPosition(anchorPosition)
                anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
            }
        }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TodoModel> {
            try {
                val nextPageNumber = params.key ?: 0
                val response = getPastTaskData(nextPageNumber, currentTimeMillis.toString())
                Log.d(TAG, "load: ${response.data.size}")

                return LoadResult.Page(
                    data = response.data,
                    prevKey = null, // Only paging forward.
                    nextKey = if (response.nextPage == -1) null else response.nextPage
                )

            } catch (e: Exception) {
                throw e
            }
        }

    }


    private fun getPastTaskData(nextPageNumber: Int, currentTimeMillis: String): TodoPagingModel {
        val pageSize = 30

        val callable1 = Callable { db.getPastTaskPaging(nextPageNumber, pageSize, currentTimeMillis) }
        val executors1 = Executors.newSingleThreadExecutor().submit(callable1)

        val todoModelArrayList = executors1.get() as ArrayList<TodoModel>

        val callable2 = Callable { db.getPastTaskCount(nextPageNumber, pageSize, currentTimeMillis) }
        val executors2 = Executors.newSingleThreadExecutor().submit(callable2)

        val remainingCount = executors2.get().toInt()

        Log.d(TAG, "getCompletedTaskData: remainingCount--> $remainingCount")

        val incrementer: Int = if (remainingCount != 0) {
            nextPageNumber + pageSize
        } else {
            -1
        }

        return TodoPagingModel(todoModelArrayList, incrementer)
    }

    val getCompletedTask = object : PagingSource<Int, TodoModel>() {
        override fun getRefreshKey(state: PagingState<Int, TodoModel>): Int? {
            return state.anchorPosition?.let { anchorPosition ->
                val anchorPage = state.closestPageToPosition(anchorPosition)
                anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
            }
        }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TodoModel> {
            try {
                val nextPageNumber = params.key ?: 0
                val response = getCompletedTaskData(nextPageNumber)
                Log.d(TAG, "load: ${response.data.size}")

                return LoadResult.Page(
                    data = response.data,
                    prevKey = null, // Only paging forward.
                    nextKey = if (response.nextPage == -1) null else response.nextPage
                )

            } catch (e: Exception) {
                throw e
            }
        }
    }


    private fun getCompletedTaskData(nextPageNumber: Int): TodoPagingModel {

        Log.d(TAG, "getCompletedTaskData: InitialLoad Data -> $nextPageNumber")

        val pageSize = 30

        val callable1 = Callable { db.getCompletedTask(nextPageNumber, pageSize) }
        val executors1 = Executors.newSingleThreadExecutor().submit(callable1)

        val todoModelArrayList = executors1.get() as ArrayList<TodoModel>

        val callable2 = Callable { db.getCompletedTaskCount(nextPageNumber, pageSize) }
        val executors2 = Executors.newSingleThreadExecutor().submit(callable2)

        val remainingCount = executors2.get().toInt()

        Log.d(TAG, "getCompletedTaskData: remainingCount--> $remainingCount")

        val incrementer: Int = if (remainingCount != 0) {
            nextPageNumber + pageSize
        } else {
            -1
        }

        return TodoPagingModel(todoModelArrayList, incrementer)
    }



    fun getSelectedEventDateItemCount(
        startEventDate: Long,
        endEventDate: Long,
        selectedEventDateTotalCount: MutableLiveData<Int>
    ) {
        val response = Executors.newSingleThreadExecutor().submit(Callable {
            db.getSingleDayTodoListTotalCount(
                startEventDate,
                endEventDate
            )
        }).get()
        selectedEventDateTotalCount.postValue(response)
    }

    fun getTodoById(dtId: Long): TodoModel {
        return Executors.newSingleThreadExecutor().submit(Callable {
            db.getTodoById(dtId)
        }).get()
    }


}

