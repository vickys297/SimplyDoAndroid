package com.example.simplydo.ui.activity.personalWorkspace.personalTask

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_DRAG
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.simplydo.R
import com.example.simplydo.adapters.PersonalTaskListAdapter
import com.example.simplydo.database.AppDatabase
import com.example.simplydo.databinding.FragmentQuickTodoBinding
import com.example.simplydo.dialog.bottomSheetDialogs.NewTaskOptionsBottomSheetDialog
import com.example.simplydo.dialog.bottomSheetDialogs.basicAddTodoDialog.AddNewRemainder
import com.example.simplydo.dialog.bottomSheetDialogs.basicAddTodoDialog.EditTodoBasic
import com.example.simplydo.dialog.bottomSheetDialogs.calenderOptions.TodoOptions
import com.example.simplydo.dialog.bottomSheetDialogs.todoOptions.TodoOptionsFragment
import com.example.simplydo.dialog.bottomSheetDialogs.workspaceDialog.WorkspaceSwitchBottomSheetDialog
import com.example.simplydo.model.AccountModel
import com.example.simplydo.model.CommonResponseModel
import com.example.simplydo.model.TodoModel
import com.example.simplydo.utils.*
import com.google.gson.Gson
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


internal val TAG = PersonalWorkspaceTaskFragment::class.java.canonicalName
internal const val CHANNEL_ID = "task_channel_id"
internal const val NOTIFICATION_ID = 8888

class PersonalWorkspaceTaskFragment : Fragment(R.layout.fragment_quick_todo), View.OnClickListener {


    private lateinit var userData: AccountModel
    private lateinit var todoObserver: Observer<CommonResponseModel>
    private lateinit var totalTaskCountObserver: Observer<Int>
    private lateinit var noNetworkObserver: Observer<String>

    private lateinit var viewModel: PersonalWorkspaceTaskViewViewModel

    private lateinit var _binding: FragmentQuickTodoBinding
    private val binding get() = _binding

    private lateinit var personalTaskListAdapter: PersonalTaskListAdapter

    private lateinit var recentSelectedItem: TodoModel

    private lateinit var newTaskOptionsBottomSheetDialog: NewTaskOptionsBottomSheetDialog
    private lateinit var todoOptions: TodoOptions

    private lateinit var workspaceSwitchBottomSheetDialog: WorkspaceSwitchBottomSheetDialog


    private val undoInterface = object : UndoInterface {
        override fun onUndo(task: Any, type: Int) {
            when (type) {
                AppConstant.Task.TASK_ACTION_RESTORE -> {
                    viewModel.undoTaskRemove(task as TodoModel)
                }
            }
        }
    }

    private val newTaskOptionsCallback: AppInterface.NewTaskDialogCallback = object :
        AppInterface.NewTaskDialogCallback {
        override fun onOptionSelected(option: Int) {
            when (option) {
                1 -> {
                    AddNewRemainder.newInstance(
                        appInterface,
                        System.currentTimeMillis()
                    ).show(
                        requireActivity().supportFragmentManager,
                        AddNewRemainder::class.java.canonicalName
                    )
                }
                2 -> {
                    findNavController().navigate(R.id.action_toDoFragment_to_addNewTodo)
                }
            }
        }

        override fun onClose() {

        }
    }


    private val editBasicTodoInterface = object : EditBasicTodoInterface {
        override fun onUpdateDetails(todoModel: TodoModel) {
            viewModel.updateTaskModel(todoModel)
        }

        override fun onAddMoreDetails(todoModel: TodoModel) {
            // show edit fragment
            val bundle = Bundle()
            bundle.putSerializable(AppConstant.NAVIGATION_TASK_DATA_KEY, todoModel)
            findNavController().navigate(R.id.action_toDoFragment_to_editFragment, bundle)
        }
    }

    private val todoOptionDialogFragments = object : TodoOptionDialogFragments {
        override fun onDelete(item: TodoModel) {
            deleteSingleTask(item)
        }

        override fun onEdit(item: TodoModel) {
            recentSelectedItem = item

            if (item.taskType == AppConstant.Task.TASK_TYPE_BASIC) {
                // show basic edit
                EditTodoBasic.newInstance(requireContext(), editBasicTodoInterface, item)
                    .show(requireActivity().supportFragmentManager, "dialog")
            }

            if (item.taskType == AppConstant.Task.TASK_TYPE_EVENT) {
                // show edit fragment
                val bundle = Bundle()
                bundle.putSerializable(AppConstant.NAVIGATION_TASK_DATA_KEY, item)
                findNavController().navigate(R.id.action_toDoFragment_to_editFragment, bundle)
            }

        }

        override fun onRestore(item: TodoModel) {
            recentSelectedItem = item
            viewModel.restoreTask(item.dtId)
            AppFunctions.showSnackBar(
                task = item,
                view = binding.root,
                message = "Task Restored",
                actionButtonName = "Undo",
                type = AppConstant.Task.TASK_ACTION_RESTORE,
                undoInterface = undoInterface
            )
        }
    }

    private fun deleteSingleTask(item: TodoModel) {
        recentSelectedItem = item
        viewModel.removeTask(item)
        AppFunctions.showSnackBar(
            task = item,
            view = binding.root,
            message = "Task Removed",
            actionButtonName = "Undo",
            type = AppConstant.Task.TASK_ACTION_RESTORE,
            undoInterface = undoInterface
        )
    }

    private val todoTaskOptionsInterface = object : TodoTaskOptionsInterface {
        override fun onCalenderView() {
            findNavController().navigate(R.id.action_toDoFragment_to_calenderFragment)
        }

        override fun onCompletedView() {
            val bundle = Bundle()
            bundle.putInt(getString(R.string.view_type), AppConstant.TODO_VIEW_COMPLETED)
            findNavController().navigate(R.id.action_toDoFragment_to_otherTodoFragment, bundle)
        }

        override fun onPastTaskView() {
            val bundle = Bundle()
            bundle.putInt(getString(R.string.view_type), AppConstant.TODO_VIEW_PAST)
            findNavController().navigate(R.id.action_toDoFragment_to_otherTodoFragment, bundle)
        }

        override fun onSettingsClicked() {
            findNavController().navigate(R.id.action_toDoFragment_to_mySettingActivity)
        }

    }

    private var todoAdapterInterface = object : TodoItemInterface {
        override fun onLongClick(item: TodoModel) {
            recentSelectedItem = item
            val options =
                TodoOptionsFragment.newInstance(requireContext(), todoOptionDialogFragments, item)
            options.show(requireActivity().supportFragmentManager, "dialog")
        }

        override fun onTaskClick(
            item: TodoModel,
            absoluteAdapterPosition: Int
        ) {
            val bundle = Bundle()
            bundle.putLong(getString(R.string.TODO_ITEM_KEY), item.dtId)
            findNavController().navigate(
                R.id.action_toDoFragment_to_todoFullDetailsFragment,
                bundle
            )
        }

    }
    private var appInterface = object : NewRemainderInterface {

        override fun onAddMoreDetails(eventDate: Long) {
            val bundle = Bundle()
            bundle.putLong(getString(R.string.eventDateString), eventDate)
            findNavController().navigate(R.id.action_toDoFragment_to_addNewTodo, bundle)
        }

        override fun onCreateTodo(
            title: String,
            task: String,
            eventDate: Long,
            isPriority: Boolean,
            isAllDayTask: Boolean
        ) {
            val newInert = viewModel.createNewTodo(
                title,
                task,
                eventDate,
                isPriority,
            )

            newInert.let {
                val bundle = Bundle()
                bundle.putLong("dtId", it)
                bundle.putString("title", title)
                bundle.putString("task", task)
                bundle.putBoolean("priority", isPriority)
                AppFunctions.showSnackBar(binding.root, "New task added")
                AppFunctions.setupNotification(it, eventDate, bundle, requireActivity())
            }

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentQuickTodoBinding.bind(view)

        userData = Gson().fromJson(
            AppPreference.getPreferences(
                AppConstant.Preferences.USER_DATA,
                requireContext()
            ), AccountModel::class.java
        )

        Glide
            .with(requireActivity())
            .load(userData.profilePicture)
            .into(binding.profileImageView2)


//        (activity as PersonalWorkspaceActivity).setSupportActionBar(binding.todoToolbar)
//        (activity as PersonalWorkspaceActivity).supportActionBar!!.setDisplayShowTitleEnabled(false)
//        setHasOptionsMenu(true)

        val accentText = "<font color='#6200EE'>Daily<br>Routine</font>"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            binding.text1.text =
                Html.fromHtml("Discover your $accentText here", HtmlCompat.FROM_HTML_MODE_LEGACY)
        } else {
            binding.text1.text = Html.fromHtml("Discover your $accentText here")
        }

        setObserver()
        setupViewModel()

        workspaceSwitchBottomSheetDialog =
            WorkspaceSwitchBottomSheetDialog.newInstance(
                requireContext(),
                callback = object : AppInterface.MyWorkspace.CreateWorkspaceDialog {
                    override fun onCreateNewWorkspace() {
                        findNavController().navigate(R.id.action_toDoFragment_to_createWorkspaceFragment)
                    }
                })

        // init
        newTaskOptionsBottomSheetDialog =
            NewTaskOptionsBottomSheetDialog.getInstance(requireContext(), newTaskOptionsCallback)
        todoOptions = TodoOptions.getInstance(requireContext(), todoTaskOptionsInterface)

        personalTaskListAdapter = PersonalTaskListAdapter(todoAdapterInterface, requireContext())
        binding.recyclerViewTodoList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = personalTaskListAdapter
        }

        binding.buttonAddNewTask.setOnClickListener(this@PersonalWorkspaceTaskFragment)
        binding.buttonTodoOption.setOnClickListener(this@PersonalWorkspaceTaskFragment)
        binding.profileImageView2.setOnClickListener(this@PersonalWorkspaceTaskFragment)
        binding.profileView.setOnClickListener {
            workspaceSwitchBottomSheetDialog.show(
                requireActivity().supportFragmentManager,
                WorkspaceSwitchBottomSheetDialog::class.java.canonicalName
            )
        }


        val simpleItemTouchCallback: ItemTouchHelper.SimpleCallback = object :
            ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            ) {

            override fun isLongPressDragEnabled(): Boolean {
                return true
            }


            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder,
            ): Boolean {
                val from = viewHolder.absoluteAdapterPosition
                val to = target.absoluteAdapterPosition

                val item1 = personalTaskListAdapter.getItemAtPosition(from)
                val item2 = personalTaskListAdapter.getItemAtPosition(to)

                if (item1 != null && item2 != null) {
//                    personalTaskListAdapter.notifyItemMoved(from, to)
                    return true
                }
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                //Remove swiped item from list and notify the RecyclerView
                val position = viewHolder.absoluteAdapterPosition
                personalTaskListAdapter.getItemAtPosition(position)?.let {
                    personalTaskListAdapter.notifyItemRemoved(position)

                    if (it.eventDateTime < AppFunctions.getCurrentDayStartInMilliSeconds()) {
                        personalTaskListAdapter.notifyItemRemoved(position)
                    }

                    if (!it.isCompleted)
                        viewModel.completeTaskByID(it.dtId).let {
                            AppFunctions.showSnackBar(
                                binding.root,
                                getString(R.string.task_completed_label)
                            )
                        }
                    if (it.isCompleted) {
                        deleteSingleTask(it)
                    }
                }
            }

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)
                if (actionState == ACTION_STATE_DRAG) {
                    viewHolder?.itemView?.alpha = 0.5f
                }
            }

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                super.clearView(recyclerView, viewHolder)
                viewHolder.itemView.alpha = 1.0f

                // Don't need to do anything here
            }
        }

        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerViewTodoList)


        
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.todo_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuSettings -> {
                findNavController().navigate(R.id.action_toDoFragment_to_mySettingActivity)
//                val intent = Intent(requireContext(), SettingsActivity::class.java)
//                requireContext().startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setObserver() {
        todoObserver = Observer {
            if (it.result == AppConstant.API_RESULT_OK) {
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
            }
        }
        noNetworkObserver = Observer {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        }
        totalTaskCountObserver = Observer {
            if (it > 0) {
                binding.recyclerViewTodoList.visibility = View.VISIBLE
                binding.noTaskAvailable.linearLayoutEmptyTask.visibility = View.GONE
            } else {
                binding.noTaskAvailable.linearLayoutEmptyTask.visibility = View.VISIBLE
                binding.recyclerViewTodoList.visibility = View.GONE
            }
        }
    }

    private fun setupViewModel() {
        viewModel =
            ViewModelProvider(
                this,
                ViewModelFactory(
                    requireActivity(),
                    AppRepository.getInstance(
                        requireActivity(),
                        AppDatabase.getInstance(
                            context = requireActivity()
                        )
                    )
                )
            )[PersonalWorkspaceTaskViewViewModel::class.java]

        binding.apply {
            personalViewModel = this@PersonalWorkspaceTaskFragment.viewModel
            lifecycleOwner = this@PersonalWorkspaceTaskFragment
            executePendingBindings()
        }


        viewModel.todoListResponse.observe(viewLifecycleOwner, todoObserver)
        viewModel.noNetworkMessage.observe(viewLifecycleOwner, noNetworkObserver)
        viewModel.getTotalTaskCount(AppFunctions.getCurrentDayStartInMilliSeconds())
            .observe(viewLifecycleOwner, totalTaskCountObserver)


    }

    override fun onResume() {
        super.onResume()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getQuickTodoList(AppFunctions.getCurrentDayStartInMilliSeconds())
                .collect {
                    personalTaskListAdapter.submitData(it)
                }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.buttonAddNewTask.id -> {
                newTaskOptionsBottomSheetDialog.show(
                    requireActivity().supportFragmentManager,
                    NewTaskOptionsBottomSheetDialog::class.java.canonicalName
                )
            }
            binding.buttonTodoOption.id -> {
                todoOptions.show(
                    requireActivity().supportFragmentManager,
                    TodoOptions::class.java.canonicalName
                )
            }
            binding.profileImageView2.id -> {
                findNavController().navigate(R.id.action_toDoFragment_to_mySettingActivity)
            }
        }
    }
}