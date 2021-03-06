package com.beta.finalprojectacad.ui.adaptors

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.beta.finalprojectacad.R
import com.beta.finalprojectacad.data.localDB.entity.CarRoom
import com.beta.finalprojectacad.data.localDB.entity.RouteRoom
import com.beta.finalprojectacad.databinding.ItemRvListTracksBinding
import com.beta.finalprojectacad.other.utilities.RouteUtils
import com.beta.finalprojectacad.viewModel.ListRoutesViewModel
import com.beta.finalprojectacad.viewModel.SharedViewModel
import com.bumptech.glide.Glide
import java.text.DateFormat
import java.text.SimpleDateFormat

class RouteListAdaptor(
    private val viewModel: ListRoutesViewModel,
    private val sharedViewModel: SharedViewModel
) : ListAdapter<RouteRoom, RouteListAdaptor.RoutesListHolder>(RouteComparator()) {

    private var context: Context? = null

    inner class RoutesListHolder(private val routeItemBinding: ItemRvListTracksBinding) :
        RecyclerView.ViewHolder(routeItemBinding.root) {
        fun bind(routeItem: RouteRoom) {
            routeItemBinding.apply {
                context?.let { context ->
                    val formatterStartTime =
                        SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                    val formattedStartTime = formatterStartTime.format(routeItem.startDriveTime)
                    textViewStartDrivingTimeRv.text = context.resources
                        .getString(R.string.start_time).plus(": \n $formattedStartTime")
                    var car: CarRoom? = sharedViewModel.getChosenCar()
                    if (car == null) {
                        car = viewModel.listAllCars?.find { it.carId == routeItem.carId }
                    }
                    car?.let {
                        val chosenCarText = "${it.brandName} ${it.modelName}"
                        textViewChosenVehicleRv.text = chosenCarText //later to do correct string with placeholder
                    }
                    textViewDroveDistanceRv.text = context.resources.getString(R.string.distance)
                        .plus(
                            ": ${routeItem.distance} " +
                                    context.resources.getString(R.string.metres)
                        )
                    val formattedDurationTime = RouteUtils.getFormattedTime(routeItem.duration)
                    textViewDurationDrivingRv.text = context.resources.getString(R.string.duration)
                        .plus(": $formattedDurationTime")
                    var frontText = context.resources.getString(R.string.avg_speed)
                    val behindText = context.getString(R.string.km_per_hour)
                    val avgSpeedText = "$frontText ${routeItem.avgSpeed} $behindText"
                    textViewAvgSpeedRv.text = avgSpeedText //later to do correct string with placeholder
                    frontText = context.resources.getString(R.string.max_speed).plus(":")
                    val maxSpeedText = "$frontText ${routeItem.maxSpeed} $behindText"
                    textViewMaxSpeedRv.text = maxSpeedText //later to do correct string with placeholder
                    if (routeItem.imgRoute.isNotEmpty()) {
                        Glide.with(root).load(routeItem.imgRoute).into(imageViewRouteImg)
                        imageViewRouteImg.setImageURI(routeItem.imgRoute.toUri())
                    }
                }
            }
        }
    }

    class RouteComparator : DiffUtil.ItemCallback<RouteRoom>() {
        override fun areItemsTheSame(oldItem: RouteRoom, newItem: RouteRoom): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: RouteRoom, newItem: RouteRoom): Boolean {
            return oldItem.duration == newItem.duration
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoutesListHolder {
        context = parent.context
        val layoutInflater = LayoutInflater.from(context)
        val routeItemBinding = ItemRvListTracksBinding.inflate(layoutInflater, parent, false)
        return RoutesListHolder(routeItemBinding)
    }

    override fun onBindViewHolder(holder: RoutesListHolder, position: Int) {
        holder.bind(getItem(position))
    }
}