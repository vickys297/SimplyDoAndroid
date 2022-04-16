package com.example.simplydo.ui.fragments.accounts.notifications

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.simplydo.R
import com.example.simplydo.databinding.NotificationSettingsFragmentBinding
import com.example.simplydo.localDatabase.AppDatabase
import com.example.simplydo.utlis.AppConstant
import com.example.simplydo.utlis.AppPreference
import com.example.simplydo.utlis.AppRepository
import com.example.simplydo.utlis.ViewModelFactory
import com.google.android.material.timepicker.MaterialTimePicker
import java.util.*


class NotificationSettingsFragment : Fragment(R.layout.notification_settings_fragment) {

    companion object {
        fun newInstance() = NotificationSettingsFragment()
    }

    private lateinit var picker: MaterialTimePicker
    private lateinit var observerRemindMeTime: Observer<Long>
    private lateinit var viewModel: NotificationSettingsViewModel
    private lateinit var binding: NotificationSettingsFragmentBinding

    // default event alert time is 15 min and 30 sec >> 1649961900179
    private var taskAlertRemainderTime: Calendar = Calendar.getInstance()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = NotificationSettingsFragmentBinding.bind(view)
        setupViewModel()
//        setupObserver()

//        viewModel.mutableRemindMeTime.postValue(
//            AppPreference.getPreferences(
//                key = AppConstant.Preferences.Settings.Notifications.REMIND_ME_BEFORE_EVENT_START,
//                default = 1649961900179L,
//                context = requireContext()
//            )
//        )

        binding.switchShowNotification.apply {
            isChecked = AppPreference.getPreferences(
                AppConstant.Preferences.Settings.Notifications.SHOW_ALL_NOTIFICATION,
                true,
                requireContext()
            )
            setOnCheckedChangeListener { _, isChecked ->
                AppPreference.storePreferences(
                    AppConstant.Preferences.Settings.Notifications.SHOW_ALL_NOTIFICATION,
                    isChecked,
                    requireContext()
                )

                also {
                    binding.let {
                        it.switchShowPersonalTaskNotification.isEnabled = this.isChecked
                        it.switchShowPersonalTaskNotification.isEnabled = this.isChecked
                        it.switchShowWorkspaceTask.isEnabled = this.isChecked
                        it.switchRemindMeBeforeTaskStar.isEnabled = this.isChecked
                        it.switchSnoozeAlert.isEnabled = this.isChecked
                        it.switchRemindCurrentDayTask.isEnabled = this.isChecked
                    }
                }
            }
        }

        binding.switchShowPersonalTaskNotification.apply {
            isChecked = AppPreference.getPreferences(
                AppConstant.Preferences.Settings.Notifications.SHOW_PERSONAL_TASK_NOTIFICATION,
                true,
                requireContext()
            )
            setOnCheckedChangeListener { _, isChecked ->
                AppPreference.storePreferences(
                    AppConstant.Preferences.Settings.Notifications.SHOW_PERSONAL_TASK_NOTIFICATION,
                    isChecked,
                    requireContext()
                )
            }
        }
//
//
//        binding.switchShowWorkspaceTask.apply {
//            isChecked = AppPreference.getPreferences(
//                AppConstant.Preferences.Settings.Notifications.SHOW_WORKSPACE_TASK_NOTIFICATION,
//                true,
//                requireContext()
//            )
//            setOnCheckedChangeListener { _, isChecked ->
//                AppPreference.storePreferences(
//                    AppConstant.Preferences.Settings.Notifications.SHOW_WORKSPACE_TASK_NOTIFICATION,
//                    isChecked,
//                    requireContext()
//                )
//            }
//        }
//        binding.switchRemindMeBeforeTaskStar.apply {
//            isChecked = AppPreference.getPreferences(
//                AppConstant.Preferences.Settings.Notifications.REMIND_ME_BEFORE_EVENT_START,
//                true,
//                requireContext()
//            )
//            setOnCheckedChangeListener { _, isChecked ->
//                AppPreference.storePreferences(
//                    AppConstant.Preferences.Settings.Notifications.REMIND_ME_BEFORE_EVENT_START,
//                    isChecked,
//                    requireContext()
//                )
//            }
//        }
//        binding.switchSnoozeAlert.apply {
//            isChecked = AppPreference.getPreferences(
//                AppConstant.Preferences.Settings.Notifications.SNOOZE_ALERT,
//                true,
//                requireContext()
//            )
//            setOnCheckedChangeListener { _, isChecked ->
//                AppPreference.storePreferences(
//                    AppConstant.Preferences.Settings.Notifications.SNOOZE_ALERT,
//                    isChecked,
//                    requireContext()
//                )
//            }
//        }
//
//        binding.switchRemindCurrentDayTask.apply {
//            isChecked = AppPreference.getPreferences(
//                AppConstant.Preferences.Settings.Notifications.REMIND_ME_THE_CURRENT_DAY_TASK,
//                true,
//                requireContext()
//            )
//            setOnCheckedChangeListener { _, isChecked ->
//                AppPreference.storePreferences(
//                    AppConstant.Preferences.Settings.Notifications.REMIND_ME_THE_CURRENT_DAY_TASK,
//                    isChecked,
//                    requireContext()
//                )
//            }
//        }
//
//        binding.textViewRemainderTime.apply {
//            text = String.format(
//                "Remind me before %s Hrs, %s Min and %s sec",
//                AppFunctions.convertTimeInMillsecToPattern(
//                    taskAlertRemainderTime.timeInMillis,
//                    "HH"
//                ),
//                AppFunctions.convertTimeInMillsecToPattern(
//                    taskAlertRemainderTime.timeInMillis,
//                    "mm"
//                ),
//                AppFunctions.convertTimeInMillsecToPattern(
//                    taskAlertRemainderTime.timeInMillis,
//                    "ss"
//                ),
//            )
//            setOnClickListener {
//                picker.show(
//                    requireActivity().supportFragmentManager,
//                    picker.javaClass.canonicalName
//                )
//            }
//        }


//        picker.addOnPositiveButtonClickListener {
//            val calendar = Calendar.getInstance()
//            calendar.set(Calendar.HOUR_OF_DAY, picker.hour)
//            calendar.set(Calendar.MINUTE, picker.minute)
//
//            viewModel.mutableRemindMeTime.postValue(calendar.timeInMillis)
//
//        }
//        picker.addOnNegativeButtonClickListener {
//            picker.dismiss()
//        }
//        picker.addOnCancelListener {
//            picker.dismiss()
//        }
//        picker.addOnDismissListener {
//            picker.dismiss()
//        }

    }

    private fun setupObserver() {
        binding.apply {
            executePendingBindings()
        }
        observerRemindMeTime = Observer {
//            picker = MaterialTimePicker.Builder()
//                .setTimeFormat(TimeFormat.CLOCK_24H)
//                .setHour(taskAlertRemainderTime.get(Calendar.HOUR_OF_DAY))
//                .setMinute(taskAlertRemainderTime.get(Calendar.MINUTE))
//                .setTitleText("Select alert time")
//                .build()
//
//            AppPreference.storePreferences(
//                AppConstant.Preferences.Settings.Notifications.REMIND_ME_BEFORE_EVENT_START,
//                it,
//                requireContext()
//            )
//            binding.textViewRemainderTime.text = String.format(
//                "Remind me before %s Hrs, %s Min and %s sec",
//                AppFunctions.convertTimeInMillsecToPattern(
//                    it,
//                    "HH"
//                ),
//                AppFunctions.convertTimeInMillsecToPattern(
//                    it,
//                    "mm"
//                ),
//                AppFunctions.convertTimeInMillsecToPattern(
//                    it,
//                    "ss"
//                ),
//            )
        }
        viewModel.mutableRemindMeTime.observe(viewLifecycleOwner, observerRemindMeTime)
    }

    private fun setupViewModel() {
        val appDatabase = AppDatabase.getInstance(requireContext())
        val repository = AppRepository.getInstance(requireContext(), appDatabase)
        val viewModelFactory = ViewModelFactory(requireContext(), repository)
        viewModel =
            ViewModelProvider(this, viewModelFactory)[NotificationSettingsViewModel::class.java]

    }

}