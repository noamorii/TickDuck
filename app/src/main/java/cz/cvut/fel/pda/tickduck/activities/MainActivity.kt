package cz.cvut.fel.pda.tickduck.activities

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.*
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import cz.cvut.fel.pda.tickduck.R
import cz.cvut.fel.pda.tickduck.databinding.LeftNavigationDrawerBinding
import cz.cvut.fel.pda.tickduck.db.viewmodels.TodoViewModel
import cz.cvut.fel.pda.tickduck.fragments.CalendarFragment
import cz.cvut.fel.pda.tickduck.fragments.FragmentManager
import cz.cvut.fel.pda.tickduck.fragments.SettingsFragment
import cz.cvut.fel.pda.tickduck.fragments.TodoFragment
import cz.cvut.fel.pda.tickduck.model.Category
import cz.cvut.fel.pda.tickduck.utils.Vibrations


class MainActivity : AppCompatActivity() {

    private lateinit var binding: LeftNavigationDrawerBinding
    private val todoViewModel: TodoViewModel by viewModels {
        TodoViewModel.TodoViewModelFactory(this)
    }

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

    override fun onBackPressed() {
        //do nothing
    }

    private fun setNavigationListener() {
        binding.mainInclude.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.settings -> {
                    FragmentManager.setFragment(SettingsFragment(), this)
                }
                R.id.calendar -> {
                    FragmentManager.setFragment(CalendarFragment.newInstance(), this)
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
        dialog.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            attributes?.windowAnimations = R.style.DialogAnimation
            setGravity(Gravity.BOTTOM)
        }
        dialog.show()

        setListenerOnCreateButton(dialog)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    v.clearFocus()
                    val imm: InputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun setListenerOnCreateButton(dialog: Dialog) {
        val createButton: ImageButton = dialog.findViewById(R.id.create_category_button)
        createButton.setOnClickListener {
            val nameField = dialog.findViewById<EditText?>(R.id.newCategoryName)
            nameField.requestFocus() // todo
            val name: String = nameField.text.toString()

            if (!todoViewModel.categoryExists(name)) {
                todoViewModel.insertCategory(name)
                binding.drawerNavView.menu.add(name)
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Category \"$name\" already exists.", Toast.LENGTH_SHORT).show()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    Vibrations.vibrate(this)
                }
            }
        }
    }

    private fun loadCategories() {
        binding.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                todoViewModel.allCategoriesLiveData.observe(this@MainActivity){}
                if (!areCategoriesLoaded) {
                    val categories = todoViewModel.allCategoriesLiveData.value
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