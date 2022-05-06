package cz.cvut.fel.pda.tickduck.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import cz.cvut.fel.pda.tickduck.activities.MainApp
import cz.cvut.fel.pda.tickduck.activities.NewTodoActivity
import cz.cvut.fel.pda.tickduck.databinding.FragmentTodoBinding
import cz.cvut.fel.pda.tickduck.db.MainViewModel
import cz.cvut.fel.pda.tickduck.db.TodoAdapter
import cz.cvut.fel.pda.tickduck.model.Todo

class TodoFragment : BaseFragment() {

    private lateinit var binding: FragmentTodoBinding
    private lateinit var editLauncher: ActivityResultLauncher<Intent>
    private lateinit var adapter: TodoAdapter

    private val mainViewModel: MainViewModel by activityViewModels {
        MainViewModel.MainViewModelFactory((context?.applicationContext as MainApp).database)
    }

    override fun onClickNew() {
        editLauncher.launch(Intent(activity, NewTodoActivity::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onEditResult()
    }

    private fun onEditResult() {
        editLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                mainViewModel.insertTodo(it.data?.getSerializableExtra("new_todo") as Todo)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTodoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRCView()
        setObserver()
    }

    private fun setObserver() {
        mainViewModel.allTodos.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    private fun initRCView() = with(binding) {
        rcViewTodos.layoutManager = LinearLayoutManager(activity)
        adapter = TodoAdapter()
        rcViewTodos.adapter = adapter
    }

    companion object {
        @JvmStatic
        fun newInstance() = TodoFragment()
    }
}