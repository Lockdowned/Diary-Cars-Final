package com.beta.finalprojectacad.ui.fragments.registrationUser

import androidx.lifecycle.ViewModel
import com.beta.finalprojectacad.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserRegistrationViewModel
@Inject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {

    fun setAuthorizedUser() {
        mainRepository.firebaseAuthorizedUser()
    }
}