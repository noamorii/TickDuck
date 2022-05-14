package cz.cvut.fel.pda.tickduck.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import cz.cvut.fel.pda.tickduck.databinding.ActivityRegistrationBinding
import cz.cvut.fel.pda.tickduck.db.viewmodels.UserViewModel
import cz.cvut.fel.pda.tickduck.model.User
import cz.cvut.fel.pda.tickduck.utils.SharedPreferencesKeys.CURRENT_USER_ID
import cz.cvut.fel.pda.tickduck.utils.SharedPreferencesKeys.CURRENT_USER_PREFERENCES
import cz.cvut.fel.pda.tickduck.utils.Vibrations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class RegistrationActivity : AppCompatActivity() {

    private lateinit var sp: SharedPreferences
    private lateinit var binding: ActivityRegistrationBinding

    private val userViewModel: UserViewModel by viewModels {
        UserViewModel.UserViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sp = getSharedPreferences(CURRENT_USER_PREFERENCES, MODE_PRIVATE)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.registrationToolbar)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = ""
        }

        handleLoginContext()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun handleLoginContext() {
        if (sp.contains(CURRENT_USER_ID)) {
            startActivity(
                Intent(this@RegistrationActivity, MainActivity::class.java)
            )
        } else {
            handleRegistration()
        }
    }

    private fun handleRegistration() {
        val username = binding.registrationUsername
        val password1 = binding.registrationPassword
        val password2 = binding.registrationPassword2
        val registrationButton = binding.registrationPasswordSubmit


        registrationButton.setOnClickListener {
            if (username.text.toString() != "" && password1.text.toString() != "" && password2.text.toString() != "") {
                runBlocking {
                    withContext(Dispatchers.IO) {
                        val foundUser = userViewModel.getByUsername(username.text.toString())
                        if (foundUser == null && password1.text.toString() != password2.text.toString()) {
                            this@RegistrationActivity.runOnUiThread {
                                Toast.makeText(this@RegistrationActivity, "Passwords need to be equal.", Toast.LENGTH_SHORT).show()
                                Vibrations.vibrate(this@RegistrationActivity)
                            }
                        } else if (foundUser == null) {

                            val newUser = userViewModel.insert(
                                User(
                                    username = username.text.toString(),
                                    password = password1.text.toString()
                                )
                            )
                            setToContext(newUser.id!!)

                            startActivity(
                                Intent(this@RegistrationActivity, MainActivity::class.java)
                            )

                        } else {
                            this@RegistrationActivity.runOnUiThread {
                                Toast.makeText(this@RegistrationActivity, "Username already exist.", Toast.LENGTH_SHORT).show()
                                Vibrations.vibrate(this@RegistrationActivity)
                            }
                        }
                    }
                }
            } else {
                Toast.makeText(this@RegistrationActivity, "Please enter all fields.", Toast.LENGTH_SHORT).show()
                Vibrations.vibrate(this@RegistrationActivity)
            }
        }
    }

    private fun setToContext(userId: Int) {
        val editor = sp.edit()
        editor.putInt(CURRENT_USER_ID, userId)
        editor.apply()
    }
}