package cz.cvut.fel.pda.tickduck.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import cz.cvut.fel.pda.tickduck.R
import cz.cvut.fel.pda.tickduck.databinding.ActivityNewTodoBinding
import cz.cvut.fel.pda.tickduck.model.Todo
import cz.cvut.fel.pda.tickduck.model.enums.FlagType

class NewTodoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewTodoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewTodoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        actionBarSetReturn()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.new_task_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.id_create) {
            setResult()
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
                null, binding.edTask.text.toString(),
                binding.edDescription.text.toString(),
                1, FlagType.BLUE,
                null, 1, false
                )
            )
        }
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun actionBarSetReturn() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }


}