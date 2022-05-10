package cz.cvut.fel.pda.tickduck.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import cz.cvut.fel.pda.tickduck.databinding.ActivitySplashBinding

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    companion object {
        private const val ANIM_DURATION = 1000L
    }

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

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