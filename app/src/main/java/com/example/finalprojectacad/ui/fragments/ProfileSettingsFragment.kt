package com.example.finalprojectacad.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.finalprojectacad.R
import com.example.finalprojectacad.data.remoteDB.FirebaseRequests
import com.example.finalprojectacad.databinding.FragmentProfileSetingsBinding
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileSettingsFragment : Fragment(R.layout.fragment_profile_setings) {

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var firebaseRequests: FirebaseRequests

    private lateinit var binding: FragmentProfileSetingsBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileSetingsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = Navigation.findNavController(view)
        binding.apply {
            buttonLogoutAccount.setOnClickListener {
                auth.signOut()
                firebaseRequests.setUserData()

                navController.popBackStack()
            }
        }
    }
}