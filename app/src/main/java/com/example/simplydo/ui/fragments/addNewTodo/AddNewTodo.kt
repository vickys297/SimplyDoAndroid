package com.example.simplydo.ui.fragments.addNewTodo

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.simplydo.R
import com.example.simplydo.databinding.AddNewTodoFragmentBinding
import com.example.simplydo.localDatabase.AppDatabase
import com.example.simplydo.model.ContactInfo
import com.example.simplydo.utli.AddAttachmentInterface
import com.example.simplydo.utli.AppConstant
import com.example.simplydo.utli.AppRepository
import com.example.simplydo.utli.ViewModelFactory
import com.example.simplydo.utli.bottomSheetDialogs.attachments.AddAttachmentsFragments
import java.util.*
import kotlin.collections.ArrayList

internal val TAG = AddNewTodo::class.java.canonicalName

class AddNewTodo : Fragment() {

    companion object {
        fun newInstance() = AddNewTodo()
    }

    private lateinit var viewModel: AddNewTodoViewModel
    private lateinit var binding: AddNewTodoFragmentBinding

    private lateinit var contactInfo: ArrayList<ContactInfo>
    private lateinit var imagesList: ArrayList<String>

    private lateinit var selectedEventDate: String


    private var addAttachmentInterface: AddAttachmentInterface = object : AddAttachmentInterface{
        override fun onAddDocument() {
        }

        override fun onAddAudio() {
        }

        override fun onOpenGallery() {
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
        setViewModel()

        arguments?.let {

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
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.btnAddAttachments.setOnClickListener {
            AddAttachmentsFragments.newInstance(addAttachmentInterface).show(requireActivity().supportFragmentManager,"dialog")
        }

        binding.btnCreateTodoTask.setOnClickListener {
            createToDo()
        }

        binding.ivClose.setOnClickListener {
            findNavController().navigateUp()
        }



    }



    private fun createToDo() {

    }

}