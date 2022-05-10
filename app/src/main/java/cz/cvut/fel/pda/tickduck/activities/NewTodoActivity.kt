package cz.cvut.fel.pda.tickduck.activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import cz.cvut.fel.pda.tickduck.R
import cz.cvut.fel.pda.tickduck.databinding.ActivityNewTodoBinding
import cz.cvut.fel.pda.tickduck.model.Todo
import cz.cvut.fel.pda.tickduck.model.enums.FlagType
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.util.*


class NewTodoActivity : AppCompatActivity() {

    companion object {
        private const val DATE_PATTERN = "dd.MM."
        private const val DATE_TIME_PATTERN = "$DATE_PATTERN, HH:mm aa"
    }

    private lateinit var binding: ActivityNewTodoBinding

    private var localTime: LocalTime? = null
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
            putExtra("new_todo", Todo(
                name = binding.edTask.text.toString(),
                description = binding.edDescription.text.toString(),
                flagInfo = FlagType.BLUE,
                userId = 1,
                time = localTime?.toString(),
                date = localDate?.toString(),
                imgName = "idk",
                idCategory = binding.edCategory.id
                )
            )
        }
        setResult(RESULT_OK, intent)
    }

    private fun initCategorySpinner() {
        binding.edCategory.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            listOf("cat_A", "cat_B", "cat_C") //todo
        )
    }

    private fun initCalendar() {
        val edDateField = binding.edDate
        val calendar = Calendar.getInstance()

        val setLocalDate = {
            localDate = LocalDate.of(
                calendar[Calendar.YEAR],
                calendar[Calendar.MONTH],
                calendar[Calendar.DAY_OF_MONTH]
            )
        }

        val setLocalDateAndTime = {
            setLocalDate()
            localTime = LocalTime.of(
                calendar[Calendar.HOUR],
                calendar[Calendar.MINUTE]
            )
        }

        val updateLabel = { pattern: String ->
            edDateField.setText(
                SimpleDateFormat(pattern, Locale.ENGLISH)
                    .format(calendar.time)
            )
        }

        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            calendar.set(year, month, day)
            updateLabel(DATE_PATTERN)
            setLocalDate()
        }

        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            updateLabel(DATE_TIME_PATTERN)
            setLocalDateAndTime()
        }

        edDateField.setOnClickListener {
            DatePickerDialog(this,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).apply {
                this.setButton(DialogInterface.BUTTON_NEUTRAL, "Time") { _, _ ->
                    TimePickerDialog(this@NewTodoActivity,
                        timeSetListener,
                        calendar[Calendar.HOUR],
                        calendar[Calendar.MINUTE],
                        false
                    ).show()
                }
                this.show()
            }
        }
    }
}