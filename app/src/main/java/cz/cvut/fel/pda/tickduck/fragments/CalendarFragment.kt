package cz.cvut.fel.pda.tickduck.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cz.cvut.fel.pda.tickduck.MainApp
import cz.cvut.fel.pda.tickduck.activities.NewTodoActivity
import cz.cvut.fel.pda.tickduck.adapters.CalendarAdapter
import cz.cvut.fel.pda.tickduck.databinding.NewCalednarFragmentBinding
import cz.cvut.fel.pda.tickduck.db.viewmodels.MainViewModel
import cz.cvut.fel.pda.tickduck.db.viewmodels.factories.MainViewModelFactory
import cz.cvut.fel.pda.tickduck.model.Todo
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter


class CalendarFragment : BaseFragment(), CalendarAdapter.OnItemListener {

    private lateinit var binding: NewCalednarFragmentBinding
    private lateinit var adapter: CalendarAdapter
    private lateinit var editLauncher: ActivityResultLauncher<Intent>
    private lateinit var monthYearText: TextView
    private lateinit var calendarRecyclerView: RecyclerView
    private var selectedDate = LocalDate.now()

    companion object {
        @JvmStatic
        fun newInstance() = CalendarFragment()
    }

    private val mainViewModel: MainViewModel by activityViewModels {
        MainViewModelFactory((context?.applicationContext as MainApp).database)
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
        binding = NewCalednarFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initWidgets()
        setMonthView()
        setButtonsListener()
    }

    private fun setButtonsListener() {
        binding.buttonLeft.setOnClickListener {
            previousMonthAction(view)
        }
        binding.buttonRight.setOnClickListener{
            nextMonthAction(view)
        }
    }

    private fun initWidgets() {
        calendarRecyclerView = binding.calendarRecyclerView
        monthYearText = binding.monthYearTV
    }

    private fun setMonthView() {
        monthYearText.text = monthYearFromDate(selectedDate)
        val daysInMonth: ArrayList<String> = daysInMonthArray(selectedDate)
        adapter = CalendarAdapter(daysInMonth, this)
        val layoutManager: RecyclerView.LayoutManager =
            GridLayoutManager(activity, 7)
        calendarRecyclerView.layoutManager = layoutManager
        calendarRecyclerView.adapter = adapter
    }

    private fun daysInMonthArray(date: LocalDate): ArrayList<String> {
        val daysInMonthArray: ArrayList<String> = ArrayList()
        val daysInMonth: Int = YearMonth.from(date).lengthOfMonth()
        val dayOfWeek = selectedDate.withDayOfMonth(1).dayOfWeek.value
        for (i in 1..42) {
            if (i <= dayOfWeek || i > daysInMonth + dayOfWeek) daysInMonthArray.add("")
            else daysInMonthArray.add((i - dayOfWeek).toString())
        }
        return daysInMonthArray
    }

    private fun monthYearFromDate(date: LocalDate): String? {
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMMM yyyy")
        return date.format(formatter)
    }

    override fun onItemClick(position: Int, dayText: String?) {
        if (!dayText.equals("")) {
            val message =
                "Selected Date " + dayText.toString() + " " + monthYearFromDate(selectedDate)
            Toast.makeText(context?.applicationContext, message, Toast.LENGTH_LONG).show()
        }
    }

    fun previousMonthAction(view: View?) {
        selectedDate = selectedDate.minusMonths(1)
        setMonthView()
    }

    fun nextMonthAction(view: View?) {
        selectedDate = selectedDate.plusMonths(1)
        setMonthView()
    }


}