package com.example.finalprojectacad.adaptors

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.finalprojectacad.R
import com.example.finalprojectacad.databinding.ItemRvListCarsBinding
import com.example.finalprojectacad.data.localDB.entity.CarRoom
import com.example.finalprojectacad.viewModel.CarViewModel

class CarsListAdaptor(
    private val viewModel: CarViewModel
): ListAdapter<CarRoom, CarsListAdaptor.CarsListHolder>(CarsComparator()) {

    lateinit var context: Context

    var chosenCar: CarRoom? = null
    var previousCarView: View? = null

    inner class CarsListHolder(private val carItemBinding: ItemRvListCarsBinding):
            RecyclerView.ViewHolder(carItemBinding.root) {
        fun bind(carItem: CarRoom) {
            val findImgRoom = viewModel.listAllImages.find { it.id == carItem.carId }
            carItemBinding.apply {
                textViewBrand.text = carItem.brandName
                textViewModel.text = carItem.modelName
                textViewYear.text = carItem.year.toString()
                textViewMilleage.text = carItem.mileage.toString()
                findImgRoom?.let {
                    imageViewCar.setImageURI(it.imgCar.toUri())
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
                }

                carChoiceChanger(car)
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


    class CarsComparator: DiffUtil.ItemCallback<CarRoom>(){
        override fun areItemsTheSame(oldItem: CarRoom, newItem: CarRoom): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: CarRoom, newItem: CarRoom): Boolean {
            return oldItem.brandName == newItem.brandName
        }
    }

}