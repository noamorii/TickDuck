package cz.cvut.fel.pda.tickduck.activities

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import cz.cvut.fel.pda.tickduck.R
import cz.cvut.fel.pda.tickduck.databinding.ActivityNewTodoBinding
import cz.cvut.fel.pda.tickduck.db.viewmodels.TodoViewModel
import cz.cvut.fel.pda.tickduck.model.Todo
import cz.cvut.fel.pda.tickduck.model.enums.PriorityEnum
import cz.cvut.fel.pda.tickduck.utils.FormatPatterns
import cz.cvut.fel.pda.tickduck.utils.SerializableExtras
import cz.cvut.fel.pda.tickduck.utils.Vibrations
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

class EditTodoActivity : AppCompatActivity() {

    private val todoViewModel: TodoViewModel by viewModels {
        TodoViewModel.TodoViewModelFactory(this)
    }

    private lateinit var binding: ActivityNewTodoBinding

    private val calendar = Calendar.getInstance()
    private var localDate: LocalDate? = null
    private var todo: Todo? = null
    private var priority: PriorityEnum? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewTodoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar.root)
        supportActionBar?.title = "Edit todo"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initData()
        initCategorySpinner()
        initCalendar()
        setButtonClearListener()
        setButtonPriorityListener()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.new_todo_toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.id_create) {
            if (validate()) {
                setToTodoEntity()
                todoViewModel.updateTodo(todo!!)
                setResult()
                finish()
            }
        }

        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initData() {
        todo = intent.getSerializableExtra(SerializableExtras.TODO_DETAIL) as Todo

        todo?.apply {
            priority = priorityEnum
            setPriority()

            binding.edTask.setText(name)

            date?.apply {
                localDate = LocalDate.parse(date)
                localDate?.apply {
                    calendar[Calendar.YEAR] = year
                    calendar[Calendar.MONTH] = monthValue - 1
                    calendar[Calendar.DAY_OF_MONTH] = dayOfMonth
                    updateLabel()
                }
            }
        }
    }

    private fun validate(): Boolean {
        return if (binding.edTask.text.toString() == "") {
            Toast.makeText(this@EditTodoActivity, "Title cannot be empty.", Toast.LENGTH_SHORT).show()
            Vibrations.vibrate(this@EditTodoActivity)
            false
        } else true
    }

    private fun setToTodoEntity() {
        todo?.apply {
            name = binding.edTask.text.toString()
            description = binding.edDescription.text.toString()
            priorityEnum = priority!!
            date = localDate?.toString()
            categoryId = (binding.edCategory.selectedItem as NewTodoActivity.CategoryWrapper).category.id!!
        }

    }

    private fun setResult() {
        val intent = Intent().apply {
            putExtra(
                SerializableExtras.TODO_DETAIL, todo!!)
        }
        setResult(RESULT_OK, intent)
    }

    private fun initCategorySpinner() {
        val categoryList = mutableListOf<NewTodoActivity.CategoryWrapper>()
        val adapter = ArrayAdapter(
            this@EditTodoActivity,
            android.R.layout.simple_spinner_dropdown_item,
            categoryList
        )
        binding.edCategory.adapter = adapter

        todoViewModel.allCategoriesLiveData.observe(this) {
            it.forEach { cat ->
                categoryList.add(NewTodoActivity.CategoryWrapper(cat))
            }
            adapter.notifyDataSetChanged()

            categoryList.forEach { catWrap ->
                if (todo!!.categoryId == catWrap.category.id) {
                    binding.edCategory.setSelection(adapter.getPosition(catWrap))
                    return@forEach
                }
            }
        }
    }

    private fun initCalendar() {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            calendar.set(year, month, day)
            updateLabel()
            localDate = LocalDate.of(
                calendar[Calendar.YEAR],
                calendar[Calendar.MONTH] + 1,
                calendar[Calendar.DAY_OF_MONTH]
            )
        }

        binding.edDate.setOnClickListener {
            DatePickerDialog(this,
                dateSetListener,
                calendar[Calendar.YEAR],
                calendar[Calendar.MONTH],
                calendar[Calendar.DAY_OF_MONTH]
            ).show()
        }
    }

    private fun updateLabel() {
        binding.edDate.setText(
            SimpleDateFormat(FormatPatterns.DATE_PATTERN, Locale.ENGLISH).format(calendar.time)
        )

        binding.clearDateButton.apply {
            setTextColor(Color.WHITE)
            isClickable = true
        }
    }

    private fun setButtonClearListener() {
        binding.clearDateButton.setOnClickListener {
            binding.edDate.text.clear()
            binding.clearDateButton.apply {
                setTextColor(ContextCompat.getColor(this@EditTodoActivity, R.color.cardview_dark_background))
                isClickable = false
            }
            localDate = null
        }
    }

    private fun setButtonPriorityListener() {
        binding.priorityButton.setOnClickListener {
            Dialog(this).apply {
                requestWindowFeature(Window.FEATURE_NO_TITLE)
                setContentView(R.layout.priority_popup)
                window?.apply {
                    setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    attributes?.windowAnimations = R.style.DialogAnimation
                }

                setPriorityOfPopupPriorityDialog(this, R.id.low_priority, PriorityEnum.LOW)
                setPriorityOfPopupPriorityDialog(this, R.id.medium_priority, PriorityEnum.MEDIUM)
                setPriorityOfPopupPriorityDialog(this, R.id.high_priority, PriorityEnum.HIGH)

                show()
            }
        }
    }

    private fun setPriorityOfPopupPriorityDialog(dialog: Dialog, id: Int, priority: PriorityEnum) {
        dialog.findViewById<Button>(id).setOnClickListener {
            this.priority = priority
            setPriority()
            dialog.hide()
        }
    }

    private fun setPriority() {
        binding.priorityButton.apply {
            text = priority!!.text
            setTextColor(priority!!.toArgb(this@EditTodoActivity))
        }
    }
}