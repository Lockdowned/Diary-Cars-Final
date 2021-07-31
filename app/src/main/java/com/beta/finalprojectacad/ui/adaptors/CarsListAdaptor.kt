package com.beta.finalprojectacad.ui.adaptors

import android.content.Context
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.beta.finalprojectacad.R
import com.beta.finalprojectacad.data.localDB.entity.CarRoom
import com.beta.finalprojectacad.databinding.ItemRvListCarsBinding
import com.beta.finalprojectacad.other.utilities.FragmentsHelper
import com.beta.finalprojectacad.viewModel.ListCarsViewModel
import com.beta.finalprojectacad.viewModel.SharedViewModel
import com.bumptech.glide.Glide
import com.github.satoshun.coroutine.autodispose.view.autoDisposeScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "CarsListAdaptor"

class CarsListAdaptor(
    private val viewModel: ListCarsViewModel,
    private val sharedViewModel: SharedViewModel
) : ListAdapter<CarRoom, CarsListAdaptor.CarsListHolder>(CarsComparator()) {

    private var context: Context? = null
    private var chosenCar: CarRoom? = null
    private var previousCarView: View? = null

    inner class CarsListHolder(private val carItemBinding: ItemRvListCarsBinding) :
        RecyclerView.ViewHolder(carItemBinding.root) {
        suspend fun bind(carItem: CarRoom) {
            carItemBinding.apply {
                val brandAndModelText = "${carItem.brandName} ${carItem.modelName}"
                var specificationText = ""
                specificationText = fillSpecificationText(carItem, specificationText)
                val findImgRoom = viewModel.listAllImages.find { imageCarRoom ->
                    imageCarRoom.id == carItem.carId
                }
                Log.d(TAG, "bind: findImgRoom: ${findImgRoom.toString()} ")
                withContext(Dispatchers.Main) {
                    textViewBrand.text = brandAndModelText
                    textViewSpecification.text = specificationText
                    if (findImgRoom == null) {
                        val defaultCarDrawable =
                            AppCompatResources.getDrawable(context!!, R.drawable.default_car)
                        imageViewCar.setImageDrawable(defaultCarDrawable)
                    } else {
                        Glide.with(carItemBinding.root.context).load(findImgRoom.imgCar)
                            .error(R.drawable.default_car)
                            .into(imageViewCar)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarsListHolder {
        context = parent.context
        val layoutInflater = LayoutInflater.from(context)
        val carItemBinding = ItemRvListCarsBinding.inflate(layoutInflater, parent, false)
        return CarsListHolder(carItemBinding)
    }

    override fun onBindViewHolder(holder: CarsListHolder, position: Int) {
        holder.itemView.autoDisposeScope.launch(Dispatchers.Default) {
            holder.apply {
                val car = getItem(position)
                bind(car)
                choosingCar(car)
            }
        }
    }

    private fun fillSpecificationText(
        carItem: CarRoom,
        specificationText: String
    ): String {
        var correctText = specificationText
        if (carItem.transmissionName.isNotEmpty()) {
            correctText +=
                "${context!!.resources.getString(R.string.transmission)}: " +
                        "${carItem.transmissionName}\n"
        }
        if (carItem.year != -1) {
            correctText +=
                "${context!!.resources.getString(R.string.year)}: ${carItem.year}\n"
        }

        if (carItem.engine.isNotEmpty()) {
            correctText +=
                "${context!!.resources.getString(R.string.engine)}: ${carItem.engine}\n"
        }
        if (carItem.mileage != -1) {
            correctText +=
                "${context!!.resources.getString(R.string.mileage)}: ${carItem.mileage}"
        }
        return correctText
    }

    private suspend fun CarsListHolder.choosingCar(
        car: CarRoom?
    ) {
        val typedValuePrimaryColour = TypedValue()
        val typedValueColorSecondary = TypedValue()
        val theme = context?.theme
        theme?.resolveAttribute(R.attr.colorOnPrimary, typedValuePrimaryColour, true)
        theme?.resolveAttribute(R.attr.colorSecondary, typedValueColorSecondary, true)
        val primaryColour = typedValuePrimaryColour.data
        val secondaryColour = typedValueColorSecondary.data
        withContext(Dispatchers.Main) {
            changeColorPreviousSelectedCar(car, secondaryColour, primaryColour)
        }
    }

    private fun CarsListHolder.changeColorPreviousSelectedCar(
        car: CarRoom?,
        secondaryColour: Int,
        primaryColour: Int
    ) {
        itemView.setOnClickListener { view ->
            if (sharedViewModel.confirmChosenCarFlag) {
                viewModel.confirmCarLiveData.value = car
            }
            if (chosenCar == null) {
                chosenCar = car
                view.setBackgroundColor(secondaryColour)
                previousCarView = view
                carChoiceChanger(car)
            } else if (chosenCar == car) {
                view.setBackgroundColor(primaryColour)
                chosenCar = null
                previousCarView = null
                carChoiceChanger(null)
            } else {
                previousCarView?.setBackgroundColor(primaryColour)
                view.setBackgroundColor(secondaryColour)
                chosenCar = car
                previousCarView = view
                carChoiceChanger(car)
            }

        }
        itemView.setOnLongClickListener { view ->
            val navigation = Navigation.findNavController(view)
            sharedViewModel.setCarToEdit(car)
            navigation.navigate(R.id.action_listCarsFragment_to_addCarFragment)
            true
        }
    }

    private fun carChoiceChanger(car: CarRoom?) {
        sharedViewModel.setChosenCar(car)
        context?.let { context ->
            FragmentsHelper.setChosenCarIdInSharedPref(car, context)
        }

    }


    class CarsComparator : DiffUtil.ItemCallback<CarRoom>() {
        override fun areItemsTheSame(oldItem: CarRoom, newItem: CarRoom): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: CarRoom, newItem: CarRoom): Boolean {
            return oldItem.brandName == newItem.brandName &&
                    oldItem.modelName == newItem.modelName &&
                    oldItem.transmissionName == newItem.transmissionName &&
                    oldItem.year == newItem.year &&
                    oldItem.engine == newItem.engine &&
                    oldItem.mileage == newItem.mileage
        }
    }
}