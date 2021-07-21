package com.example.finalprojectacad.ui.fragments.registrationUser

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.finalprojectacad.repositories.MainRepository
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