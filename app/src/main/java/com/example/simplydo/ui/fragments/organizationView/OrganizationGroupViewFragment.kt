package com.example.simplydo.ui.fragments.organizationView

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.simplydo.R
import com.example.simplydo.adapters.WorkspaceGroupViewAdapter
import com.example.simplydo.databinding.GroupViewFragmentBinding
import com.example.simplydo.localDatabase.AppDatabase
import com.example.simplydo.model.*
import com.example.simplydo.utlis.*
import com.google.gson.Gson

internal val TAG = GroupViewFragment::class.java.canonicalName

class GroupViewFragment : Fragment(R.layout.group_view_fragment) {

    companion object {
        fun newInstance() = GroupViewFragment()
    }

    private lateinit var observerArraylistTodoModel: Observer<ArrayList<TodoModel>>
    private lateinit var viewModelOrganization: OrganizationGroupViewViewModel
    private lateinit var binding: GroupViewFragmentBinding
    private lateinit var groupViewAdapter: WorkspaceGroupViewAdapter

    private var taskCount = 0


    private var callback = object : AppInterface.GroupViewCallback {
        override fun onSelect(item: WorkspaceGroupsCollectionModel) {
            Log.i(TAG, "onSelect: ")
            val bundle = Bundle()
            bundle.putSerializable(AppConstant.Key.NAVIGATION_WORKSPACE_GROUP_ITEM, item)
            findNavController().navigate(
                R.id.action_groupViewFragment_to_organizationTaskFragment,
                bundle
            )
        }

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = GroupViewFragmentBinding.bind(view)
        setupViewModel()
        setupObserver()


        val accentText = "<font color='#6200EE'>Workspace</font>"

        val workspaceTextTitle = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(
                String.format("My %s", accentText),
                Html.FROM_HTML_MODE_LEGACY
            )
        } else {
            Html.fromHtml(String.format("My %s", accentText))
        }

        binding.textViewHeader.text = workspaceTextTitle
        binding.textViewParticipants.text = String.format("%d groups created", taskCount)

    }

    private fun setupObserver() {
        observerArraylistTodoModel = Observer {
            taskCount = it.size
            val dummy = getDummyData(it)
            groupViewAdapter = WorkspaceGroupViewAdapter(dataset = dummy, callback = callback)

            binding.recyclerViewGroupView.apply {
                layoutManager =
                    GridLayoutManager(requireContext(), 2)
                adapter = groupViewAdapter
            }
        }
        viewModelOrganization.mutableArrayTodoModel.observe(
            viewLifecycleOwner,
            observerArraylistTodoModel
        )
    }

    private fun setupViewModel() {
        val appDatabase = AppDatabase.getInstance(requireContext())
        val appRepository = AppRepository.getInstance(requireContext(), appDatabase)
        viewModelOrganization = ViewModelProvider(
            this,
            ViewModelFactory(requireContext(), appRepository)
        )[OrganizationGroupViewViewModel::class.java]

    }

    private fun getDummyData(arrayList: ArrayList<TodoModel>): ArrayList<WorkspaceGroupsCollectionModel> {
        val dataset = ArrayList<WorkspaceGroupsCollectionModel>()

        for (item in 1..4) {
            dataset.add(
                WorkspaceGroupsCollectionModel(
                    id = item.toString(),
                    name = "Sample Group Name",
                    description = "Test Description",
                    createdBy = UserIdModel(
                        admin = Gson().fromJson(
                            AppPreference.getPreferences(
                                AppConstant.Preferences.USER_DATA,
                                requireContext()
                            ), UserModel::class.java
                        )
                    ),
                    task = arrayList,
                    people = arrayListOf(
                        UserAccountModel(
                            user = UserModel(
                                firstName = "Vignesh",
                                middleName = "",
                                lastName = "Selvam",
                                profilePicture = "https://picsum.photos/200",
                                email = "vignesh297@gmail.com",
                                phone = "8012215105",
                                uKey = "78325648771762178364"
                            ),
                            role = arrayListOf()
                        ),
                        UserAccountModel(
                            user = UserModel(
                                firstName = "Sandeep",
                                middleName = "",
                                lastName = "Selvam",
                                profilePicture = "https://picsum.photos/200",
                                email = "sandeep0312@gmail.com",
                                phone = "8012215105",
                                uKey = "783256487717621798664"
                            ),
                            role = arrayListOf()
                        ), UserAccountModel(
                            user = UserModel(
                                firstName = "Vignesh",
                                middleName = "",
                                lastName = "Selvam",
                                profilePicture = "https://picsum.photos/200",
                                email = "vignesh297@gmail.com",
                                phone = "8012215105",
                                uKey = "78325648771762178364"
                            ),
                            role = arrayListOf()
                        ),
                        UserAccountModel(
                            user = UserModel(
                                firstName = "Sandeep",
                                middleName = "",
                                lastName = "Selvam",
                                profilePicture = "https://picsum.photos/200",
                                email = "sandeep0312@gmail.com",
                                phone = "8012215105",
                                uKey = "783256487717621798664"
                            ),
                            role = arrayListOf()
                        )
                    )
                )
            )
        }

        return dataset
    }

}