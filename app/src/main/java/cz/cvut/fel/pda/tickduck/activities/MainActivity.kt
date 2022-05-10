package cz.cvut.fel.pda.tickduck.activities

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import cz.cvut.fel.pda.tickduck.R
import cz.cvut.fel.pda.tickduck.databinding.LeftNavigationDrawerBinding
import cz.cvut.fel.pda.tickduck.db.viewmodels.MainViewModel
import cz.cvut.fel.pda.tickduck.fragments.FragmentManager
import cz.cvut.fel.pda.tickduck.fragments.TodoFragment
import cz.cvut.fel.pda.tickduck.model.Category

class MainActivity : AppCompatActivity() {

    private lateinit var binding: LeftNavigationDrawerBinding
    private val viewModel: MainViewModel by viewModels()
    private var areCategoriesLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LeftNavigationDrawerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FragmentManager.setFragment(TodoFragment.newInstance(), this)
        setNavigationListener()
        loadCategories()
        setNavigationViewMenuListener()
    }

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
        binding.drawerCreateCategoryButton.setOnClickListener {
            showDialog()
        }
    }

    private fun showDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.new_category_popup)
        dialog.show()
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)

        setListenerOnCreateButton(dialog)
    }

    private fun setListenerOnCreateButton(dialog: Dialog) {
        val createButton: ImageButton = dialog.findViewById(R.id.create_category_button)
        createButton.setOnClickListener {
            val name: String = dialog.findViewById<EditText?>(R.id.newCategoryName).text.toString()
            if (!viewModel.categoryExists(name)) {
                viewModel.insertCategory(
                    Category(null, name, 0)
                )
                binding.drawerNavView.menu.add(name)
            }
            dialog.dismiss()
        }
    }

    private fun loadCategories() {
        binding.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                viewModel.allCategories.observe(this@MainActivity){}
                if (!areCategoriesLoaded) {
                    val categories = viewModel.allCategories.value
                    if (categories != null) {
                        for (category: Category in categories) {
                            binding.drawerNavView.menu.add(category.name)
                        }
                        if (binding.drawerNavView.menu.size() > 1) {
                            areCategoriesLoaded = true
                            binding.drawerLayout.removeDrawerListener(this)
                        }
                    }
                }
            }
            override fun onDrawerOpened(drawerView: View) {}
            override fun onDrawerStateChanged(newState: Int) {}
            override fun onDrawerClosed(drawerView: View) {}
        })
    }
}