package cz.cvut.fel.pda.tickduck.fragments

import androidx.appcompat.app.AppCompatActivity
import cz.cvut.fel.pda.tickduck.R

object FragmentManager {

    var currentFragment: BaseFragment? = null

    fun setFragment(newFragment: BaseFragment, activity: AppCompatActivity) {
        val transaction = activity.supportFragmentManager.beginTransaction()

        transaction.replace(R.id.frame, newFragment)
        transaction.commit()

        currentFragment = newFragment
    }

}