package com.example.simplydo.ui.fragments.accounts.notifications

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.simplydo.R
import com.example.simplydo.databinding.NotificationSettingsFragmentBinding
import com.example.simplydo.dialog.bottomSheetDialogs.SnoozeTimePickerBottomSheetDialog
import com.example.simplydo.database.AppDatabase
import com.example.simplydo.model.TaskReminderTime
import com.example.simplydo.utils.*
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.gson.Gson

internal val TAG_NOTIFICATION = NotificationSettingsFragment::class.java.canonicalName
class NotificationSettingsFragment : Fragment(R.layout.notification_settings_fragment) {

    companion object {
        fun newInstance() = NotificationSettingsFragment()
    }

    private lateinit var observerEveryDayTaskReminderTime: Observer<in TaskReminderTime>
    private lateinit var picker: MaterialTimePicker
    private lateinit var everyDayTaskAlertTimePicker: MaterialTimePicker
    private lateinit var observerRemindMeTime: Observer<TaskReminderTime>
    private lateinit var viewModel: NotificationSettingsViewModel
    private lateinit var binding: NotificationSettingsFragmentBinding
    private lateinit var snoozeTimePickerBottomSheetDialog: SnoozeTimePickerBottomSheetDialog

    //    default every task reminder time, 24 hours format
    private var everyDayTaskAlertTime = TaskReminderTime(7, 0)

    private var snoozeAlertTime: Int = 5

    // default event alert time is 0 Hours and 15 minutes
    private var taskAlertRemainderTime = TaskReminderTime()

    private val snoozeTimeInterface = object : SnoozeTimeInterface {
        override fun onSnoozeTimeSelect(time: Int) {
            snoozeAlertTime = time
            AppPreference.storePreferences(
                AppConstant.Preferences.Settings.Notifications.SNOOZE_ALERT_TIME,
                snoozeAlertTime,
                requireContext()
            )
            binding.textViewSnoozeTime.text =
                String.format("Snooze every %d minutes", snoozeAlertTime)
        }

        override fun onClose() {
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = NotificationSettingsFragmentBinding.bind(view)
        setupViewModel()
        setupObserver()

        snoozeAlertTime = AppPreference.getPreferences(
            AppConstant.Preferences.Settings.Notifications.SNOOZE_ALERT_TIME,
            5,
            requireContext()
        )
        snoozeTimePickerBottomSheetDialog = SnoozeTimePickerBottomSheetDialog.newInstance(
            requireContext(),
            callback = snoozeTimeInterface
        )

        viewModel.mutableEveryDayTaskReminderTime.postValue(
            Gson().fromJson(
                AppPreference.getPreferences(
                    AppConstant.Preferences.Settings.Notifications.REMIND_ME_TIME,
                    Gson().toJson(everyDayTaskAlertTime),
                    requireContext()
                ), TaskReminderTime::class.java
            )
        )

        viewModel.mutableTaskReminderTime.postValue(
            Gson().fromJson(
                AppPreference.getPreferences(
                    AppConstant.Preferences.Settings.Notifications.REMIND_ME_BEFORE_EVENT_START_TIME,
                    Gson().toJson(taskAlertRemainderTime),
                    requireContext()
                ), TaskReminderTime::class.java
            )
        )

        setupView()
    }

    private fun setupObserver() {
        observerRemindMeTime = Observer {
            Log.i(TAG_NOTIFICATION, "setupObserver: picker >> $it")
            it?.let {
                taskAlertRemainderTime = it
                picker = MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(taskAlertRemainderTime.hours)
                    .setMinute(taskAlertRemainderTime.minutes)
                    .build()

                picker.addOnPositiveButtonClickListener {
                    taskAlertRemainderTime.hours = picker.hour
                    taskAlertRemainderTime.minutes = picker.minute
                    viewModel.mutableTaskReminderTime.postValue(taskAlertRemainderTime)
                }

                binding.textViewRemainderTime.text = String.format(
                    "Remind me before %s Hrs, %s Min",
                    taskAlertRemainderTime.hours,
                    taskAlertRemainderTime.minutes
                )

                AppPreference.storePreferences(
                    AppConstant.Preferences.Settings.Notifications.REMIND_ME_BEFORE_EVENT_START_TIME,
                    Gson().toJson(taskAlertRemainderTime),
                    requireContext()
                )
            }
        }

        observerEveryDayTaskReminderTime = Observer {
            it?.let {
                everyDayTaskAlertTime = it

                everyDayTaskAlertTimePicker = MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(everyDayTaskAlertTime.hours)
                    .setMinute(everyDayTaskAlertTime.minutes)
                    .build()

                binding.textViewCurrentDayTaskRemainderTime.text =
                    String.format(
                        "Remind me on %02d: %02d every day",
                        everyDayTaskAlertTime.hours, everyDayTaskAlertTime.minutes
                    )

                AppPreference.storePreferences(
                    AppConstant.Preferences.Settings.Notifications.REMIND_ME_TIME,
                    Gson().toJson(everyDayTaskAlertTime),
                    requireContext()
                )

                everyDayTaskAlertTimePicker.addOnPositiveButtonClickListener {
                    everyDayTaskAlertTime.hours = everyDayTaskAlertTimePicker.hour
                    everyDayTaskAlertTime.minutes = everyDayTaskAlertTimePicker.minute
                    viewModel.mutableEveryDayTaskReminderTime.postValue(everyDayTaskAlertTime)
                }
            }
        }

        viewModel.mutableTaskReminderTime.observe(viewLifecycleOwner, observerRemindMeTime)
        viewModel.mutableEveryDayTaskReminderTime.observe(
            viewLifecycleOwner,
            observerEveryDayTaskReminderTime
        )
    }

    private fun setupViewModel() {
        val appDatabase = AppDatabase.getInstance(requireContext())
        val repository = AppRepository.getInstance(requireContext(), appDatabase)
        val viewModelFactory = ViewModelFactory(requireContext(), repository)

        viewModel =
            ViewModelProvider(this, viewModelFactory)[NotificationSettingsViewModel::class.java]

        binding.apply {
            this.viewModel = this@NotificationSettingsFragment.viewModel
            executePendingBindings()
        }
    }

    private fun setupView() {
        binding.switchShowNotification.apply {

            isChecked = AppPreference.getPreferences(
                AppConstant.Preferences.Settings.Notifications.SHOW_ALL_NOTIFICATION,
                true,
                requireContext()
            )

            disableAllOnNotificationDisabled(this.isChecked)

            setOnCheckedChangeListener { _, isChecked ->
                AppPreference.storePreferences(
                    AppConstant.Preferences.Settings.Notifications.SHOW_ALL_NOTIFICATION,
                    isChecked,
                    requireContext()
                )
                also { disableAllOnNotificationDisabled(isChecked) }
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

        binding.switchShowWorkspaceTask.apply {
            isChecked = AppPreference.getPreferences(
                AppConstant.Preferences.Settings.Notifications.SHOW_WORKSPACE_TASK_NOTIFICATION,
                true,
                requireContext()
            )
            setOnCheckedChangeListener { _, isChecked ->
                AppPreference.storePreferences(
                    AppConstant.Preferences.Settings.Notifications.SHOW_WORKSPACE_TASK_NOTIFICATION,
                    isChecked,
                    requireContext()
                )
            }
        }

        binding.switchRemindMeBeforeTaskStar.apply {
            isChecked = AppPreference.getPreferences(
                AppConstant.Preferences.Settings.Notifications.REMIND_ME_BEFORE_EVENT_START,
                true,
                requireContext()
            )
            setOnCheckedChangeListener { _, isChecked ->
                AppPreference.storePreferences(
                    AppConstant.Preferences.Settings.Notifications.REMIND_ME_BEFORE_EVENT_START,
                    isChecked,
                    requireContext()
                )
            }
        }

        binding.switchSnoozeAlert.apply {
            isChecked = AppPreference.getPreferences(
                AppConstant.Preferences.Settings.Notifications.SNOOZE_ALERT,
                true,
                requireContext()
            )
            setOnCheckedChangeListener { _, isChecked ->
                AppPreference.storePreferences(
                    AppConstant.Preferences.Settings.Notifications.SNOOZE_ALERT,
                    isChecked,
                    requireContext()
                )
            }
        }

        binding.switchRemindCurrentDayTask.apply {
            isChecked = AppPreference.getPreferences(
                AppConstant.Preferences.Settings.Notifications.REMIND_ME_THE_CURRENT_DAY_TASK,
                true,
                requireContext()
            )
            setOnCheckedChangeListener { _, isChecked ->
                AppPreference.storePreferences(
                    AppConstant.Preferences.Settings.Notifications.REMIND_ME_THE_CURRENT_DAY_TASK,
                    isChecked,
                    requireContext()
                )
            }
        }

        binding.textViewRemainderTime.apply {
            setOnClickListener {
                picker.show(
                    requireActivity().supportFragmentManager,
                    picker.javaClass.canonicalName
                )
            }
        }

        binding.textViewSnoozeTime.apply {
            text = String.format("Snooze every %d minutes", snoozeAlertTime)
            setOnClickListener {
                snoozeTimePickerBottomSheetDialog.show(
                    requireActivity().supportFragmentManager,
                    snoozeTimePickerBottomSheetDialog.javaClass.canonicalName
                )
            }
        }

        binding.textViewCurrentDayTaskRemainderTime.apply {
            text = String.format(
                "Remind me on %d: %d every day",
                everyDayTaskAlertTime.hours, everyDayTaskAlertTime.minutes
            )
            setOnClickListener {
                everyDayTaskAlertTimePicker.show(
                    requireActivity().supportFragmentManager,
                    everyDayTaskAlertTimePicker.javaClass.canonicalName
                )
            }
        }
    }

    private fun disableAllOnNotificationDisabled(isChecked: Boolean) {
        binding.let {
            it.switchShowPersonalTaskNotification.isEnabled = isChecked
            it.switchShowPersonalTaskNotification.isEnabled = isChecked
            it.switchShowWorkspaceTask.isEnabled = isChecked
            it.switchRemindMeBeforeTaskStar.isEnabled = isChecked
            it.switchSnoozeAlert.isEnabled = isChecked
            it.switchRemindCurrentDayTask.isEnabled = isChecked

            it.textViewRemainderTime.isEnabled = isChecked
            it.textViewCurrentDayTaskRemainderTime.isEnabled = isChecked
            it.textViewSnoozeTime.isEnabled = isChecked
        }
    }
}