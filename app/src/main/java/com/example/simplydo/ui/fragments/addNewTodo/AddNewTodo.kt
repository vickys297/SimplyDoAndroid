package com.example.simplydo.ui.fragments.addNewTodo

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.simplydo.R
import com.example.simplydo.databinding.AddNewTodoFragmentBinding
import com.example.simplydo.localDatabase.AppDatabase
import com.example.simplydo.model.ContactModel
import com.example.simplydo.utli.*
import com.example.simplydo.utli.bottomSheetDialogs.attachments.AddAttachmentsFragments
import java.util.*
import kotlin.collections.ArrayList

internal val TAG = AddNewTodo::class.java.canonicalName

class AddNewTodo : Fragment(), NewTodoOptionsFragmentsInterface {

    companion object {
        fun newInstance() = AddNewTodo()
    }

    private lateinit var viewModel: AddNewTodoViewModel
    private lateinit var binding: AddNewTodoFragmentBinding

    private lateinit var contactInfo: ArrayList<ContactModel>
    private lateinit var imagesList: ArrayList<String>

    private lateinit var selectedEventDate: String


    private var addAttachmentInterface: AddAttachmentInterface = object : AddAttachmentInterface {
        override fun onAddDocument() {
        }

        override fun onAddAudio() {
            findNavController().navigate(R.id.action_addNewTodo_to_audioListFragment)
        }

        override fun onOpenGallery() {
            findNavController().navigate(R.id.action_addNewTodo_to_galleryListFragment)
        }

        override fun onAddContact() {
            findNavController().navigate(R.id.action_addNewTodo_to_contactsFragment)
        }

        override fun onAddLocation() {
        }

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.add_new_todo_fragment, container, false)
        setupObserver()
        setViewModel()

        arguments?.let {


            if (requireArguments().getString("ContactList").isNullOrEmpty()) {
                Log.i(TAG, "onCreateView:  ${requireArguments().getString("ContactList")}")
            } else {
                Log.i(TAG, "onCreateView: No Contact List Found")
            }


            selectedEventDate = it.get(getString(R.string.eventDateString)) as String

            binding.include.tvDayOfMonth.text =
                AppConstant.parseStringDateToCalender(selectedEventDate)
                    .get(Calendar.DAY_OF_MONTH)
                    .toString()

            binding.include.tvMonth.text = AppConstant.dateFormatter(
                AppConstant.DATE_PATTERN_MONTH_TEXT)
                .format(AppConstant.parseStringDateToCalender(selectedEventDate).time)
                .uppercase(Locale.getDefault())
        }


        // setup array list
        contactInfo = ArrayList()
        imagesList = ArrayList()

        return binding.root
    }

    private fun setupObserver() {
    }

    private fun setViewModel() {
        viewModel = ViewModelProvider(this,
            ViewModelFactory(requireContext(),
                AppRepository.getInstance(requireContext(),
                    AppDatabase.getInstance(context = requireContext())))).get(
            AddNewTodoViewModel::class.java)
        binding.apply {
            this.viewModel = this@AddNewTodo.viewModel
            lifecycleOwner = this@AddNewTodo
            executePendingBindings()
        }


        // We use a String here, but any type that can be put in a Bundle is supported
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<ArrayList<ContactModel>>(
            "SelectedContactList")?.observe(
            viewLifecycleOwner) { result ->
            // Do something with the result.

            Log.i(TAG, "currentBackStackEntry: $result")
        }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        binding.btnCreateTodoTask.setOnClickListener {
            createToDo()
        }

        binding.imageButtonNewTodoOptions.setOnClickListener {
            AddAttachmentsFragments.newInstance(addAttachmentInterface)
                .show(requireActivity().supportFragmentManager, "dialog")
        }
    }


    private fun createToDo() {
        // create new task
    }

    override fun onAddAttachments() {
        AddAttachmentsFragments.newInstance(addAttachmentInterface)
            .show(requireActivity().supportFragmentManager, "dialog")
    }

    override fun onClose() {
        findNavController().navigateUp()
    }

}