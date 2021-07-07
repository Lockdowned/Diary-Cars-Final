package com.example.finalprojectacad.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.finalprojectacad.R
import com.example.finalprojectacad.data.remoteDB.FirebaseRequests
import com.example.finalprojectacad.databinding.FragmentRegistrationBinding
import com.example.finalprojectacad.other.utilities.RemoteSynchronizeUtils
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val TAG = "RegistrationFragment"

@AndroidEntryPoint
class RegistrationFragment : Fragment() {

    private lateinit var binding: FragmentRegistrationBinding

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var firebaseRequests: FirebaseRequests

    private lateinit var navigation: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistrationBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        navigation = Navigation.findNavController(view)
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            imageButtonAuthorization.setOnClickListener {
                loginUserWithEmailAndPassword()
            }

            buttonRegistrationNewUser.setOnClickListener {
                registerNewUserWithEmailAndPassword()
            }
        }
    }

    private fun registerNewUserWithEmailAndPassword() {
        val email = binding.editTextRegistrationLogin.text.toString()
        val password = binding.editTextRegistrationPassword.text.toString()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                auth.createUserWithEmailAndPassword(email, password).await()

                moveToBackStack()
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e(TAG, "registerNewUser: ${e.message}")
                }
            }
        }

    }

    private fun loginUserWithEmailAndPassword() {
        val email = binding.editTextAuthotrizationLogin.text.toString()
        val password = binding.editTextAuthorizationPassword.text.toString()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                auth.signInWithEmailAndPassword(email, password).await()

                moveToBackStack()
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e(TAG, "registerNewUser: ${e.message}")
                }
            }
        }
    }

    private suspend fun moveToBackStack() {
        if (RemoteSynchronizeUtils.checkLoginUser(auth)) {
            firebaseRequests.setUserData()
//                    navigation.navigate(R.id.listCarsFragment) // QUESTION!!! WHY if i do this i will crash when i navigate again to setting fragment
            navigation.popBackStack()
        } else {
            // show tmth
        }
    }


}