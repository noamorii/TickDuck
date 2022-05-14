package cz.cvut.fel.pda.tickduck.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.activityViewModels
import com.google.android.material.navigation.NavigationView
import cz.cvut.fel.pda.tickduck.R
import cz.cvut.fel.pda.tickduck.adapters.TodoAdapter
import cz.cvut.fel.pda.tickduck.databinding.FragmentSearchBinding
import cz.cvut.fel.pda.tickduck.db.viewmodels.TodoViewModel
import cz.cvut.fel.pda.tickduck.model.Todo
import java.util.*

class SearchFragment : Fragment() {

    private lateinit var adapter: TodoAdapter
    private lateinit var binding: FragmentSearchBinding


    private val todoViewModel: TodoViewModel by activityViewModels {
        TodoViewModel.TodoViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSearch()
        binding.humburger.setOnClickListener {
            activity?.findViewById<DrawerLayout>(R.id.drawer_layout)?.open()
        }
    }

    fun setAdapter(adapter: TodoAdapter) {
        this@SearchFragment.adapter = adapter
    }

    private fun setSearch() {
        val editText: EditText = binding.searchTodos
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                filter(p0.toString())
            }
        })
    }

    private fun filter(text: String) {
        val list: List<Todo>? = todoViewModel.allTodosLiveData.value
        val result: MutableList<Todo> = mutableListOf()
        if (list != null) {
            for (todo in list) {
                if (todo.name.lowercase(Locale.getDefault())
                        .contains(text.lowercase(Locale.getDefault()))) {
                    result.add(todo)
                }
            }
            adapter.submitList(result)
        }
    }
}