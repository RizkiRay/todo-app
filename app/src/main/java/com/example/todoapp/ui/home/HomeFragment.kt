package com.example.todoapp.ui.home

import android.opengl.Visibility
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import com.example.todoapp.data.db.entity.Task
import com.example.todoapp.databinding.FragmentHomeBinding
import com.example.todoapp.scheduler.Scheduler
import com.example.todoapp.scheduler.SchedulerImpl
import com.example.todoapp.ui.task.add.AddTaskViewModel
import com.google.android.material.snackbar.Snackbar

typealias State = HomeViewModel.State

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val adapter by lazy {
        TaskAdapter(
            viewModel::setSelectedTask,
            ::onItemChecked
        )
    }

    private val viewModel: HomeViewModel by viewModels()
    private val scheduler: Scheduler by lazy { SchedulerImpl(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addEvents()
        setupRecyclerView()
        addObserver()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getItems()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerView() {
        binding.rvTask.adapter = adapter
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val deletedTask = adapter.getItemAt(viewHolder.adapterPosition)
                viewModel.deleteItem(deletedTask)
                scheduler.cancel(deletedTask)
                Snackbar.make(
                    binding.rvTask,
                    getString(R.string.info_task_deleted, deletedTask.name),
                    Snackbar.LENGTH_SHORT
                ).show()
            }

        }).attachToRecyclerView(binding.rvTask)
    }

    private fun render(state: State) {
        renderItems(state.tasks)
        state.selectedTask?.let {
            goToEditTask(it)
        }
    }

    private fun renderItems(items: List<Task>) {
        binding.tvEmptyData.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
        adapter.items = items
    }

    private fun goToEditTask(selectedTask: Task) {
        goToAddTaskScreen(Bundle().apply {
            putParcelable(AddTaskViewModel.KEY_TASK, selectedTask)
        })
        viewModel.setSelectedTask(null)
    }

    private fun goToAddTaskScreen(bundle: Bundle = Bundle()) {
        findNavController().navigate(R.id.action_navigation_home_to_navigation_form, bundle)
    }

    private fun onItemChecked(task: Task, isCompleted: Boolean) {
        scheduler.cancel(task)
        viewModel.setTaskCompleted(task, isCompleted)
    }

    private fun addEvents() {
        binding.etSearch.doAfterTextChanged {
            viewModel.setKeyword(it.toString())
        }
        binding.fabAddTask.setOnClickListener {
            goToAddTaskScreen()
        }
    }

    private fun addObserver() {
        viewModel.uiState.observe(viewLifecycleOwner) {
            render(it)
        }
    }
}