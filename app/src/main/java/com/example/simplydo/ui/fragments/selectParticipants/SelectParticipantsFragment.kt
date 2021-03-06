package com.example.simplydo.ui.fragments.selectParticipants

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplydo.R
import com.example.simplydo.adapters.workspace.ParticipantsAdapter
import com.example.simplydo.database.AppDatabase
import com.example.simplydo.databinding.SelectParticipantsFragmentBinding
import com.example.simplydo.model.AccountModel
import com.example.simplydo.model.UserAccountModel
import com.example.simplydo.utils.AppConstant
import com.example.simplydo.utils.AppRepository
import com.example.simplydo.utils.ParticipantInterface
import com.example.simplydo.utils.ViewModelFactory

internal val TAG = SelectParticipantsFragment::class.java.canonicalName

class SelectParticipantsFragment : Fragment(R.layout.select_participants_fragment) {

    companion object {
        fun newInstance() = SelectParticipantsFragment()
    }

    private lateinit var viewModel: SelectParticipantsViewModel
    private lateinit var binding: SelectParticipantsFragmentBinding
    private lateinit var participantsAdapter: ParticipantsAdapter
    private lateinit var observerParticipantData: Observer<ArrayList<AccountModel>>
    private val participantsList: ArrayList<UserAccountModel> = arrayListOf()


    private val callback = object : ParticipantInterface {
        override fun onParticipantSelected(item: UserAccountModel) {
            if (item.selected && !participantsList.contains(item)) {
                participantsList.add(item)
            } else {
                if (participantsList.contains(item)) {
                    participantsList.remove(item)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = SelectParticipantsFragmentBinding.bind(view)
        findNavController().previousBackStackEntry?.savedStateHandle?.set(
            AppConstant.Key.NAVIGATION_PARTICIPANT_KEY,
            null
        )
        setViewModel()
        setupObserver()
        viewModel.mutableParticipantsData.postValue(null)

        participantsAdapter = ParticipantsAdapter(isRemoveVisible = true, callback = callback)

        binding.recyclerViewParticipants.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = participantsAdapter
        }
        viewModel.getParticipantsList()

        binding.buttonAddParticipants.setOnClickListener {
            participantsList.distinct()
            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                AppConstant.Key.NAVIGATION_PARTICIPANT_KEY,
                participantsList
            )
            findNavController().popBackStack()
        }
    }

    private fun setupObserver() {
        observerParticipantData = Observer {
            it?.let {
                val dataset: ArrayList<UserAccountModel> = arrayListOf()
                it.forEach { accountModel ->
                    dataset.add(
                        UserAccountModel(
                            account = accountModel,
                            role = arrayListOf()
                        )
                    )
                }
                participantsAdapter.updateDataset(dataset)
            }
        }
        viewModel.mutableParticipantsData.observe(viewLifecycleOwner, observerParticipantData)
    }

    private fun setViewModel() {
        val appRepository =
            AppRepository.getInstance(requireContext(), AppDatabase.getInstance(requireContext()))
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(requireContext(), appRepository)
        )[SelectParticipantsViewModel::class.java]
        binding.apply {
            executePendingBindings()
        }
    }


}