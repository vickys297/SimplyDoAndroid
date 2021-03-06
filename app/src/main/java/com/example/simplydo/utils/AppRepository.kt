package com.example.simplydo.utils

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.simplydo.api.API
import com.example.simplydo.api.WorkspaceAPI
import com.example.simplydo.api.WorkspaceGroupAPI
import com.example.simplydo.api.network.NoConnectivityException
import com.example.simplydo.api.network.RetrofitServices
import com.example.simplydo.database.AppDatabase
import com.example.simplydo.database.dao.TodoDAO
import com.example.simplydo.model.*
import com.example.simplydo.model.entity.WorkspaceGroupModel
import com.example.simplydo.model.privateWorkspace.WorkspaceGroupTaskModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.Executors


internal val TAG = AppRepository::class.java.canonicalName

class AppRepository private constructor(
    private val context: Context,
    val appDatabase: AppDatabase
) {
    private var db: TodoDAO = appDatabase.todoDao()
    private var tagDb = appDatabase.tagDao()
    private var workspaceDb = appDatabase.workspaceDao()
    private var workspaceGroupDb = appDatabase.workspaceGroupDao()
    var workspaceGroupTaskDb = appDatabase.workspaceGroupTaskDao()

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


    fun insertTodoTask(todoModel: TodoModel): Long {
        val callable = Callable { db.insert(todoModel = todoModel) }
        val executor = Executors.newSingleThreadExecutor().submit(callable)
        return executor!!.get()
    }

    fun insertWorkspaceTodoTask(workspaceGroupTaskModel: WorkspaceGroupTaskModel): Long {
        val callable =
            Callable { workspaceGroupTaskDb.insertWorkspaceDatabase(workspaceGroupTaskModel = workspaceGroupTaskModel) }
        val executor = Executors.newSingleThreadExecutor().submit(callable)
        return executor!!.get()
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
                    // show No Connectivity message to account or do whatever you want.
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
                            AppFunctions.convertTimeInMillsecToPattern(
                                it.eventDateTime,
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

        val callable1 =
            Callable { db.getPastTaskPaging(nextPageNumber, pageSize, currentTimeMillis) }
        val executors1 = Executors.newSingleThreadExecutor().submit(callable1)

        val todoModelArrayList = executors1.get() as ArrayList<TodoModel>

        val callable2 =
            Callable { db.getPastTaskCount(nextPageNumber, pageSize, currentTimeMillis) }
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

    fun restoreTask(dtId: Long) {
        Thread {
            db.restoreTask(
                dtId,
                AppFunctions.dateFormatter(AppConstant.DATE_PATTERN_ISO).format(Date().time)
            )
        }.start()
    }

    fun checkPastTask(eventDate: Long): Int {
        return Executors.newSingleThreadExecutor().submit(Callable {
            db.checkPastTask(eventDate)
        }).get()
    }

    fun checkCompletedTask(currentDateEndTime: Long): Int {
        return Executors.newSingleThreadExecutor().submit(Callable {
            db.checkCompletedTask(currentDateEndTime)
        }).get()
    }

    fun updateTodoData(updateModel: TodoModel): Int {
        return Executors.newSingleThreadExecutor().submit(Callable {
            db.updateTaskData(updateModel)
        }).get()
    }

    fun getAvailableTags(): ArrayList<TagModel> {
        return Executors.newSingleThreadExecutor().submit(Callable {
            tagDb.getAllTag() as ArrayList<TagModel>
        }).get()
    }

    fun insertTag(tag: String) {
        Thread {
            tagDb.insertTag(TagModel(tag))
        }.start()
    }

    fun createNewWorkspace(data: WorkspaceModel) {
        workspaceDb.createNewWorkspace(data)
    }

    fun writeNewWorkspace(workspace: WorkspaceModel) {
        workspaceDb.createNewWorkspace(workspace)
    }

    fun getWorkspaceList(): ArrayList<WorkspaceModel> {
        return workspaceDb.getMyWorkspace() as ArrayList
    }

    fun getDummyTask(): ArrayList<TodoModel> {
        return db.getAllTodoList() as ArrayList
    }

    fun getParticipantList() {
        return
    }

    fun getParticipatesFromWorkspace(): ArrayList<AccountModel> {
        return workspaceDb.getUserFromWorkspace().accounts
    }

    fun insertNewWorkspaceGroup(newGroup: WorkspaceGroupModel) {
        val workspaceGroupId = workspaceGroupDb.insertNewWorkspaceGroup(newGroup)

        val callable = Callable { workspaceGroupDb.getWorkspaceGroupById(workspaceGroupId) }
        val workspaceGroupMode = Executors.newSingleThreadExecutor().submit(callable).get()

        Thread { syncNewWorkspaceGroup(workspaceGroupMode) }.start()
    }

    fun syncNewWorkspaceGroup(workspaceGroupModel: WorkspaceGroupModel) {
        val service = RetrofitServices.getInstance(context).createService(WorkspaceAPI::class.java)
        val request = service.syncWorkspaceGroup(workspaceGroupModel)
        request.enqueue(object : Callback<CommonResponseModel> {
            override fun onResponse(
                call: Call<CommonResponseModel>,
                response: Response<CommonResponseModel>
            ) {
            }

            override fun onFailure(call: Call<CommonResponseModel>, t: Throwable) {
            }

        })
    }

    fun getWorkspaceGroup(workspaceID: Long): PagingSource<Int, WorkspaceGroupModel> {
        return workspaceGroupDb.getWorkSpaceGroupByWorkspaceId(workspaceID)
    }

    fun createWorkspaceGroup(dataset: ArrayList<WorkspaceGroupModel>) {
        for (item in dataset) {
            workspaceGroupDb.insertNewWorkspaceGroup(item)
        }
    }

    fun getWorkspaceTaskByGroupId(groupTaskId: Long): PagingSource<Int, WorkspaceGroupTaskModel> {
        return workspaceGroupTaskDb.getAllWorkspaceGroupTask(groupTaskId)
    }

    fun getWorkspaceTaskByTaskId(dtId: Long): WorkspaceGroupTaskModel {
        return Executors.newSingleThreadExecutor().submit(Callable {
            workspaceGroupTaskDb.getWorkspaceGroupTaskById(dtId)
        }).get()
    }

    fun updateWorkspaceTaskData(workspaceGroupTaskModel: WorkspaceGroupTaskModel): Int {
        return Executors.newSingleThreadExecutor().submit(Callable {
            workspaceGroupTaskDb.updateWorkspaceTaskData(workspaceGroupTaskModel)
        }).get()
    }

    fun deleteWorkspaceTaskById(task: WorkspaceGroupTaskModel): Int {
        return workspaceGroupTaskDb.deleteWorkspaceTaskById(task)
    }

    fun getAllWorkspaceGroupTaskInLiveData(groupTaskId: Long): LiveData<List<WorkspaceGroupTaskModel>> {
        return workspaceGroupTaskDb.getAllWorkspaceGroupTaskInLiveData(groupTaskId)
    }

    fun getWorkspaceGroupTaskCount(workspaceID: Long): Int {
        return workspaceGroupDb.getWorkspaceGroupTaskCount(workspaceID)
    }

    fun workspaceSearchTask(
        searchFilterText: String,
        workspaceId: Long,
        groupId: Long
    ): PagingSource<Int, WorkspaceGroupTaskModel> {
        return workspaceGroupTaskDb.workspaceSearchTask(searchFilterText, workspaceId, groupId)
    }

    fun getApplicationContext(): Context {
        return context
    }

    fun deleteWorkspaceGroup(item: WorkspaceGroupModel) {
        workspaceGroupDb.deleteWorkspaceGroup(item)
    }

    fun updateWorkspaceGroup(workspaceGroupModel: WorkspaceGroupModel) {
        workspaceGroupDb.updateWorkspaceGroup(workspaceGroupModel)
    }

    suspend fun getParticipatesFromCloudWorkspace(): ArrayList<AccountModel> {
        val service =
            RetrofitServices.getInstance(context).createService(WorkspaceGroupAPI::class.java)
        val request = service.getPeopleList()

        return if (request.isSuccessful) {
            val responseData = request.body()!!
            responseData.data
        } else {
            arrayListOf()
        }

    }

}



