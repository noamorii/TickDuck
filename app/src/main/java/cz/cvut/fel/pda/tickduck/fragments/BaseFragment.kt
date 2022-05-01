package cz.cvut.fel.pda.tickduck.fragments

import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {
    abstract fun onClickNew()
}