package cz.cvut.fel.pda.tickduck.activities

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import cz.cvut.fel.pda.tickduck.databinding.ActivityLoginBinding
import cz.cvut.fel.pda.tickduck.db.viewmodels.UserViewModel
import cz.cvut.fel.pda.tickduck.utils.SharedPreferencesKeys.CURRENT_USER_ID
import cz.cvut.fel.pda.tickduck.utils.SharedPreferencesKeys.CURRENT_USER_PREFERENCES
import cz.cvut.fel.pda.tickduck.utils.Vibrations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext


class LoginActivity : AppCompatActivity() {

    private lateinit var sp: SharedPreferences
    private lateinit var binding: ActivityLoginBinding

    private val userViewModel: UserViewModel by viewModels {
        UserViewModel.UserViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sp = getSharedPreferences(CURRENT_USER_PREFERENCES, MODE_PRIVATE)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (sp.contains(CURRENT_USER_ID)) {
            startActivity(
                Intent(this@LoginActivity, MainActivity::class.java)
            )
        }

        registrationLink()
        handleLogin()
    }

    private fun registrationLink() {
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                startActivity(
                    Intent(this@LoginActivity, RegistrationActivity::class.java)
                )
            }
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }

        val spannableString = SpannableString("Don't have an account? Sing up")
        spannableString.setSpan(clickableSpan, 23, 30, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.loginLinkToSingUp.apply {
            text = spannableString
            movementMethod = LinkMovementMethod.getInstance()
            highlightColor = Color.TRANSPARENT
        }
    }

    private fun handleLogin() {
        val username = binding.loginUsername
        val password = binding.loginPassword
        val loginButton = binding.loginPasswordSubmit

        loginButton.setOnClickListener {
            if (username.text.toString() != "" && password.text.toString() != "") {
                runBlocking {
                    withContext(Dispatchers.IO) {
                        val foundUser = userViewModel.getByUsername(username.text.toString())
                        if (foundUser != null && foundUser.password == password.text.toString()) {
                            setToContext(foundUser.id!!)
                            startActivity(
                                Intent(this@LoginActivity, MainActivity::class.java)
                            )
                        } else {
                            this@LoginActivity.runOnUiThread {
                                Toast.makeText(this@LoginActivity, "Invalid login.", Toast.LENGTH_SHORT).show()
                                Vibrations.vibrate(this@LoginActivity)
                            }
                        }
                    }
                }
            } else {
                Toast.makeText(this@LoginActivity, "Please enter all fields.", Toast.LENGTH_SHORT).show()
                Vibrations.vibrate(this@LoginActivity)
            }
        }
    }

    private fun setToContext(userId: Int) {
        val editor = sp.edit()
        editor.putInt(CURRENT_USER_ID, userId)
        editor.apply()
    }
}