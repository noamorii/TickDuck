package cz.cvut.fel.pda.tickduck.activities

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import cz.cvut.fel.pda.tickduck.R
import cz.cvut.fel.pda.tickduck.databinding.ActivityDrawerBinding
import cz.cvut.fel.pda.tickduck.db.viewmodels.MainViewModel
import cz.cvut.fel.pda.tickduck.fragments.FragmentManager
import cz.cvut.fel.pda.tickduck.fragments.TodoFragment
import cz.cvut.fel.pda.tickduck.model.Category
import kotlinx.coroutines.CoroutineScope

class TodoMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDrawerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDrawerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setNavigationListener()
        FragmentManager.setFragment(TodoFragment.newInstance(), this)
        setNavigationViewMenuListener()
    }

    private val viewModel: MainViewModel by viewModels()

    private fun setNavigationListener() {
        binding.mainInclude.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.settings -> {
                    Log.d("Log", "Settings")
                }
                R.id.calendar -> {
                    Log.d("Log", "Calendar")
                }
                R.id.todayTodos -> {
                    FragmentManager.setFragment(TodoFragment.newInstance(), this)
                }
            }
            true
        }

        binding.mainInclude.fab.setOnClickListener {
            FragmentManager.currentFragment?.onClickNew()
            Log.d("Log", "New Todo")
        }
    }

    private fun setNavigationViewMenuListener() {
        binding.categoryButton.setOnClickListener {
            showDialog()
        }
    }

    private fun showDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.activity_new_category)
        dialog.show()
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)

        setListenerOnCreate(dialog)
    }

    private fun setListenerOnCreate(dialog: Dialog) {
        val createButton: ImageButton = dialog.findViewById(R.id.create_category_button)
        createButton.setOnClickListener {
            val name: String = dialog.findViewById<EditText?>(R.id.newCategoryName).text.toString()
            if (!viewModel.categoryExists(name)) {
                viewModel.insertCategory(
                    Category(null, name, 0)
                )
                binding.navView.menu.add(name)
                dialog.dismiss()
            }
        }
    }
}