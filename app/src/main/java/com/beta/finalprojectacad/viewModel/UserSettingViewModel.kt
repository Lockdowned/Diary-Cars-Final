package com.beta.finalprojectacad.viewModel

import androidx.lifecycle.ViewModel
import com.beta.finalprojectacad.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserSettingViewModel
@Inject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {

    fun setAuthorizedUser() {
        mainRepository.firebaseAuthorizedUser()
    }
}