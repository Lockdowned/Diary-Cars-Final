package com.beta.finalprojectacad.ui.fragments

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isGone
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.beta.finalprojectacad.R
import com.beta.finalprojectacad.data.localDB.entity.CarRoom
import com.beta.finalprojectacad.databinding.BottomSheetDialogConfCarBinding
import com.beta.finalprojectacad.other.utilities.FragmentsHelper
import com.beta.finalprojectacad.viewModel.SharedViewModel
import com.beta.finalprojectacad.viewModel.BottomSheetViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

        adaptationForLandscapeOrientation(view)

        lifecycleScope.launch(Dispatchers.Main) {
            val navigation = requireActivity().supportFragmentManager
                .findFragmentById(R.id.mainNavFragment)?.findNavController()
            var chosenCar = sharedViewModel.getChosenCar()
            if (chosenCar == null) {
                val sharedPrefCarId = FragmentsHelper.getChosenCarIdAnyway(requireContext())
                if (sharedPrefCarId != -1) {
                    chosenCar = sharedViewModel.listAllCars.find { sharedPrefCarId == it.carId }
                }
            }

            binding?.apply {
                if (chosenCar != null) {
                    val infoTextCar = chosenCar.brandName + " " + chosenCar.modelName
                    if (chosenCar.year != -1) {
                        infoTextCar.plus(" ${chosenCar.year}")
                    }
                    textViewCarBottomDialog.text = infoTextCar

                    showCarImg(chosenCar)


                    buttonConfirmCarBottomDialog.setOnClickListener {
                        navigation
                            ?.navigate(R.id.action_bottomSheetDialogConfCar_to_trackTripFragment)
                    }
                } else {
                    buttonConfirmCarBottomDialog.isGone = true
                }

                buttonChangeCarBottomDialog.setOnClickListener {
                    sharedViewModel.confirmChosenCarFlag = false
                    navigation?.navigate(R.id.listCarsFragment)
                }
            }
        }

    }

    private fun adaptationForLandscapeOrientation(view: View) {
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            view.viewTreeObserver.removeOnGlobalLayoutListener { activity }
            val dialog = dialog
            val bottomSheet =
                dialog?.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            val behavior = BottomSheetBehavior.from(bottomSheet!!)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.peekHeight = 0
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