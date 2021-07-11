package com.example.finalprojectacad.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalprojectacad.adaptors.RouteListAdaptor
import com.example.finalprojectacad.databinding.FragmentListTracksBinding
import com.example.finalprojectacad.data.localDB.entity.CarRoom
import com.example.finalprojectacad.data.localDB.entity.RouteRoom
import com.example.finalprojectacad.viewModel.CarViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListTracksFragment : Fragment() {

    private lateinit var binding: FragmentListTracksBinding
    private val viewModel: CarViewModel by activityViewModels()
    private var chosenCar: CarRoom? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListTracksBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val routeListAdaptor = RouteListAdaptor(viewModel)




        val navController = Navigation.findNavController(view)
        binding.apply {

            recyclerViewListRoutes.adapter = routeListAdaptor
            recyclerViewListRoutes.layoutManager = LinearLayoutManager(context)


            chosenCar = viewModel.getChosenCar()
            textViewChosenCar.text = "${chosenCar?.brandName} ${chosenCar?.modelName}"

            buttonToTrackingFragment.setOnClickListener {
                if (chosenCar == null) {
                    Snackbar.make(it, "Need to choose a car", Snackbar.LENGTH_LONG).show()
                } else {
                    val bundle = Bundle()
                    bundle.putInt("carId", chosenCar!!.carId!!)
//                    navController.navigate(R.id.action_listTracksFragment_to_trackTripFragment, bundle)
                }
            }
        }


        viewModel.getAllCars.observe(
            viewLifecycleOwner, Observer {
                viewModel.listAllCars = it
            }
        )

        viewModel.allRoutes.observe(
            viewLifecycleOwner, Observer {
                routeListAdaptor.submitList(it)
            }
        )



    }

}