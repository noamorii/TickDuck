package cz.cvut.fel.pda.tickduck.activities

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import cz.cvut.fel.pda.tickduck.R
import cz.cvut.fel.pda.tickduck.databinding.LeftNavigationDrawerBinding
import cz.cvut.fel.pda.tickduck.db.viewmodels.CategoryViewModel
import cz.cvut.fel.pda.tickduck.db.viewmodels.TodoViewModel
import cz.cvut.fel.pda.tickduck.fragments.CalendarFragment
import cz.cvut.fel.pda.tickduck.fragments.FragmentManager
import cz.cvut.fel.pda.tickduck.fragments.SettingsFragment
import cz.cvut.fel.pda.tickduck.fragments.TodoFragment
import cz.cvut.fel.pda.tickduck.model.Todo
import cz.cvut.fel.pda.tickduck.model.User
import cz.cvut.fel.pda.tickduck.utils.BitmapConverter
import cz.cvut.fel.pda.tickduck.utils.Vibrations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: LeftNavigationDrawerBinding

    private val todoViewModel: TodoViewModel by viewModels {
        TodoViewModel.TodoViewModelFactory(this)
    }

    private val categoryViewModel: CategoryViewModel by viewModels {
        CategoryViewModel.CategoryViewModelFactory(this)
    }

    private var areCategoriesLoaded = false
    private var isPictureSet = false
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LeftNavigationDrawerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FragmentManager.setFragment(TodoFragment.newInstance(), this)
        setNavigationListener()

        setNavigationViewMenuListener()
        user = todoViewModel.loggedUser!!
        loadUser(user)
        loadCategories()
        loadUserData()

        binding.drawerNavView.setNavigationItemSelectedListener(this)
    }

    private fun loadUser(user: User) {
        binding.drawerNavView.getHeaderView(0).findViewById<TextView>(R.id.textView).text = user.username
        if (user.profilePicture != null)
            user.profilePicture.apply {
                binding.drawerNavView.getHeaderView(0).findViewById<ImageView>(R.id.imageView)
                    .setImageBitmap(this?.let { BitmapConverter.convert(it) })
                isPictureSet = true
            }
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }

    private fun setNavigationListener() {
        binding.mainInclude.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.settings -> {
                    setFabColor(R.color.fab_disable)
                    FragmentManager.setFragment(SettingsFragment(), this)
                }
                R.id.calendar -> {
                    setFabColor(R.color.main_color)
                    FragmentManager.setFragment(CalendarFragment.newInstance(), this)
                }
                R.id.todayTodos -> {
                    setFabColor(R.color.main_color)
                    if (FragmentManager.currentFragment !is TodoFragment) {
                        FragmentManager.setFragment(TodoFragment.newInstance(), this)
                    }
                }
            }
            true
        }

        binding.mainInclude.fab.setOnClickListener {
            FragmentManager.currentFragment?.onClickNew()
        }
    }

    private fun setFabColor(color: Int) {
        findViewById<FloatingActionButton>(R.id.fab).apply {
            this.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this@MainActivity, color))
        }
    }

    private fun setNavigationViewMenuListener() {
        binding.deleteCategory.setOnClickListener {
            val view: View = layoutInflater.inflate(R.layout.dialog_spinner, null)

            val categoryList = mutableListOf<NewTodoActivity.CategoryWrapper>()
            val spinner = view.findViewById<Spinner>(R.id.spinner)
            val adapter = ArrayAdapter(
                this@MainActivity,
                android.R.layout.simple_spinner_dropdown_item,
                categoryList
            )
            spinner.adapter = adapter

            categoryViewModel.categoriesLiveData.observe(this) {
                it.forEach { cat ->
                    if (cat.name != "Inbox") categoryList.add(NewTodoActivity.CategoryWrapper(cat))
                }
                adapter.notifyDataSetChanged()
            }

            AlertDialog.Builder(this, R.style.MyDialogTheme)
                .setTitle("Choose category to delete. Also todos will be deleted.")
                .setPositiveButton("Delete") { p0, p1 ->
                    val toDelete = spinner.selectedItem as NewTodoActivity.CategoryWrapper
                    if (toDelete.category.name != "Inbox") {
                        todoViewModel.deleteCategory(toDelete.category.id!!)
                        binding.drawerNavView.menu.removeItem(toDelete.category.id)
                    } else {
                        Toast.makeText(this@MainActivity, "Category \"Inbox\" cannot be deleted.", Toast.LENGTH_SHORT).show()
                    }

                }
                .setNegativeButton("Cancel") { p0, p1 -> p0?.dismiss() }
                .setView(view)
                .create()
                .show()
        }
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
        dialog.findViewById<ImageButton>(R.id.create_category_button).setOnClickListener {
            val nameField = dialog.findViewById<EditText?>(R.id.newCategoryName)
            val name: String = nameField.text.toString()

            runBlocking {
                withContext(Dispatchers.IO) {
                    if (name == "") {
                        this@MainActivity.runOnUiThread {
                            Toast.makeText(this@MainActivity, "Cannot add empty category name.", Toast.LENGTH_SHORT).show()
                            Vibrations.vibrate(this@MainActivity)
                        }
                    } else if (categoryViewModel.getByName(name) == null) {
                        todoViewModel.insertCategory(name)
                        val persistedCat = categoryViewModel.getByName(name)
                        this@MainActivity.runOnUiThread {
                            persistedCat?.apply {
                                binding.drawerNavView.menu.add(Menu.NONE, this.id!!, Menu.NONE, this.name)
                            }
                        }
                        dialog.dismiss()
                    } else {
                        this@MainActivity.runOnUiThread {
                            Toast.makeText(this@MainActivity, "Category \"$name\" already exists.", Toast.LENGTH_SHORT).show()
                            Vibrations.vibrate(this@MainActivity)
                        }
                    }
                }
            }
        }
    }

    private fun loadUserData() {
        binding.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                if (!isPictureSet && user.profilePicture != null) {
                    user.profilePicture.apply {
                        binding.drawerNavView.getHeaderView(0).findViewById<ImageView>(R.id.imageView)
                            .setImageBitmap(this?.let { BitmapConverter.convert(it) })
                        isPictureSet = true
                    }
                    binding.drawerLayout.removeDrawerListener(this)
                }
            }
            override fun onDrawerOpened(drawerView: View) {}
            override fun onDrawerClosed(drawerView: View) {}
            override fun onDrawerStateChanged(newState: Int) {}
        })
    }

    private fun loadCategories() {
        runBlocking {
            withContext(Dispatchers.IO) {
                categoryViewModel.getAll().forEach {
                    binding.drawerNavView.menu.add(Menu.NONE, it.id!!, Menu.NONE, it.name)
                }
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (item.toString() == "All categories") {
            TodoFragment.adapter.submitList(todoViewModel.allTodosLiveData.value)
            findViewById<TextView>(R.id.textView4).text = item.toString()
            return true
        }
        TodoFragment.adapter.submitList(searchByCategory(item.toString()))
        findViewById<TextView>(R.id.textView4).text = item.toString()
        return true
    }

    private fun searchByCategory(category: String): ArrayList<Todo> {
        val listByCategory : ArrayList<Todo> = ArrayList()
        for (v in todoViewModel.allTodosLiveData.value!!)
            runBlocking {
                val name = categoryViewModel.getById(v.categoryId)?.name
                if (name == category) listByCategory.add(v)
            }
        return listByCategory
    }

}