package com.example.finalprojectacad.ui.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.example.finalprojectacad.R
import com.example.finalprojectacad.data.localDB.entity.BrandRoom
import com.example.finalprojectacad.data.localDB.entity.CarRoom
import com.example.finalprojectacad.data.localDB.entity.ImageCarRoom
import com.example.finalprojectacad.data.localDB.entity.ModelRoom
import com.example.finalprojectacad.databinding.FragmentAddCarBinding
import com.example.finalprojectacad.other.utilities.SaveImgToScopedStorage
import com.example.finalprojectacad.ui.activity.MainActivity
import com.example.finalprojectacad.viewModel.CarViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

private const val TAG = "AddCarFragment"

@AndroidEntryPoint
class AddCarFragment : Fragment() {

    private var binding: FragmentAddCarBinding? = null
    private val viewModel: CarViewModel by activityViewModels()

    var choseImgUri: Uri? = null
    var carToEdit: CarRoom? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddCarBinding.inflate(layoutInflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setDataIfEditCar()

        viewModel.getAllCars.observe(
            viewLifecycleOwner, Observer {
                viewModel.listAllCars = it
            }
        )

        val brandListName = mutableListOf<String>()
        var brandList = listOf<BrandRoom>()

        binding?.apply {

            imageButtonAddBrandInDB.setOnClickListener {
                CoroutineScope(Dispatchers.Main).launch {
                    val brandText = textInputLayoutBrandNewCar.editText?.text.toString()
                    if (textInputLayoutBrandNewCar.editText?.text.isNullOrEmpty()) {
                        Toast.makeText(context, "Please fill brand text field", Toast.LENGTH_SHORT)
                            .show()
                    } else if (brandAlreadyExistInDb(brandText, brandListName)) {
                        Toast.makeText(context, "This brand already exist", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        addBrandInDbDialog(brandText)
                    }
                }
            }

            imageButtonAddModelInDB.setOnClickListener {
                CoroutineScope(Dispatchers.Main).launch {
                    val brandText = textInputLayoutBrandNewCar.editText?.text.toString()
                    val modelText = textInputLayoutModelNewCar.editText?.text.toString()
                    if (textInputLayoutModelNewCar.editText?.text.isNullOrEmpty()) {
                        Toast.makeText(context, "Please fill model text field", Toast.LENGTH_SHORT)
                            .show()
                    } else if (textInputLayoutBrandNewCar.editText?.text.isNullOrEmpty()) {
                        Toast.makeText(context, "Please fill brand text field", Toast.LENGTH_SHORT)
                            .show()
                    } else if (!brandAlreadyExistInDb(brandText, brandListName)) {
                        Toast.makeText(context, "At the begin add brand name", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        addModelInDbDialog(modelText, brandText, brandList)
                    }
                }
            }


            ivSelectImageCar.setOnClickListener {
                Intent(Intent.ACTION_OPEN_DOCUMENT).also {
                    it.type = "image/*"
                    regImageIntent.launch(it)
                }
            }


            viewModel.allBrands.observe(
                viewLifecycleOwner, Observer { brandsListRoom ->
                    brandList = brandsListRoom
                    brandListName.clear()
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

            val allModelsName = mutableListOf<String>()
            var listModelsRoom = listOf<ModelRoom>()
            viewModel.allModels.observe(
                viewLifecycleOwner, Observer { modelsRoom ->
                    val modelsName = mutableListOf<String>()
                    listModelsRoom = modelsRoom
                    modelsRoom.forEach {
                        modelsName.add(it.modelName)
                    }
                    viewModel.setAllModels(modelsName)
                })

            autoCompleteTextModelNewCar.setAdapter(autoCompleteSetAdapter(allModelsName))

            autoCompleteTextModelNewCar.setOnClickListener {
                viewModel.fillCorrectModelsByCar(
                    allModelsName,
                    listModelsRoom,
                    brandList,
                    textInputLayoutBrandNewCar.editText!!.text.toString()
                )
                autoCompleteTextModelNewCar.setAdapter(autoCompleteSetAdapter(allModelsName))
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
                    && !textInputLayoutModelNewCar.editText!!.text.equals("")
                ) {
                    collectAndInsertCar()
                    val navigation = Navigation.findNavController(view)
                    navigation.popBackStack()
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

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun brandAlreadyExistInDb(writtenBrand: String, brandListName: List<String>): Boolean {
        for (brandName in brandListName) {
            if (writtenBrand == brandName) {
                return true
            }
        }
        return false
    }

    private fun addBrandInDbDialog(brandName: String) {
        val dialog = AlertDialog.Builder(context)
        dialog.run {
            setMessage("Insert this brand: $brandName in database?")
            setPositiveButton("Yes") { _, _ ->
                Log.d(TAG, "addBrandInDBDialog: ")
                val newBrandRoom = BrandRoom(brandName)
                viewModel.insertNewBrand(newBrandRoom)
            }
            setNegativeButton("No") { _, _ -> }
        }
        val alertDialog = dialog.create()
        alertDialog.show()
    }

    private fun addModelInDbDialog(
        modelName: String,
        brandName: String,
        listBrandRoom: List<BrandRoom>
    ) {
        val dialog = AlertDialog.Builder(context)
        dialog.run {
            setMessage("Insert this brand: $modelName in database?")
            setPositiveButton("Yes") { _, _ ->
                Log.d(TAG, "addBrandInDBDialog: ")
                var brandId: Int? = null
                for (brand in listBrandRoom) {
                    if (brandName == brand.brandName) {
                        brandId = brand.brandId
                    }
                }
                brandId?.let { idBrand ->
                    val newModelRoom = ModelRoom(modelName, idBrand)
                    viewModel.insertNewModel(newModelRoom)
                    Log.d(TAG, "addModelInDbDialog: new model insert in db model: $newModelRoom")
                }
            }
            setNegativeButton("No") { _, _ -> }
        }
        val alertDialog = dialog.create()
        alertDialog.show()
    }

    private fun autoCompleteSetAdapter(list: List<String>): ArrayAdapter<String> {
        return ArrayAdapter(
            requireContext(), android.R.layout.simple_dropdown_item_1line, list
        )
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun collectAndInsertCar() {
        copyToScopeStorageImg(viewModel.listAllCars.size)
        val car = CarRoom()
        binding?.apply {
            val brand = textInputLayoutBrandNewCar.editText?.text
            brand?.let {
                if (brand.isNotEmpty()) car.brandName = brand.toString()
            }
            val model = textInputLayoutModelNewCar.editText?.text
            model?.let {
                if (model.isNotEmpty()) car.modelName = model.toString()
            }
            val engine = textInputLayoutEngineNewCar.editText?.text
            engine?.let {
                if (engine.isNotEmpty()) car.engine = engine.toString()
            }
            val transmission = textInputLayoutTransmissionNewCar.editText?.text
            transmission?.let {
                if (transmission.isNotEmpty()) car.transmissionName = transmission.toString()
            }
            val year = textInputLayoutYearNewCar.editText?.text
            year?.let {
                if (it.isNotEmpty()) car.year = it.toString().toInt()
            }
            val mileage = textInputLayoutMileageNewCar.editText?.text
            mileage?.let {
                if (mileage.isNotEmpty()) car.mileage = mileage.toString().toInt()
            }
            choseImgUri?.let {
                car.flagPresenceImg = true
            }
            car.timestamp = System.currentTimeMillis()
            carToEdit?.let { carEdit ->
                car.carId = carEdit.carId
            }
        }
        clearAllField()
        viewModel.insertNewCar(car)
    }

    private fun clearAllField() {
        binding?.apply {
            textInputLayoutBrandNewCar.editText?.text?.clear()
            textInputLayoutModelNewCar.editText?.text?.clear()
            textInputLayoutEngineNewCar.editText?.text?.clear()
            textInputLayoutTransmissionNewCar.editText?.text?.clear()
            textInputLayoutYearNewCar.editText?.text?.clear()
            textInputLayoutMileageNewCar.editText?.text?.clear()

            ivSelectImageCar.setImageResource(R.drawable.default_car)
            choseImgUri = null
        }

    }

    private fun setDataIfEditCar() {
        viewModel.getCarToEdit()?.let { car ->
            carToEdit = car
            binding?.apply {
                if (car.brandName.isNotEmpty()) {
                    textInputLayoutBrandNewCar.editText?.setText(car.brandName)
                }
                if (car.modelName.isNotEmpty()) {
                    textInputLayoutModelNewCar.editText?.setText(car.modelName)
                }
                if (car.engine.isNotEmpty()) {
                    textInputLayoutEngineNewCar.editText?.setText(car.engine)
                }
                if (car.transmissionName.isNotEmpty()) {
                    textInputLayoutTransmissionNewCar.editText?.setText(car.transmissionName)
                }
                if (car.year != -1) {
                    textInputLayoutYearNewCar.editText?.setText(car.year.toString())
                }
                if (car.mileage != -1) {
                    textInputLayoutMileageNewCar.editText?.setText(car.mileage.toString())
                }
            }
        }

    }


    private fun copyToScopeStorageImg(carListSize: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            var flagSuccessSave = false
            choseImgUri?.let {
                val currentIdCars = carListSize + 1
                val act = activity as MainActivity
                flagSuccessSave = SaveImgToScopedStorage.save(requireContext(), currentIdCars, it)
                if (flagSuccessSave) {
                    val listScopeStorageImg = act.openSavedImg()
                    val lastSavedImg = listScopeStorageImg.last() // mb need find by name file

                    val imgRoom = ImageCarRoom(
                        lastSavedImg.toString(),
                        System.currentTimeMillis(),
                        currentIdCars
                    )
                    viewModel.insertNewImg(imgRoom)
                }
            }

        }
    }

    private val regImageIntent = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            data?.data?.let { imgUri ->
                binding?.apply {
                    Glide.with(requireContext()).load(imgUri)
                        .into(ivSelectImageCar)
                }
//                 binding?.ivSelectImageCar?.setImageURI(imgUri)
                choseImgUri = imgUri
            }
        }
    }


}