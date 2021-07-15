package com.example.finalprojectacad.adaptors

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.finalprojectacad.R
import com.example.finalprojectacad.data.localDB.entity.CarRoom
import com.example.finalprojectacad.databinding.ItemRvListCarsBinding
import com.example.finalprojectacad.viewModel.CarViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "CarsListAdaptor"

class CarsListAdaptor(
    private val viewModel: CarViewModel
) : ListAdapter<CarRoom, CarsListAdaptor.CarsListHolder>(CarsComparator()) {

    lateinit var context: Context

    var chosenCar: CarRoom? = null
    var previousCarView: View? = null

    inner class CarsListHolder(private val carItemBinding: ItemRvListCarsBinding) :
        RecyclerView.ViewHolder(carItemBinding.root) {
        fun bind(carItem: CarRoom) {
            carItemBinding.apply {
                val brandAndModelText = "${carItem.brandName} ${carItem.modelName}"
                textViewBrand.text = brandAndModelText
                var specificationText = ""
                if (carItem.transmissionName.isNotEmpty()) {
                    specificationText += "transmission: ${carItem.transmissionName}\n"
                }
                if (carItem.year != -1) {
                    specificationText += "year: ${carItem.year}\n"
                }

                if (carItem.engine.isNotEmpty()) {
                    specificationText += "engine: ${carItem.engine}\n"
                }
                if (carItem.mileage != -1) {
                    specificationText += "mileage: ${carItem.mileage}"
                }
                textViewSpecification.text = specificationText
                CoroutineScope(Dispatchers.Main).launch {
                    val findImgRoom =
                        viewModel.listAllImages.find { imageCarRoom -> imageCarRoom.id == carItem.carId }
                    Log.d(TAG, "bind: findImgRoom: ${findImgRoom.toString()} ")
                    if (findImgRoom == null) {
                        val defaultCarDrawable =
                            AppCompatResources.getDrawable(context, R.drawable.default_car);
                        imageViewCar.setImageDrawable(defaultCarDrawable)
                    } else {
                        Glide.with(carItemBinding.root.context).load(findImgRoom.imgCar)
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
        holder.apply {
            val car = getItem(position)
            bind(car)
            viewModel.getChosenCar()?.let {
                if (it == car) {
                    holder.itemView.setBackgroundColor(Color.YELLOW)
                    chosenCar = it
                    previousCarView = holder.itemView
                }
            }
            itemView.setOnClickListener { view ->
                if (chosenCar == null) {
                    chosenCar = car
                    view.setBackgroundColor(Color.YELLOW)
                    previousCarView = view
                    carChoiceChanger(car)
                } else if (chosenCar == car) {
                    view.setBackgroundColor(Color.WHITE)
                    chosenCar = null
                    previousCarView = null
                    carChoiceChanger(null)
                } else {
                    previousCarView?.setBackgroundColor(Color.WHITE)
                    view.setBackgroundColor(Color.YELLOW)
                    chosenCar = car
                    previousCarView = view
                    carChoiceChanger(car)
                }

            }
            itemView.setOnLongClickListener { view ->
                val navigation = Navigation.findNavController(view)
                viewModel.setCarToEdit(car)
                navigation.navigate(R.id.action_listCarsFragment_to_addCarFragment)
                true
            }
        }

    }

    private fun carChoiceChanger(car: CarRoom?) {
        viewModel.setChosenCar(car)
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