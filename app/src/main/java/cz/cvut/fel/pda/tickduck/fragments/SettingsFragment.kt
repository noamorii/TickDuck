package cz.cvut.fel.pda.tickduck.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import cz.cvut.fel.pda.tickduck.R
import cz.cvut.fel.pda.tickduck.activities.NewTodoActivity
import cz.cvut.fel.pda.tickduck.databinding.FragmentSearchBinding
import cz.cvut.fel.pda.tickduck.databinding.FragmentSettingsBinding
import cz.cvut.fel.pda.tickduck.db.viewmodels.TodoViewModel
import cz.cvut.fel.pda.tickduck.model.intentDTO.NewTodoDTO
import cz.cvut.fel.pda.tickduck.utils.SerializableExtras

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

}