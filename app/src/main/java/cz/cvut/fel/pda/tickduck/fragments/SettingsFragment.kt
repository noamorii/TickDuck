package cz.cvut.fel.pda.tickduck.fragments

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cz.cvut.fel.pda.tickduck.activities.LoginActivity
import cz.cvut.fel.pda.tickduck.databinding.FragmentSettingsBinding
import cz.cvut.fel.pda.tickduck.utils.SharedPreferencesKeys.CURRENT_USER_ID
import cz.cvut.fel.pda.tickduck.utils.SharedPreferencesKeys.CURRENT_USER_PREFERENCES

class SettingsFragment : BaseFragment() {

    private lateinit var binding: FragmentSettingsBinding

    override fun onClickNew() {
       //do nothing
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setSignOutListener()
    }

    private fun setSignOutListener() {
        binding.signOut.setOnClickListener {
            requireActivity().getSharedPreferences(CURRENT_USER_PREFERENCES, MODE_PRIVATE).apply {
                val spEditor = edit()
                spEditor.remove(CURRENT_USER_ID)
                spEditor.apply()
            }

            startActivity(
                Intent(activity, LoginActivity::class.java)
            )
        }
    }
}