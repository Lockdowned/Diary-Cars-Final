package com.example.finalprojectacad.ui.fragments.registrationUser

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.finalprojectacad.R
import com.example.finalprojectacad.databinding.FragmentRegistrationBinding
import com.example.finalprojectacad.other.utilities.FragmentsHelper
import com.example.finalprojectacad.other.utilities.RemoteSynchronizeUtils
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
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

    private var binding: FragmentRegistrationBinding? = null
    private val viewModel: UserRegistrationViewModel by activityViewModels()

    @Inject
    lateinit var auth: FirebaseAuth

    private var navigation: NavController? = null

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

            imgBtnGoogleSignIn.setOnClickListener {
                tuneAndStartGoogleAuthIntent()
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
                        Toast.makeText(
                            context,
                            "You have successfully registered",
                            Toast.LENGTH_SHORT
                        ).show()
                        moveToBackStack()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Log.e(TAG, "registerNewUser: ${e.message}")
                        Toast.makeText(context, "Missing internet connection", Toast.LENGTH_SHORT)
                            .show()
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
                        Toast.makeText(
                            context,
                            "You have been successfully authorized",
                            Toast.LENGTH_SHORT
                        ).show()
                        moveToBackStack()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context, "No such user or missing internet connection",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e(TAG, "registerNewUser: ${e.message}")
                    }
                }
            }
        }
    }

    private fun inspectInputFields(emailText: String, passwordText: String): Boolean {
        if (emailText.isEmpty()) {
            Toast.makeText(context, "Pleas fill email field", Toast.LENGTH_SHORT).show()
            return false
        }
        if (passwordText.isEmpty()) {
            Toast.makeText(context, "Pleas fill password field", Toast.LENGTH_SHORT).show()
            return false
        }
        if (passwordText.length < 6) {
            Toast.makeText(context, "Password must contains at least 6 symbols", Toast.LENGTH_SHORT)
                .show()
            return false
        }
        if (emailText.length > 5) {
            val isCorrectEmail = FragmentsHelper.checkCorrectEmail(emailText)
            if (!isCorrectEmail) {
                Toast.makeText(context, "Pleas write correct email", Toast.LENGTH_SHORT).show()
            }
            return isCorrectEmail
        } else {
            Toast.makeText(context, "Pleas write correct email", Toast.LENGTH_SHORT).show()
        }
        return false
    }

    private fun moveToBackStack() { // !!! and update authorize state !!!
        if (RemoteSynchronizeUtils.checkLoginUser(auth)) {
            viewModel.setAuthorizedUser()
        }
        navigation?.popBackStack()
    }

    private fun tuneAndStartGoogleAuthIntent() {
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail() //if need .requestProfile() give more info
            .build()
        val signInClient = GoogleSignIn.getClient(requireActivity(), options)
        signInClient.signInIntent.also {
            startForRegistarionResult.launch(it)
        }
    }

    private fun googleAuthForFirebase(account: GoogleSignInAccount) {
        val credentials = GoogleAuthProvider.getCredential(account.idToken, null)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                auth.signInWithCredential(credentials).await()
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "You have been successfully authorized",
                        Toast.LENGTH_SHORT
                    ).show()
                    moveToBackStack()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.d(TAG, "googleAuthForFirebase: ${e.message}")
                    Toast.makeText(
                        requireContext(),
                        "Missing internet connection", Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private val startForRegistarionResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val account = GoogleSignIn.getSignedInAccountFromIntent(result.data).result
                account?.let { googleSignInAccount ->
                    googleAuthForFirebase(googleSignInAccount)
                }
            } // correct work?
        }
}