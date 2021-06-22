package com.example.finalprojectacad.ui.fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Adapter
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.finalprojectacad.R
import com.example.finalprojectacad.databinding.FragmentAddCarBinding
import com.example.finalprojectacad.db.entity.BrandRoom
import com.example.finalprojectacad.db.entity.CarRoom
import com.example.finalprojectacad.db.entity.ModelRoom
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
    ): View {
        binding = FragmentAddCarBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val brandListName = mutableListOf<String>()
        var brandList: List<BrandRoom>? = null

        binding.apply {

            viewModel.allBrands.observe(
                viewLifecycleOwner, Observer { brandsListRoom->
                    brandList = brandsListRoom
                    brandsListRoom.forEach {
                        brandListName.add(it.brandName)
                    }
                    autoCompleteTextBrandNewCar.setAdapter(autoCompleteSetAdapter(brandListName))
                })

            autoCompleteTextBrandNewCar.threshold = 2

            autoCompleteTextBrandNewCar.setOnItemClickListener { _, _, _, _ ->
               activity?.hideKeyboard(view)
            }
            
            autoCompleteTextModelNewCar.setOnItemClickListener { _, _, _, _ ->
                activity?.hideKeyboard(view)
            }

            autoCompleteTextTransmissionNewCar.setOnItemClickListener { _, _, _, _ ->
                activity?.hideKeyboard(view)
            }

            val correctModels = mutableListOf<String>()
            var listModelsRoom = listOf<ModelRoom>()
            viewModel.allModels.observe(
                viewLifecycleOwner, Observer { modelsRoom ->
                    correctModels.clear()
                    listModelsRoom = modelsRoom
                    modelsRoom.forEach {
                        correctModels.add(it.modelName)
                    }
                })

            autoCompleteTextModelNewCar.setOnClickListener {
                if (textInputLayoutBrandNewCar.editText!!.text.isEmpty()) {
                    autoCompleteTextModelNewCar.setAdapter(autoCompleteSetAdapter(correctModels))
                } else {
                    val brandWriteText = textInputLayoutBrandNewCar.editText!!.text.toString()
                    val findIdBrand = brandList?.find { it.brandName == brandWriteText }
                    if (findIdBrand == null) {
                        autoCompleteTextModelNewCar.setAdapter(autoCompleteSetAdapter(correctModels))
                    } else {
                        val findCorrectModel = listModelsRoom.filter {
                            it.brandId == findIdBrand.brandId
                        }
                        if (findCorrectModel.isEmpty()) {
                            autoCompleteTextModelNewCar
                                .setAdapter(autoCompleteSetAdapter(correctModels))

                        } else {
                            val findCorrectModelName = mutableListOf<String>()
                            findCorrectModel.forEach {
                                findCorrectModelName.add(it.modelName)
                            }
                            autoCompleteTextModelNewCar
                                .setAdapter(autoCompleteSetAdapter(findCorrectModelName))
                        }
                    }
                }
            }

            val transmissionList = mutableListOf<String>()
            viewModel.allTransmissions.observe(
                viewLifecycleOwner, Observer {
                    if (transmissionList.isEmpty()) {
                        it.forEach {
                            transmissionList.add(it.transmissionName)
                        }
                    }
            })

            autoCompleteTextTransmissionNewCar.setAdapter(autoCompleteSetAdapter(transmissionList))


            constrainAddFragment.setOnClickListener {
                activity?.hideKeyboard(view)
                Log.d(TAG, "onViewCreated: tap outside fields")
            }

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

    private fun autoCompleteSetAdapter(list: List<String>): ArrayAdapter<String>{
        return ArrayAdapter(
            requireContext(), android.R.layout.simple_dropdown_item_1line, list
        )
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
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