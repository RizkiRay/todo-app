package com.example.todoapp.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.data.db.entity.Task
import com.example.todoapp.databinding.ItemTaskBinding
import com.example.todoapp.utils.toDate
import com.example.todoapp.utils.toHumanDate

class TaskAdapter(
    private val onItemClicked: (task: Task) -> Unit = {},
    private val onCheckedChange: (task: Task, isChecked: Boolean) -> Unit = { _, _ -> }
) :
    RecyclerView.Adapter<TaskAdapter.ViewHolder>() {
    var items: List<Task> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    fun getItemAt(pos: Int): Task {
        return items[pos]
    }

    inner class ViewHolder(val itemBinding: ItemTaskBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(item: Task) = with(itemBinding) {
            tvTaskTitle.text = item.name
            tvTaskDeadline.text = item.dueDate.toDate()?.toHumanDate()
            cbCompleted.isChecked = item.isCompleted
            root.setOnClickListener { onItemClicked.invoke(item) }
            cbCompleted.setOnCheckedChangeListener { _, isChecked ->
                onCheckedChange.invoke(item, isChecked)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemTaskBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

}
