package cz.cvut.fel.pda.tickduck.activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import cz.cvut.fel.pda.tickduck.R
import cz.cvut.fel.pda.tickduck.databinding.ActivityNewTodoBinding
import cz.cvut.fel.pda.tickduck.db.viewmodels.CategoryViewModel
import cz.cvut.fel.pda.tickduck.model.Category
import cz.cvut.fel.pda.tickduck.model.enums.FlagType
import cz.cvut.fel.pda.tickduck.model.intentDTO.NewTodoDTO
import cz.cvut.fel.pda.tickduck.utils.SerializableExtras.NEW_TODO_DTO
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.util.*


class NewTodoActivity : AppCompatActivity() {

    companion object {
        private const val DATE_PATTERN = "dd.MM.yyyy"
    }

    private val categoryViewModel: CategoryViewModel by viewModels {
        CategoryViewModel.CategoryViewModelFactory(this)
    }

    private lateinit var binding: ActivityNewTodoBinding

    private var localDate: LocalDate? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewTodoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar.root)
        supportActionBar?.title = "Create new todo"
        supportActionBar?.setDisplayHomeAsUpEnabled(true) //back

        initCategorySpinner()
        initCalendar()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.new_todo_toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.id_create) {
            setResult()
            finish()
        }

        if (item.itemId == android.R.id.home) {
            finish()
            //back
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setResult() {
        val intent = Intent().apply {
            putExtra(NEW_TODO_DTO, NewTodoDTO(
                name = binding.edTask.text.toString(),
                description = binding.edDescription.text.toString(),
                flagInfo = FlagType.BLUE,
                date = localDate.toString(),
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
        val calendar = Calendar.getInstance()

        val updateLabel = { pattern: String ->
            edDateField.setText(
                SimpleDateFormat(pattern, Locale.ENGLISH)
                    .format(calendar.time)
            )
        }

        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            calendar.set(year, month, day)
            updateLabel(DATE_PATTERN)
            localDate = LocalDate.of(
                calendar[Calendar.YEAR],
                calendar[Calendar.MONTH],
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

    class CategoryWrapper(
        val category: Category
    ) {
        override fun toString(): String {
            return category.name
        }
    }
}