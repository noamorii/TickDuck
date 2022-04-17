package cz.cvut.fel.pda.tickduck.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import cz.cvut.fel.pda.tickduck.R
import cz.cvut.fel.pda.tickduck.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setNavigationListener()
    }

    private fun setNavigationListener() {

        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.settings -> {
                    Log.d("Log", "Settings")
                }
                R.id.calendar -> {
                    Log.d("Log", "Calendar")
                }
                R.id.todayTodos -> {
                    Log.d("Log", "Todos")
                }
            }
            true
        }

        binding.fab.setOnClickListener {
            Log.d("Log", "New Todo")
        }
    }
}