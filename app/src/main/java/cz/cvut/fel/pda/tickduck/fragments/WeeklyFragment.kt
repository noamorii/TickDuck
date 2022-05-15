package cz.cvut.fel.pda.tickduck.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cz.cvut.fel.pda.tickduck.activities.NewTodoActivity
import cz.cvut.fel.pda.tickduck.activities.TodoDetailActivity
import cz.cvut.fel.pda.tickduck.adapters.CalendarAdapter
import cz.cvut.fel.pda.tickduck.adapters.TodoAdapter
import cz.cvut.fel.pda.tickduck.databinding.FragmentWeeklyBinding
import cz.cvut.fel.pda.tickduck.db.viewmodels.TodoViewModel
import cz.cvut.fel.pda.tickduck.model.Todo
import cz.cvut.fel.pda.tickduck.model.intentDTO.NewTodoDTO
import cz.cvut.fel.pda.tickduck.utils.CalendarUtils
import cz.cvut.fel.pda.tickduck.utils.CalendarUtils.monthYearFromDate
import cz.cvut.fel.pda.tickduck.utils.SerializableExtras
import cz.cvut.fel.pda.tickduck.utils.SerializableExtras.TODO_DETAIL
import java.time.LocalDate

class WeeklyFragment : BaseFragment(), TodoAdapter.Listener, CalendarAdapter.OnItemListener {

    private lateinit var monthYearText: TextView
    private lateinit var todoAdapter: TodoAdapter
    private lateinit var calendarRecyclerView: RecyclerView
    private lateinit var binding: FragmentWeeklyBinding
    private lateinit var editLauncher: ActivityResultLauncher<Intent>

    companion object {
        @JvmStatic
        fun newInstance() = WeeklyFragment()
    }

    private val todoViewModel: TodoViewModel by activityViewModels {
        TodoViewModel.TodoViewModelFactory(requireContext())
    }

    override fun onClickNew() {
        editLauncher.launch(Intent(activity, NewTodoActivity::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CalendarUtils.selectedDay = LocalDate.now()
        onEditResult()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWeeklyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initWidgets()
        setWeekView()
        initRCView()
        setObserver()
        setButtonsListener()
    }

    private fun initRCView() = with(binding) {
        todoRecyclerView.layoutManager = LinearLayoutManager(activity)
        todoAdapter = TodoAdapter(this@WeeklyFragment)
        ItemTouchHelper(simpleCallback).attachToRecyclerView(todoRecyclerView)
        todoRecyclerView.adapter = todoAdapter
    }

    private fun setObserver() {
        todoViewModel.allTodosLiveData.observe(viewLifecycleOwner) {
            todoAdapter.submitList(searchByDate(CalendarUtils.selectedDay))
        }
    }

    private fun searchByDate(date: LocalDate): ArrayList<Todo> {
        val listByDate : ArrayList<Todo> = ArrayList()
        for (v in todoViewModel.allTodosLiveData.value!!)
            if (v.date == date.minusMonths(0).toString()) listByDate.add(v)
        return listByDate
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
            val id: Int? = todoViewModel.allTodosLiveData.value?.get(viewHolder.adapterPosition)?.id
            if (id != null) {
                deleteTodo(id)
            }
        }
    }

    private fun setButtonsListener() {
        binding.buttonLeft.setOnClickListener {
            previousWeekAction()
        }
        binding.buttonRight.setOnClickListener{
            nextWeekAction()
        }
        binding.weeklyMode.setOnClickListener {
            FragmentManager.setFragment(CalendarFragment.newInstance(), activity as AppCompatActivity)
        }
        binding.todayTodos.setOnClickListener {
            CalendarUtils.selectedDay = LocalDate.now()
            setWeekView()
        }
    }

    private fun initWidgets() {
        calendarRecyclerView = binding.calendarRecyclerView
        monthYearText = binding.monthYearTV
    }

    private fun setWeekView() {
        monthYearText.text = monthYearFromDate(CalendarUtils.selectedDay)
        val daysInWeek: ArrayList<LocalDate?> = CalendarUtils.daysInWeekArray(CalendarUtils.selectedDay)
        val calendarAdapter = CalendarAdapter(daysInWeek, this)
        val layoutManager: RecyclerView.LayoutManager =
            GridLayoutManager(activity, 7)
        calendarRecyclerView.layoutManager = layoutManager
        calendarRecyclerView.adapter = calendarAdapter
    }

    private fun onEditResult() {
        editLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                todoViewModel.insertTodo(it.data?.getSerializableExtra(SerializableExtras.NEW_TODO_DTO) as NewTodoDTO)
            }
        }
    }

    private fun previousWeekAction() {
        CalendarUtils.selectedDay = CalendarUtils.selectedDay.minusWeeks(1)
        todoAdapter.submitList(searchByDate(CalendarUtils.selectedDay))
        setWeekView()
    }

    private fun nextWeekAction() {
        CalendarUtils.selectedDay = CalendarUtils.selectedDay.plusWeeks(1)
        todoAdapter.submitList(searchByDate(CalendarUtils.selectedDay))
        setWeekView()
    }

    override fun onItemClick(position: Int, date: LocalDate?) {
        if (date != null) {
            CalendarUtils.selectedDay = date
            setWeekView()
            todoAdapter.submitList(searchByDate(date))
        }
    }

    override fun onClickItem(task: Todo) {
        val intent = Intent(activity, TodoDetailActivity::class.java).apply {
            putExtra(TODO_DETAIL, task)
        }
        editLauncher.launch(intent)
    }

    override fun deleteTodo(id: Int) {
        todoViewModel.deleteTodo(id)
    }

    override fun onClickCheckbox(task: Todo) {
        todoViewModel.updateTodo(task)
    }
}