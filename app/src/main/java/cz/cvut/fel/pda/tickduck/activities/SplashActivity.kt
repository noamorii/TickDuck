package cz.cvut.fel.pda.tickduck.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import cz.cvut.fel.pda.tickduck.databinding.ActivitySplashBinding
import cz.cvut.fel.pda.tickduck.utils.SharedPreferencesKeys.CURRENT_USER_ID
import cz.cvut.fel.pda.tickduck.utils.SharedPreferencesKeys.CURRENT_USER_PREFERENCES

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    companion object {
        private const val ANIM_DURATION = 1L //todo
    }

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //todo
        val sp = this.getSharedPreferences(CURRENT_USER_PREFERENCES, MODE_PRIVATE)
        val editor = sp.edit()
        editor.putInt(CURRENT_USER_ID, 1)
        editor.apply()

        binding.welcomeText.alpha = 0f
        binding.welcomeText.animate().apply {
            duration = ANIM_DURATION
            alpha(1f)
            withEndAction {
                startActivity(
                    Intent(this@SplashActivity, MainActivity::class.java)
                )
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
            }
        }
    }
}