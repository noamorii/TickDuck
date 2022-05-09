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
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.*


class NewTodoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewTodoBinding

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
        menuInflater.inflate(R.menu.new_task_menu, menu)
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
                null,
                binding.edTask.text.toString(),
                binding.edDescription.text.toString(),
                1,
                FlagType.BLUE, //todo
                null,
                binding.edCategory.id,
                false
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
        val calendar = Calendar.getInstance()
        val edDateField = binding.edDate

        val updateDateLabel = {
            edDateField.setText(
                SimpleDateFormat("dd.MM.", Locale.ENGLISH)
                    .format(calendar.time)
            )
        }

        val updateDateTimeLabel = {
            edDateField.setText(
                SimpleDateFormat("dd.MM., HH:mm aa", Locale.ENGLISH)
                    .format(calendar.time)
            )
        }

        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            updateDateTimeLabel()
        }

        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            calendar.set(year, month, day)
            updateDateLabel()
        }

        val timePicker = TimePickerDialog(
            this,
            timeSetListener,
            calendar[Calendar.HOUR],
            calendar[Calendar.MINUTE],
            false
        )

        edDateField.setOnClickListener {
            val a = DatePickerDialog(
                this,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            a.setButton(DialogInterface.BUTTON_NEUTRAL, "Time") { _, _ ->
                timePicker.show()
            }
            a.show()
        }
    }
}