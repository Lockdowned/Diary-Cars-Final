package com.example.finalprojectacad.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalprojectacad.adaptors.RouteListAdaptor
import com.example.finalprojectacad.databinding.FragmentListTracksBinding
import com.example.finalprojectacad.data.localDB.entity.CarRoom
import com.example.finalprojectacad.other.enums.RouteSortType
import com.example.finalprojectacad.viewModel.CarViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListTracksFragment : Fragment() {

    private var binding: FragmentListTracksBinding? = null
    private val viewModel: CarViewModel by activityViewModels()
    private var chosenCar: CarRoom? = null
    private var routeListAdaptor: RouteListAdaptor? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListTracksBinding.inflate(layoutInflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        routeListAdaptor = viewModel.createOrGetRoutesRVAdaptor()


        binding?.apply {

            recyclerViewListRoutes.adapter = routeListAdaptor
            recyclerViewListRoutes.layoutManager = LinearLayoutManager(context)


            chosenCar = viewModel.getChosenCar()
            if (chosenCar != null) {
                textViewChosenCar.text = "${chosenCar?.brandName} ${chosenCar?.modelName}"
            }

            when(viewModel.routeSortType) {
                RouteSortType.DATE -> spinnerRouteSortType.setSelection(0)
                    RouteSortType.DISTANCE -> spinnerRouteSortType.setSelection(1)
                RouteSortType.DURATION -> spinnerRouteSortType.setSelection(2)
                RouteSortType.AVG_SPEED -> spinnerRouteSortType.setSelection(3)
                RouteSortType.MAX_SPEED -> spinnerRouteSortType.setSelection(4)
            }
            spinnerRouteSortType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                   when(position) {
                       0 -> viewModel.sortRoutes(RouteSortType.DATE)
                       1 -> viewModel.sortRoutes(RouteSortType.DISTANCE)
                       2 -> viewModel.sortRoutes(RouteSortType.DURATION)
                       3 -> viewModel.sortRoutes(RouteSortType.AVG_SPEED)
                       4 -> viewModel.sortRoutes(RouteSortType.MAX_SPEED)
                   }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
        }


        viewModel.getAllCars.observe(
            viewLifecycleOwner, Observer {
                viewModel.listAllCars = it
            }
        )

//        viewModel.allRoutes.observe(
//            viewLifecycleOwner, Observer {
//                routeListAdaptor?.submitList(it)
//            }
//        )



        viewModel.routesSorted.observe(
            viewLifecycleOwner, Observer {
                routeListAdaptor?.submitList(it)
            })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}