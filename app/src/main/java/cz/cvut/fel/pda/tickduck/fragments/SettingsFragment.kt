package cz.cvut.fel.pda.tickduck.fragments

import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.Intent.ACTION_PICK
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import cz.cvut.fel.pda.tickduck.R
import cz.cvut.fel.pda.tickduck.activities.LoginActivity
import cz.cvut.fel.pda.tickduck.activities.NewTodoActivity
import cz.cvut.fel.pda.tickduck.databinding.FragmentSettingsBinding
import cz.cvut.fel.pda.tickduck.db.viewmodels.TodoViewModel
import cz.cvut.fel.pda.tickduck.db.viewmodels.UserViewModel
import cz.cvut.fel.pda.tickduck.model.intentDTO.NewTodoDTO
import cz.cvut.fel.pda.tickduck.utils.BitmapConverter
import cz.cvut.fel.pda.tickduck.utils.CalendarUtils
import cz.cvut.fel.pda.tickduck.utils.SerializableExtras
import cz.cvut.fel.pda.tickduck.utils.SharedPreferencesKeys.CURRENT_USER_ID
import cz.cvut.fel.pda.tickduck.utils.SharedPreferencesKeys.CURRENT_USER_PREFERENCES
import cz.cvut.fel.pda.tickduck.utils.Vibrations
import kotlinx.coroutines.*
import java.time.LocalDate

class SettingsFragment : BaseFragment() {

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var editLauncher: ActivityResultLauncher<Intent>
    private lateinit var registerActivityTakeAnImage: ActivityResultLauncher<Intent>
    private lateinit var registerPermissionsActivityTakeAnImage: ActivityResultLauncher<String>
    private lateinit var registerActivityBrowseAnImage: ActivityResultLauncher<Intent>
    private lateinit var registerPermissionsActivityBrowseAnImage: ActivityResultLauncher<String>

    private val userViewModel: UserViewModel by activityViewModels {
        UserViewModel.UserViewModelFactory(requireContext())
    }

    private val todoViewModel: TodoViewModel by activityViewModels {
        TodoViewModel.TodoViewModelFactory(requireContext())
    }

    override fun onClickNew() {
        editLauncher.launch(Intent(activity, NewTodoActivity::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CalendarUtils.selectedDay = LocalDate.now()
        onEditResult()
    }

    private fun onEditResult() {
        editLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                todoViewModel.insertTodo(it.data?.getSerializableExtra(SerializableExtras.NEW_TODO_DTO) as NewTodoDTO)
            }
        }
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

        registerResultLaunchers()

        setSignOutListener()
        loadUserProfileImage()
        uploadProfileImageListener()
    }

    private fun registerResultLaunchers() {
        registerActivityTakeAnImage = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                CoroutineScope(Dispatchers.IO).launch {
                    val image = it.data?.extras?.get("data") as Bitmap
                    saveAndLoadProfilePicture(image)
                    loadUserProfileImage()
                }
            }
        }

        registerPermissionsActivityTakeAnImage = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                takeAnImage()
            } else {
                Toast.makeText(requireActivity(), "Camera permissions are required.", Toast.LENGTH_SHORT).show()
                Vibrations.vibrate(this@SettingsFragment.requireContext())
            }
        }

        registerActivityBrowseAnImage = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val dataUri = it.data?.data!!

                CoroutineScope(Dispatchers.IO).launch {
                    val image = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        ImageDecoder.decodeBitmap(ImageDecoder.createSource(requireContext().contentResolver, dataUri))
                    } else {
                        @Suppress("DEPRECATION")
                        MediaStore.Images.Media.getBitmap(requireContext().contentResolver, dataUri)
                    }
                    saveAndLoadProfilePicture(image)
                }
            }
        }

        registerPermissionsActivityBrowseAnImage = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                browseAnImage()
            } else {
                Toast.makeText(requireActivity(), "Storage reading permissions are required.", Toast.LENGTH_SHORT).show()
                Vibrations.vibrate(this@SettingsFragment.requireContext())
            }
        }
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

    private fun uploadProfileImageListener() {
        binding.button2.setOnClickListener {
            val dialog = Dialog(requireContext())
            showDialog(dialog)

            takeAnImageListener(dialog)
            browseAnImageListener(dialog)
        }
    }

    private fun showDialog(dialog: Dialog) {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.photo_popup)
        dialog.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            attributes?.windowAnimations = R.style.DialogAnimation
        }
        dialog.show()
    }

    private fun takeAnImageListener(dialog: Dialog) {
        dialog.findViewById<Button>(R.id.take_image).setOnClickListener {
            dialog.hide()
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                takeAnImage()
            } else {
                registerPermissionsActivityTakeAnImage.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun takeAnImage() {
        try {
            registerActivityTakeAnImage.launch(
                Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            )
        } catch (e: ActivityNotFoundException) {
            Log.d("Log", "Activity not found")
        }
    }

    private fun browseAnImageListener(dialog: Dialog) {
        dialog.findViewById<Button>(R.id.browse_image).setOnClickListener {
            dialog.hide()
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            ) {
                browseAnImage()
            } else {
                registerPermissionsActivityBrowseAnImage.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    private fun browseAnImage() {
        try {
            registerActivityBrowseAnImage.launch(
                Intent(ACTION_PICK, EXTERNAL_CONTENT_URI).apply {
                    type = "image/*"
                }
            )
        } catch (e: ActivityNotFoundException) {
            Log.d("Log", "Activity not found")
        }
    }

    private suspend fun saveAndLoadProfilePicture(picture: Bitmap) {
        withContext(Dispatchers.Main) {
            userViewModel.loggedUser!!.profilePicture = BitmapConverter.convert(picture)
            userViewModel.updateUser()
            loadUserProfileImage()
        }
    }

    private fun loadUserProfileImage() {
        userViewModel.loggedUser!!.profilePicture?.apply {
            binding.profilePicture.setImageBitmap(BitmapConverter.convert(this))
        }
    }
}