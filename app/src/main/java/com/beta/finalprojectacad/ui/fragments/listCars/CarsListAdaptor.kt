package com.beta.finalprojectacad.ui.fragments.listCars

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
import com.bumptech.glide.Glide
import com.beta.finalprojectacad.R
import com.beta.finalprojectacad.data.localDB.entity.CarRoom
import com.beta.finalprojectacad.databinding.ItemRvListCarsBinding
import com.beta.finalprojectacad.other.utilities.FragmentsHelper
import com.beta.finalprojectacad.ui.SharedViewModel
import kotlinx.coroutines.*

private const val TAG = "CarsListAdaptor"

class CarsListAdaptor(
    private val viewModel: ListCarsViewModel,
    private val sharedViewModel: SharedViewModel
) : ListAdapter<CarRoom, CarsListAdaptor.CarsListHolder>(CarsComparator()) {

    var context: Context? = null
    var chosenCar: CarRoom? = null
    var previousCarView: View? = null
    val localCoroutineScope = CoroutineScope(Job() + Dispatchers.Default)

    inner class CarsListHolder(private val carItemBinding: ItemRvListCarsBinding) :
        RecyclerView.ViewHolder(carItemBinding.root) {
        fun bind(carItem: CarRoom) {
            carItemBinding.apply {
                val brandAndModelText = "${carItem.brandName} ${carItem.modelName}"
                textViewBrand.text = brandAndModelText
                var specificationText = ""
                if (carItem.transmissionName.isNotEmpty()) {
                    specificationText +=
                        "${context!!.resources.getString(R.string.transmission)}: " +
                                "${carItem.transmissionName}\n"
                }
                if (carItem.year != -1) {
                    specificationText +=
                        "${context!!.resources.getString(R.string.year)}: ${carItem.year}\n"
                }

                if (carItem.engine.isNotEmpty()) {
                    specificationText +=
                        "${context!!.resources.getString(R.string.engine)}: ${carItem.engine}\n"
                }
                if (carItem.mileage != -1) {
                    specificationText +=
                        "${context!!.resources.getString(R.string.mileage)}: ${carItem.mileage}"
                }
                textViewSpecification.text = specificationText
                localCoroutineScope.launch {
                    val findImgRoom =
                        viewModel.listAllImages.find { imageCarRoom ->
                            imageCarRoom.id == carItem.carId
                        }
                    Log.d(TAG, "bind: findImgRoom: ${findImgRoom.toString()} ")
                    withContext(Dispatchers.Main) {
                        if (findImgRoom == null) {
                            val defaultCarDrawable =
                                AppCompatResources.getDrawable(context!!, R.drawable.default_car);
                            imageViewCar.setImageDrawable(defaultCarDrawable)
                        } else {
                            withContext(Dispatchers.Main) {
                                Glide.with(carItemBinding.root.context).load(findImgRoom.imgCar)
                                    .into(imageViewCar)
                            }
                        }
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
        holder.apply {
            val car = getItem(position)
            bind(car)
            choosingCar(car, holder)
        }

    }

    private fun CarsListHolder.choosingCar(
        car: CarRoom?,
        holder: CarsListHolder
    ) {
        val typedValuePrimaryColour = TypedValue()
        val typedValueColorSecondary = TypedValue()
        val theme = context?.theme
        theme?.resolveAttribute(R.attr.colorOnPrimary, typedValuePrimaryColour, true)
        theme?.resolveAttribute(R.attr.colorSecondary, typedValueColorSecondary, true)
        val primaryColour = typedValuePrimaryColour.data
        val secondaryColour = typedValueColorSecondary.data
//        sharedViewModel.getChosenCar()?.let {
//            if (it == car) {
//                holder.itemView.setBackgroundColor(secondaryColour)
//                chosenCar = it
//                previousCarView = holder.itemView
//            }
//        }
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