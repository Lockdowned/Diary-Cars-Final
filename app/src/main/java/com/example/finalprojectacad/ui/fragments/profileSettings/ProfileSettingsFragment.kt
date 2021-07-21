package com.example.finalprojectacad.ui.fragments.profileSettings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.example.finalprojectacad.R
import com.example.finalprojectacad.databinding.FragmentProfileSetingsBinding
import com.example.finalprojectacad.viewModel.CarViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileSettingsFragment : Fragment(R.layout.fragment_profile_setings) {

    private var binding: FragmentProfileSetingsBinding? = null
    private val viewModel: UserSettingViewModel by activityViewModels()

    @Inject
    lateinit var auth: FirebaseAuth

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
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}