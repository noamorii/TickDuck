package cz.cvut.fel.pda.tickduck.activities

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import cz.cvut.fel.pda.tickduck.R
import cz.cvut.fel.pda.tickduck.databinding.LeftNavigationDrawerBinding
import cz.cvut.fel.pda.tickduck.db.viewmodels.CategoryViewModel
import cz.cvut.fel.pda.tickduck.db.viewmodels.TodoViewModel
import cz.cvut.fel.pda.tickduck.fragments.CalendarFragment
import cz.cvut.fel.pda.tickduck.fragments.FragmentManager
import cz.cvut.fel.pda.tickduck.fragments.SettingsFragment
import cz.cvut.fel.pda.tickduck.fragments.TodoFragment
import cz.cvut.fel.pda.tickduck.model.Category
import cz.cvut.fel.pda.tickduck.model.Todo
import cz.cvut.fel.pda.tickduck.model.User
import cz.cvut.fel.pda.tickduck.utils.BitmapConverter
import cz.cvut.fel.pda.tickduck.utils.Vibrations
import kotlinx.coroutines.runBlocking


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
        addCategoriesListener()
        loadUserData()

      //  binding.drawerNavView.setNavigationItemSelectedListener(this)
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
                    FragmentManager.setFragment(SettingsFragment(), this)
                }
                R.id.calendar -> {
                    FragmentManager.setFragment(CalendarFragment.newInstance(), this)
                }
                R.id.todayTodos -> {
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

    private fun setNavigationViewMenuListener() {
        binding.deleteCategory.setOnClickListener {

            val categoryList = mutableListOf<String>("Choose a category...")
            for (v in todoViewModel.allCategoriesLiveData.value!!)
                categoryList.add(v.name)

            val view: View = layoutInflater.inflate(R.layout.dialog_spinner, null)
            val spinner = view.findViewById<Spinner>(R.id.spinner)
            val adapter = ArrayAdapter(this,
                android.R.layout.simple_spinner_item,
                categoryList)

            AlertDialog.Builder(this, R.style.MyDialogTheme)
                .setTitle("Choose category to delete")
                .setPositiveButton("Delete") { p0, p1 ->
                    if (spinner.selectedItem != "Choose a category...") {
                        runBlocking {
                            val catId = categoryViewModel.getByName(spinner.selectedItem.toString())?.id
                            if (catId != null) {
                                categoryViewModel.delete(catId)
                                changeNavigationDrawerContent()
                              //  binding.drawerLayout.closeDrawer(binding.drawerLayout)
                            }
                        }
                    }
                }
                .setNegativeButton("Cancel") { p0, p1 ->
                    p0?.dismiss() }
                .setView(view)
                .create()
                .show()

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
            spinner.adapter = adapter
        }
        binding.drawerCreateCategoryButton.setOnClickListener {
            showDialog()
        }
    }

    private fun changeNavigationDrawerContent() {

        binding.drawerNavView.menu.clear()
        todoViewModel.allCategoriesLiveData.observe(this@MainActivity){
            //val categories = todoViewModel.allCategoriesLiveData.value
//            if (categories != null) {
//                for (category: Category in categories) {
//                    binding.drawerNavView.menu.add(category.name)
//                }
//            }
            it.forEach { cat ->
                binding.drawerNavView.menu.add(cat.name)
            }

        }




//        areCategoriesLoaded = false
//        addCategoriesListener()
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
            val name: String = nameField.text.toString()

            if (!todoViewModel.categoryExists(name)) {
                todoViewModel.insertCategory(name)
                binding.drawerNavView.menu.add(name)
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Category \"$name\" already exists.", Toast.LENGTH_SHORT).show()
                Vibrations.vibrate(this)
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

    private fun loadCategories(): Boolean {

        todoViewModel.allCategoriesLiveData.observe(this@MainActivity){}
        if (!areCategoriesLoaded) {
            val categories = todoViewModel.allCategoriesLiveData.value
            if (categories != null) {
                for (category: Category in categories) {
                    binding.drawerNavView.menu.add(category.name)
                }
                if (binding.drawerNavView.menu.size() > 1) {
                    areCategoriesLoaded = true
                    return true
                } else {
                    return false
                }
            } else {
                return false
            }
        } else {
            return true
        }
    }

    private fun addCategoriesListener() {
        binding.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                if (loadCategories()) binding.drawerLayout.removeDrawerListener(this)
            }
            override fun onDrawerOpened(drawerView: View) {}
            override fun onDrawerStateChanged(newState: Int) {}
            override fun onDrawerClosed(drawerView: View) {}
        })
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        println(item)
        if (item.toString() == "All categories") {
            TodoFragment.adapter.submitList(todoViewModel.allTodosLiveData.value)
            return true
        }
        TodoFragment.adapter.submitList(searchByCategory(item.toString()))
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