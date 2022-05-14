package cz.cvut.fel.pda.tickduck.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cz.cvut.fel.pda.tickduck.R
import cz.cvut.fel.pda.tickduck.activities.NewTodoActivity
import cz.cvut.fel.pda.tickduck.activities.TodoDetailActivity
import cz.cvut.fel.pda.tickduck.adapters.TodoAdapter
import cz.cvut.fel.pda.tickduck.databinding.RecycleViewFragmentBinding
import cz.cvut.fel.pda.tickduck.db.viewmodels.TodoViewModel
import cz.cvut.fel.pda.tickduck.model.Todo
import cz.cvut.fel.pda.tickduck.model.intentDTO.NewTodoDTO
import cz.cvut.fel.pda.tickduck.utils.SerializableExtras.NEW_TODO_DTO
import cz.cvut.fel.pda.tickduck.utils.SerializableExtras.TODO_DETAIL
import java.util.*

class TodoFragment : BaseFragment(), TodoAdapter.Listener {

    companion object {
        @JvmStatic
        fun newInstance() = TodoFragment()
        lateinit var adapter: TodoAdapter
    }

    private lateinit var binding: RecycleViewFragmentBinding
    private lateinit var editLauncher: ActivityResultLauncher<Intent>
    private lateinit var searchFragment: SearchFragment

    private val todoViewModel: TodoViewModel by activityViewModels {
        TodoViewModel.TodoViewModelFactory(requireContext())
    }

    override fun onClickNew() {
        editLauncher.launch(Intent(activity, NewTodoActivity::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        onEditResult()
        searchFragment = SearchFragment()
        childFragmentManager.beginTransaction().replace(R.id.search, searchFragment).commit()
    }

    private fun onEditResult() {
        editLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                todoViewModel.insertTodo(it.data?.getSerializableExtra(NEW_TODO_DTO) as NewTodoDTO)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = RecycleViewFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater) // todo not working
        inflater.inflate(R.menu.todo_fragment_toolbar_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

        }
        return false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRCView()
        setObserver()
    }

    private fun setObserver() {
        todoViewModel.allTodosLiveData.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    private fun initRCView() = with(binding) {
        rcViewTodos.layoutManager = LinearLayoutManager(activity)
        adapter = TodoAdapter(this@TodoFragment)
        ItemTouchHelper(simpleCallback).attachToRecyclerView(rcViewTodos)
        rcViewTodos.adapter = adapter
        searchFragment.setAdapter(adapter)
    }

    override fun onClickItem(task: Todo) {
        val intent = Intent(activity, TodoDetailActivity::class.java).apply {
            putExtra(TODO_DETAIL, task)
        }
        editLauncher.launch(intent)
    }

    override fun onClickCheckbox(task: Todo) {
        todoViewModel.updateTodo(task)
    }

    override fun deleteTodo(id: Int) {
        todoViewModel.deleteTodo(id)
    }

    private val simpleCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            TODO("Not yet implemented")
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            todoViewModel.allTodosLiveData.value?.get(viewHolder.adapterPosition)?.apply {
                deleteTodo(id!!)
                Toast.makeText(requireContext(), "Todo \"${name}\" has been deleted.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}