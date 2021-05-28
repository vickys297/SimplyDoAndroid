package com.example.simplydo.utli

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.simplydo.api.API
import com.example.simplydo.localDatabase.AppDatabase
import com.example.simplydo.localDatabase.TodoDAO
import com.example.simplydo.model.CommonResponseModel
import com.example.simplydo.model.RequestDataFromCloudResponseModel
import com.example.simplydo.model.TodoModel
import com.example.simplydo.network.NoConnectivityException
import com.example.simplydo.network.RetrofitServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import kotlin.collections.HashMap


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


    fun deleteTaskByPosition(id: Long) {
        Thread {
            db.deleteTaskById(id)
        }.start()
        deleteTodoFromCloud(id)
    }

    fun getTodoByEventDate(
        date: String
    ) {
        Thread{
            db.getTodoByEnetDate(date)
        }.start()

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


    fun uploadDataToCloudDatabase() {

        val callable = Callable { db.getNotSynchronizedTodoData() }
        val thread = Executors.newSingleThreadExecutor().submit(callable)
        val todoLists = thread.get() as ArrayList<TodoModel>

        if (todoLists.isNotEmpty()) {
            val retrofitServices =
                RetrofitServices.getInstance(context).createService(API::class.java)
            val uploadToDatabase = retrofitServices.uploadDataToCloudDatabase(todoLists,
                Session.getSession(Constant.USER_KEY, context))
            uploadToDatabase.enqueue(object : Callback<CommonResponseModel> {
                override fun onResponse(
                    call: Call<CommonResponseModel>,
                    response: Response<CommonResponseModel>,
                ) {
                    val data = response.body()
                    data?.let {
                        if (data.result == Constant.API_RESULT_OK) {
                            updateSynchronizedTodoData(todoLists)
                        }
                    }
                }

                override fun onFailure(call: Call<CommonResponseModel>, t: Throwable) {

                }

            })
        }


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
                Log.i(TAG, "New Bulk insert -> ${thread.get()}")
            }
        }
    }

    fun downloadTaskByDate(dateString: String) {

        val hashMap = HashMap<String, String>()
        hashMap["eventDate"] = dateString

        val retrofitServices = RetrofitServices.getInstance(context).createService(API::class.java)
        val syncFromCloud =
            retrofitServices.syncFromCloudByDate(
                Session.getSession(Constant.USER_KEY,
                context = context), hashMap)

        syncFromCloud.enqueue(object : Callback<RequestDataFromCloudResponseModel> {
            override fun onResponse(
                call: Call<RequestDataFromCloudResponseModel>,
                response: Response<RequestDataFromCloudResponseModel>,
            ) {
                val data = response.body()
                data?.let {
                    if (it.result == Constant.API_RESULT_OK) {
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
                    Callable { db.getTodoByCreatedDateEventDate(it.eventDate, it.createdAt) }
                val executors = Executors.newSingleThreadExecutor().submit(callable)

                val todo = executors.get()

                if (todo == null) {
                    it.synchronize = 1
                    Thread {
                        db.insert(it)
                    }.start()
                }
            }
        }
    }

    fun getNextTaskAvailability(
        selectedEventDate: String,
        nextAvailableDate: MutableLiveData<List<TodoModel>>
    ) {
        val callable = Callable {
            db.getNextEventCountByDate(date = selectedEventDate)
        }

        val executors = Executors.newSingleThreadExecutor().submit(callable)
        nextAvailableDate.postValue(executors.get())
    }

    fun completeTaskById(dtId: Long) {
        Thread{
            db.completeTaskById(dtId, Constant.dateFormatter(Constant.DATE_PATTERN_ISO).format(Date().time))
        }.start()
    }


}