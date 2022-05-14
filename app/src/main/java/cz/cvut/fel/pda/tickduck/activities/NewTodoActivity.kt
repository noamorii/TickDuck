package cz.cvut.fel.pda.tickduck.activities

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import cz.cvut.fel.pda.tickduck.R
import cz.cvut.fel.pda.tickduck.databinding.ActivityNewTodoBinding
import cz.cvut.fel.pda.tickduck.db.viewmodels.CategoryViewModel
import cz.cvut.fel.pda.tickduck.model.Category
import cz.cvut.fel.pda.tickduck.model.enums.PriorityEnum
import cz.cvut.fel.pda.tickduck.model.intentDTO.NewTodoDTO
import cz.cvut.fel.pda.tickduck.utils.FormatPatterns.DATE_PATTERN
import cz.cvut.fel.pda.tickduck.utils.SerializableExtras.NEW_TODO_DTO
import cz.cvut.fel.pda.tickduck.utils.Vibrations
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*


class NewTodoActivity : AppCompatActivity() {

    private val categoryViewModel: CategoryViewModel by viewModels {
        CategoryViewModel.CategoryViewModelFactory(this)
    }

    private lateinit var binding: ActivityNewTodoBinding

    private var localDate: LocalDate? = null
    private var priority: PriorityEnum = PriorityEnum.MEDIUM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewTodoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar.root)
        supportActionBar?.title = "Create new todo"
        supportActionBar?.setDisplayHomeAsUpEnabled(true) //back

        initCategorySpinner()
        initCalendar()
        setPriority()
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
                setResult()
                finish()
            }
        }

        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun validate(): Boolean {
        return if (binding.edTask.text.toString() == "") {
            Toast.makeText(this@NewTodoActivity, "Title cannot be empty.", Toast.LENGTH_SHORT).show()
            Vibrations.vibrate(this@NewTodoActivity)
            false
        } else true
    }

    private fun setResult() {
        val intent = Intent().apply {
            putExtra(NEW_TODO_DTO, NewTodoDTO(
                name = binding.edTask.text.toString(),
                description = binding.edDescription.text.toString(),
                priorityEnum = priority,
                date = localDate?.toString(),
                idCategory = (binding.edCategory.selectedItem as CategoryWrapper).category.id!!
                )
            )
        }
        setResult(RESULT_OK, intent)
    }

    private fun initCategorySpinner() {
        val categoryList = mutableListOf<CategoryWrapper>()
        val adapter = ArrayAdapter(
            this@NewTodoActivity,
            android.R.layout.simple_spinner_dropdown_item,
            categoryList
        )
        binding.edCategory.adapter = adapter

        categoryViewModel.categoriesLiveData.observe(this) {
            it.forEach { cat ->
                categoryList.add(CategoryWrapper(cat))
            }
            adapter.notifyDataSetChanged()
        }
    }

    private fun initCalendar() {
        val edDateField = binding.edDate
        val clearButton = binding.clearDateButton
        val calendar = Calendar.getInstance()

        val updateLabel = { pattern: String ->
            edDateField.setText(
                SimpleDateFormat(pattern, Locale.ENGLISH)
                    .format(calendar.time)
            )
            clearButton.apply {
                setTextColor(Color.WHITE)
                isClickable = true
            }
        }

        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            calendar.set(year, month, day)
            updateLabel(DATE_PATTERN)
            localDate = LocalDate.of(
                calendar[Calendar.YEAR],
                calendar[Calendar.MONTH] + 1,
                calendar[Calendar.DAY_OF_MONTH]
            )
        }

        edDateField.setOnClickListener {
            DatePickerDialog(this,
                dateSetListener,
                calendar[Calendar.YEAR],
                calendar[Calendar.MONTH],
                calendar[Calendar.DAY_OF_MONTH]
            ).show()
        }
    }

    private fun setButtonClearListener() {
        binding.clearDateButton.setOnClickListener {
            binding.edDate.text.clear()
            binding.clearDateButton.apply {
                setTextColor(ContextCompat.getColor(this@NewTodoActivity, R.color.cardview_dark_background))
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
            text = priority.text
            setTextColor(priority.toArgb(this@NewTodoActivity))
        }
    }

    class CategoryWrapper(
        val category: Category
    ) {
        override fun toString(): String {
            return category.name
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as CategoryWrapper

            if (category != other.category) return false

            return true
        }

        override fun hashCode(): Int {
            return category.hashCode()
        }

    }
}