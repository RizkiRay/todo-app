package com.example.todoapp.ui.task.add

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.todoapp.R
import com.example.todoapp.data.db.entity.Task
import com.example.todoapp.databinding.FragmentAddTaskBinding
import com.example.todoapp.scheduler.Scheduler
import com.example.todoapp.scheduler.SchedulerImpl
import com.example.todoapp.utils.toHumanDate
import java.util.Calendar
import java.util.Date

typealias State = AddTaskViewModel.State

class AddTaskFragment : Fragment() {
    private val viewModel: AddTaskViewModel by viewModels()

    private var _binding: FragmentAddTaskBinding? = null
    private val binding get() = _binding!!
    private val scheduler: Scheduler by lazy { SchedulerImpl(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.extractArguments(arguments)
        addObserver()
        addEvents()
    }

    private fun render(state: State) {
        renderTitle(state.title)
        state.date?.let { renderDateTime(it) }
        if (state.isSaved) {
            state.insertedTask?.let(::addScheduleNotif)
            val message = if (state.task != null) getString(R.string.info_task_updated)
            else getString(R.string.info_task_inserted)
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

            findNavController().navigateUp()
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkNotifPermission(): Boolean = ContextCompat.checkSelfPermission(
        requireActivity(),
        android.Manifest.permission.POST_NOTIFICATIONS
    ) == PackageManager.PERMISSION_GRANTED

    private fun addScheduleNotif(task: Task) {
        scheduler.schedule(task)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!checkNotifPermission()) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    101
                )
            }
        }
    }

    private fun renderTitle(title: String) {
        binding.etTitle.setText(title)
    }

    private fun renderDateTime(date: Date) {
        binding.etDate.setText(date.toHumanDate())
    }

    private fun addObserver() {
        viewModel.uiState.observe(viewLifecycleOwner) {
            render(it)
        }
    }

    private fun addEvents() {
        binding.etTitle.doAfterTextChanged { viewModel.setTitle(it.toString()) }
        binding.etDate.setOnClickListener {
            pickDateTime()
        }
        binding.btnSave.setOnClickListener { viewModel.saveOrUpdateTask() }
    }

    private fun pickDateTime() {
        val now = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                TimePickerDialog(
                    requireContext(),
                    { _, hour, minute ->
                        val pickedDateTime = Calendar.getInstance()
                        pickedDateTime.set(year, month, day, hour, minute)
                        viewModel.setDateTime(pickedDateTime.time)
                    },
                    now.get(Calendar.HOUR_OF_DAY),
                    now.get(Calendar.MINUTE),
                    false
                ).show()
            },
            now.get(Calendar.YEAR),
            now.get(Calendar.MONDAY),
            now.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}