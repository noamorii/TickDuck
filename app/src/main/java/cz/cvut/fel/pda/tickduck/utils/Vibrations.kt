package cz.cvut.fel.pda.tickduck.utils

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.VibratorManager
import androidx.annotation.RequiresApi

object Vibrations {
    @RequiresApi(Build.VERSION_CODES.S)
    fun vibrate(context: Context) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator.vibrate(
            VibrationEffect.createOneShot(
                250, VibrationEffect.DEFAULT_AMPLITUDE
            )
        )
    }
}