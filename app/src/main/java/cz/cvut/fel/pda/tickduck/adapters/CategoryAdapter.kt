package cz.cvut.fel.pda.tickduck.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cz.cvut.fel.pda.tickduck.R
import cz.cvut.fel.pda.tickduck.databinding.TaskBinding
import cz.cvut.fel.pda.tickduck.model.Category

class CategoryAdapter(
    private val listener: Listener
) : ListAdapter<Category, CategoryAdapter.CategoryHolder>(Comparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryHolder {
        return CategoryHolder.create(parent)
    }

    override fun onBindViewHolder(holder: CategoryHolder, position: Int) {
        holder.setData(getItem(position), listener)
    }

    class CategoryHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = TaskBinding.bind(view)

        companion object {
            fun create(parent: ViewGroup): CategoryHolder {
                return CategoryHolder(LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.task, parent, false)
                )
            }
        }
        fun setData(category: Category, listener: Listener) = with(binding) {
            taskTitle.text = category.name
        }
    }

    class Comparator : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem.name == newItem.name
        }
    }

    interface Listener {
        fun onClickItem(category: Category)
        fun deleteCategory(id: Int)
    }
}