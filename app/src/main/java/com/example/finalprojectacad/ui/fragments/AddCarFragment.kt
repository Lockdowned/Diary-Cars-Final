package com.example.finalprojectacad.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.finalprojectacad.R
import com.example.finalprojectacad.databinding.FragmentAddCarBinding
import com.example.finalprojectacad.db.entity.CarRoom
import com.example.finalprojectacad.viewModel.CarViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "AddCarFragment"

@AndroidEntryPoint
class AddCarFragment : Fragment() {

    private lateinit var binding: FragmentAddCarBinding
    private val viewModel: CarViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddCarBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            buttonSaveNewCar.setOnClickListener {
                if (!textInputLayoutBrandNewCar.editText!!.text.equals("")
                    && !textInputLayoutModelNewCar.editText!!.text.equals("")){
                    collectAndInsertNewCar()
                } else {
                    Snackbar.make(
                        view,
                        "You should fill brand and model fields",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }
       
    }

    private fun collectAndInsertNewCar() {
        val newCar = CarRoom()
        binding.apply {
            val brand = textInputLayoutBrandNewCar.editText?.text
            brand?.let {
                if(brand.isNotEmpty()) newCar.brandName = brand.toString()
            }
            val model = textInputLayoutModelNewCar.editText?.text
            model?.let {
                if (model.isNotEmpty()) newCar.modelName = model.toString()
            }
            val engine = textInputLayoutEngineNewCar.editText?.text
            engine?.let {
                if (engine.isNotEmpty()) newCar.engine = engine.toString()
            }
            val transmission = textInputLayoutTransmissionNewCar.editText?.text
            transmission?.let {
                if (transmission.isNotEmpty()) newCar.transmissionName = transmission.toString()
            }
            val year = textInputLayoutYearNewCar.editText?.text
            year?.let {
                if (year.isNotEmpty()) newCar.year = year.toString().toInt()
            }
            val mileage = textInputLayoutMileageNewCar.editText?.text
            mileage?.let {
                if (mileage.isNotEmpty()) newCar.mileage = mileage.toString().toInt()
            }
        }
        viewModel.insertNewCar(newCar)
    }

}