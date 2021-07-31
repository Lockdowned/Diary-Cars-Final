package com.beta.finalprojectacad.ui.fragments

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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.beta.finalprojectacad.R
import com.beta.finalprojectacad.databinding.FragmentRegistrationBinding
import com.beta.finalprojectacad.other.utilities.FragmentsHelper
import com.beta.finalprojectacad.other.utilities.RemoteSynchronizeUtils
import com.beta.finalprojectacad.viewModel.UserRegistrationViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.AndroidEntryPoint
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
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    auth.createUserWithEmailAndPassword(email, password).await()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            resources.getString(R.string.you_have_successfully_registered),
                            Toast.LENGTH_SHORT
                        ).show()
                        startAndTuningSynchronization()
                        navigation?.popBackStack()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Log.e(TAG, "registerNewUser: ${e.message}")
                        Toast.makeText(
                            context,
                            resources.getString(R.string.missing_internet_connection),
                            Toast.LENGTH_SHORT
                        )
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
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    auth.signInWithEmailAndPassword(email, password).await()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            resources.getString(R.string.you_have_been_successfully_authorized),
                            Toast.LENGTH_SHORT
                        ).show()
                        startAndTuningSynchronization()
                        navigation?.popBackStack()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            resources.getString(R.string.no_such_user_or_missing_internet_connection),
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
            Toast.makeText(
                context,
                resources.getString(R.string.please_fill_email_field),
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        if (passwordText.isEmpty()) {
            Toast.makeText(
                context,
                resources.getString(R.string.pleas_fill_password_field),
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        if (passwordText.length < 6) {
            Toast.makeText(
                context,
                resources.getString(R.string.password_must_contains_at_least_6_symbols),
                Toast.LENGTH_SHORT
            )
                .show()
            return false
        }
        if (emailText.length > 5) {
            val isCorrectEmail = FragmentsHelper.checkCorrectEmail(emailText)
            if (!isCorrectEmail) {
                Toast.makeText(
                    context,
                    resources.getString(R.string.pleas_write_correct_email),
                    Toast.LENGTH_SHORT
                ).show()
            }
            return isCorrectEmail
        } else {
            Toast.makeText(
                context,
                resources.getString(R.string.pleas_write_correct_email),
                Toast.LENGTH_SHORT
            ).show()
        }
        return false
    }

    private fun startAndTuningSynchronization() {
        if (RemoteSynchronizeUtils.checkLoginUser(auth)) {
            viewModel.setAuthorizedUser()
            FragmentsHelper.starWorkManagerSynchronization(requireActivity())
        }
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
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                auth.signInWithCredential(credentials).await()
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        resources.getString(R.string.you_have_been_successfully_authorized),
                        Toast.LENGTH_SHORT
                    ).show()
                    startAndTuningSynchronization()
                    navigation?.popBackStack()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.d(TAG, "googleAuthForFirebase: ${e.message}")
                    Toast.makeText(
                        requireContext(),
                        resources.getString(R.string.missing_internet_connection),
                        Toast.LENGTH_SHORT
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
            }
        }
}