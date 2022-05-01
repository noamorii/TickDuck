package cz.cvut.fel.pda.tickduck.db

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cz.cvut.fel.pda.tickduck.R
import cz.cvut.fel.pda.tickduck.databinding.TaskBinding
import cz.cvut.fel.pda.tickduck.model.Todo

class TodoAdapter : ListAdapter<Todo, TodoAdapter.TaskHolder>(Comparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskHolder {
        return TaskHolder.create(parent)
    }

    override fun onBindViewHolder(holder: TaskHolder, position: Int) {
        holder.setData(getItem(position))
    }

    class TaskHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = TaskBinding.bind(view)

        fun setData(task: Todo) = with(binding) {
            taskTitle.text = task.name
        }

        companion object {
            fun create(parent: ViewGroup): TaskHolder {
                return TaskHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.task, parent, false))
            }
        }
    }

    class Comparator : DiffUtil.ItemCallback<Todo>() {
        override fun areItemsTheSame(oldItem: Todo, newItem: Todo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Todo, newItem: Todo): Boolean {
            return oldItem == newItem
        }
    }
}