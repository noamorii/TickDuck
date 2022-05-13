package cz.cvut.fel.pda.tickduck.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import cz.cvut.fel.pda.tickduck.R
import cz.cvut.fel.pda.tickduck.databinding.ActivityNewTodoBinding
import cz.cvut.fel.pda.tickduck.databinding.ActivityTodoDetailBinding
import cz.cvut.fel.pda.tickduck.db.viewmodels.CategoryViewModel
import cz.cvut.fel.pda.tickduck.db.viewmodels.UserViewModel
import cz.cvut.fel.pda.tickduck.model.Todo
import kotlinx.coroutines.runBlocking

class TodoDetailActivity : AppCompatActivity() {
    private var todo: Todo? = null
    private lateinit var binding: ActivityTodoDetailBinding

    private val categoryViewModel: CategoryViewModel by viewModels {
        CategoryViewModel.CategoryViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTodoDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar.root)
        supportActionBar?.title = "Todo"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        initializeTodo()
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
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initializeTodo() {
        todo = intent.getSerializableExtra("todo_detail") as Todo
    }

    private fun fillData() = with(binding) {
        if (todo != null) {
            runBlocking {
                todoCategory.text = categoryViewModel.getById(todo!!.idCategory)?.name
            }
            TodoDate.text = todo?.date
            TodoDescription.text = todo?.description
            TodoName.text = todo?.name
        }
    }
}