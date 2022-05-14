package cz.cvut.fel.pda.tickduck.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import cz.cvut.fel.pda.tickduck.R
import cz.cvut.fel.pda.tickduck.databinding.ActivityTodoDetailBinding
import cz.cvut.fel.pda.tickduck.db.viewmodels.CategoryViewModel
import cz.cvut.fel.pda.tickduck.model.Todo
import cz.cvut.fel.pda.tickduck.utils.FormatPatterns.DATE_PATTERN
import cz.cvut.fel.pda.tickduck.utils.SerializableExtras.TODO_DETAIL
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TodoDetailActivity : AppCompatActivity() {

    private val categoryViewModel: CategoryViewModel by viewModels {
        CategoryViewModel.CategoryViewModelFactory(this)
    }

    private val dateTimeDateFormatter = DateTimeFormatter.ofPattern(DATE_PATTERN)
    private lateinit var editLauncher: ActivityResultLauncher<Intent>
    private lateinit var binding: ActivityTodoDetailBinding

    private var todo: Todo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTodoDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar.root)
        supportActionBar?.title = "Detail"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        initializeTodo()
        onEditResult()
        fillData()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.detail_toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        } else if (item.itemId == R.id.id_edit) {
            editLauncher.launch(
                Intent(this, EditTodoActivity::class.java).apply {
                    putExtra(TODO_DETAIL, todo!!)
            })
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initializeTodo() {
        todo = intent.getSerializableExtra(TODO_DETAIL) as Todo
    }

    private fun onEditResult() {
        editLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                todo = it.data?.getSerializableExtra(TODO_DETAIL) as Todo
                fillData()
            }
        }
    }

    private fun fillData() = with(binding) {
        todo?.apply {

            runBlocking {
                todoCategory.text = categoryViewModel.getById(categoryId)?.name
            }

            date?.apply {
                TodoDate.text = LocalDate.parse(this).format(dateTimeDateFormatter)
            }
            TodoDescription.text = description
            TodoName.setTextColor(priorityEnum.toArgb(this@TodoDetailActivity))
            TodoName.text = name
        }
    }
}