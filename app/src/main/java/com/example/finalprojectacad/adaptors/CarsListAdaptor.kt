package com.example.finalprojectacad.adaptors

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.finalprojectacad.databinding.ItemRvListCarsBinding
import com.example.finalprojectacad.db.entity.CarRoom
import com.example.finalprojectacad.viewModel.CarViewModel
import dagger.hilt.android.AndroidEntryPoint

class CarsListAdaptor(
    private val viewModel: CarViewModel
): ListAdapter<CarRoom, CarsListAdaptor.CarsListHolder>(CarsComparator()) {

    lateinit var context: Context

    inner class CarsListHolder(private val carItemBinding: ItemRvListCarsBinding):
            RecyclerView.ViewHolder(carItemBinding.root) {
        fun bind(carItem: CarRoom) {
            carItemBinding.apply {
                textViewBrand.text = carItem.brandName
                textViewModel.text = carItem.modelName
                textViewYear.text = carItem.year.toString()
                textViewMilleage.text = carItem.mileage.toString()
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
        holder.bind(getItem(position))
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