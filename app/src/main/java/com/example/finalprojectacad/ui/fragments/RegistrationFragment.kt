package com.example.finalprojectacad.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
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
import java.util.regex.Pattern
import javax.inject.Inject

private const val TAG = "RegistrationFragment"

@AndroidEntryPoint
class RegistrationFragment : Fragment() {

    private var binding: FragmentRegistrationBinding? = null

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
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        navigation = Navigation.findNavController(view)
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {

            imageButtonAuthorization.setOnClickListener {
                loginUserWithEmailAndPassword()
            }

            buttonRegistrationNewUser.setOnClickListener {
                registerNewUserWithEmailAndPassword()
            }
        }
    }

    private fun registerNewUserWithEmailAndPassword() {
        val email = binding?.editTextRegistrationLogin?.text.toString()
        val password = binding?.editTextRegistrationPassword?.text.toString()
        if (inspectInputFields(email, password)) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.createUserWithEmailAndPassword(email, password).await()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "You have successfully registered", Toast.LENGTH_SHORT).show()
                        moveToBackStack()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Log.e(TAG, "registerNewUser: ${e.message}")
                        Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }

    private fun loginUserWithEmailAndPassword() {
        val email = binding?.editTextAuthotrizationLogin?.text.toString()
        val password = binding?.editTextAuthorizationPassword?.text.toString()
        if (inspectInputFields(email, password)) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.signInWithEmailAndPassword(email, password).await()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "You have been successfully authorized", Toast.LENGTH_SHORT).show()
                        moveToBackStack()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                        Log.e(TAG, "registerNewUser: ${e.message}")
                    }
                }
            }
        }
    }

    private fun inspectInputFields(emailText: String, passwordText: String): Boolean {
        if (emailText.isEmpty()){
            Toast.makeText(context, "Pleas fill email field", Toast.LENGTH_SHORT).show()
            return false
        }
        if (passwordText.isEmpty()){
            Toast.makeText(context, "Pleas fill password field", Toast.LENGTH_SHORT).show()
            return false
        }
        if (passwordText.length < 6) {
            Toast.makeText(context, "Password must contains at least 6 symbols", Toast.LENGTH_SHORT).show()
            return false
        }
        if (emailText.length > 5) {
            val emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                    "[a-zA-Z0-9_+&*-]+)*@" +
                    "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                    "A-Z]{2,7}$"
            val pat: Pattern = Pattern.compile(emailRegex)
            val isValidEmail = pat.matcher(emailText).matches()
            if (!isValidEmail) {
                Toast.makeText(context, "Pleas write correct email", Toast.LENGTH_SHORT).show()
            }
            return isValidEmail
        } else {
            Toast.makeText(context, "Pleas write correct email", Toast.LENGTH_SHORT).show()
        }

        return false
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