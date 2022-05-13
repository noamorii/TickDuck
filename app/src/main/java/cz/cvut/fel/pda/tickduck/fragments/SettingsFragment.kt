package cz.cvut.fel.pda.tickduck.fragments

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import cz.cvut.fel.pda.tickduck.activities.LoginActivity
import cz.cvut.fel.pda.tickduck.databinding.FragmentSettingsBinding
import cz.cvut.fel.pda.tickduck.db.viewmodels.UserViewModel
import cz.cvut.fel.pda.tickduck.utils.BitmapConverter
import cz.cvut.fel.pda.tickduck.utils.SharedPreferencesKeys.CURRENT_USER_ID
import cz.cvut.fel.pda.tickduck.utils.SharedPreferencesKeys.CURRENT_USER_PREFERENCES
import cz.cvut.fel.pda.tickduck.utils.Vibrations

class SettingsFragment : BaseFragment() {

    private lateinit var binding: FragmentSettingsBinding

    private val userViewModel: UserViewModel by activityViewModels {
        UserViewModel.UserViewModelFactory(requireContext())
    }

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
        takeAPictureListener()
        loadUserProfileImage()
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

    private fun takeAPictureListener() {
        val registerActivity = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val image = it.data?.extras?.get("data") as Bitmap
                saveProfilePicture(image)
                loadUserProfileImage()
            }
        }

        val takeAPicture = {
            try {
                registerActivity.launch(
                    Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                )
            } catch (e: ActivityNotFoundException) {
                Log.d("Log", "Activity not found")
            }
        }

        val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                takeAPicture()
            } else {
                Toast.makeText(requireActivity(), "Camera permissions are required.", Toast.LENGTH_SHORT).show()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    Vibrations.vibrate(this@SettingsFragment.requireContext())
                }
            }
        }

        binding.button2.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                takeAPicture()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun saveProfilePicture(picture: Bitmap) {
        userViewModel.loggedUser!!.profilePicture = BitmapConverter.convert(picture)
        userViewModel.updateUser()
    }

    private fun loadUserProfileImage() {
        userViewModel.loggedUser!!.profilePicture?.apply {
            binding.profilePicture.setImageBitmap(BitmapConverter.convert(this))
        }
    }
}