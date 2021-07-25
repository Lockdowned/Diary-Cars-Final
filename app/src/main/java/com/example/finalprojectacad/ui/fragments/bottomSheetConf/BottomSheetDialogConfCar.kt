package com.example.finalprojectacad.ui.fragments.bottomSheetConf

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.finalprojectacad.R
import com.example.finalprojectacad.data.localDB.entity.CarRoom
import com.example.finalprojectacad.databinding.BottomSheetDialogConfCarBinding
import com.example.finalprojectacad.ui.SharedViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BottomSheetDialogConfCar : BottomSheetDialogFragment() {

    private var binding: BottomSheetDialogConfCarBinding? = null
    private val viewModel: BottomSheetViewModel by activityViewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetDialogConfCarBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navigation = requireActivity().supportFragmentManager
            .findFragmentById(R.id.mainNavFragment)?.findNavController()
//        val navigation = Navigation.findNavController(view) //why didn't find navigation
        val chosenCar = sharedViewModel.getChosenCar()

        binding?.apply {
            if (chosenCar != null) {
                val infoTextCar = chosenCar.brandName + " " + chosenCar.modelName
                if (chosenCar.year != -1) {
                    infoTextCar.plus(" ${chosenCar.year}")
                }
                textViewCarBottomDialog.text = infoTextCar

                showCarImg(chosenCar)


                buttonConfirmCarBottomDialog.setOnClickListener {
                    navigation?.navigate(R.id.trackTripFragment)
                }
            } else {
                buttonConfirmCarBottomDialog.isGone = true
            }

            buttonChangeCarBottomDialog.setOnClickListener {
                sharedViewModel.confirmChosenCarFlag = true
                navigation?.popBackStack()
            }
        }

    }

    private fun showCarImg(chosenCar: CarRoom) {
        viewModel.allImagesLiveData.observe(
            this@BottomSheetDialogConfCar,
            Observer { listImg ->
                val findImgRoom =
                    listImg.find { imageCarRoom ->
                        imageCarRoom.id == chosenCar.carId
                    }
                findImgRoom?.let { findImg ->
                    Glide.with(binding!!.root.context).load(findImg.imgCar)
                        .into(binding!!.imageViewCarBottomDialog)
                }
            })
    }


}