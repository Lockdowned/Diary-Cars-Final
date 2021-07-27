package com.example.finalprojectacad.ui.fragments.profileSettings

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.finalprojectacad.R
import com.example.finalprojectacad.databinding.FragmentProfileSetingsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


private const val TAG = "ProfileSettingsFragment"

@AndroidEntryPoint
class ProfileSettingsFragment : Fragment() {

    private var binding: FragmentProfileSetingsBinding? = null
    private val viewModel: UserSettingViewModel by activityViewModels()

    @Inject
    lateinit var auth: FirebaseAuth

    private var imgUriAvatar: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileSetingsBinding.inflate(layoutInflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = Navigation.findNavController(view)
        binding?.apply {
            buttonLogoutAccount.setOnClickListener {
                auth.signOut()
                viewModel.setAuthorizedUser()

                navController.popBackStack()
            }

            imageViewUserAvatar.setOnClickListener {
                Intent(Intent.ACTION_OPEN_DOCUMENT).also {
                    it.type = "image/*"
                    regImageIntent.launch(it)
                }
            }

            val savedUserAvatar = auth.currentUser?.photoUrl
            savedUserAvatar?.let { imgUri ->
                Log.d(TAG, "onViewCreated: imgUri: ${imgUri.path}")
                Log.d(TAG, "onViewCreated: imgUri: ${imgUri}")
                val a = Glide.with(view).load(imgUri).error(R.drawable.default_user_img).into(imageViewUserAvatar)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private val regImageIntent = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            data?.data?.let { imgUri ->
                binding?.apply {
                    Glide.with(requireContext()).load(imgUri)
                        .into(imageViewUserAvatar)
                }
                imgUriAvatar = imgUri

                lifecycleScope.launch(Dispatchers.IO) {
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setPhotoUri(imgUri)
                        .build()
                    try {
                        auth.currentUser?.updateProfile(profileUpdates)
                        Log.d(TAG, "successfully update avatar ")
                    } catch (e: Exception) {
                        Log.e(TAG, "upload user avatar exception: ${e.message}")
                    }
                }
            }
        }
    }
}