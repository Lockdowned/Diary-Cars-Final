package com.example.finalprojectacad.adaptors

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.finalprojectacad.databinding.ItemRvListTracksBinding
import com.example.finalprojectacad.data.localDB.entity.RouteRoom
import com.example.finalprojectacad.viewModel.CarViewModel

class RouteListAdaptor(
    private val viewModel: CarViewModel
): ListAdapter<RouteRoom, RouteListAdaptor.RoutesListHolder>(RouteComparator()) {


    inner class RoutesListHolder(private val routeItemBinding: ItemRvListTracksBinding):
            RecyclerView.ViewHolder(routeItemBinding.root) {
                fun bind(routeItem:  RouteRoom) {
                    routeItemBinding.apply {
                        textViewStartDrivingTimeRv.text = "Start time: ${routeItem.startDriveTime}"
                        val car = viewModel.listAllCars.find { it.carId == routeItem.carId }
                        car?.let {
                            textViewChosenVehicleRv.text = "${it.brandName} ${it.modelName}"
                        }
                        textViewDroveDistanceRv.text = "distance: ${routeItem.distance} metres"
                        textViewDurationDrivingRv.text = "duration: ${routeItem.duration}"
                        textViewAvgSpeedRv.text = "avg speed: ${routeItem.avgSpeed} km/h"
                        textViewMaxSpeedRv.text = "max speed: ${routeItem.maxSpeed} km/h"
                        if (routeItem.imgRoute.isNotEmpty())
                        imageViewRouteImg.setImageURI(routeItem.imgRoute.toUri())
                    }
                }
            }

    class RouteComparator: DiffUtil.ItemCallback<RouteRoom>() {
        override fun areItemsTheSame(oldItem: RouteRoom, newItem: RouteRoom): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: RouteRoom, newItem: RouteRoom): Boolean {
            return oldItem.duration == newItem.duration
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoutesListHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val routeItemBinding = ItemRvListTracksBinding.inflate(layoutInflater, parent, false)
        return RoutesListHolder(routeItemBinding)
    }

    override fun onBindViewHolder(holder: RoutesListHolder, position: Int) {
        val itemRoute = getItem(position)
        holder.bind(itemRoute)
    }

}