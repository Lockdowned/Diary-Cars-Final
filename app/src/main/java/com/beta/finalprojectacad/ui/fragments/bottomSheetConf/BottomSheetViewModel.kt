package com.beta.finalprojectacad.ui.fragments.bottomSheetConf

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.beta.finalprojectacad.data.localDB.entity.ImageCarRoom
import com.beta.finalprojectacad.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BottomSheetViewModel
@Inject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {

    val allImagesLiveData: LiveData<List<ImageCarRoom>> = mainRepository.getAllImages().asLiveData()

}