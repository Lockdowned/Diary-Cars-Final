package com.example.finalprojectacad.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalprojectacad.R
import com.example.finalprojectacad.adaptors.CarsListAdaptor
import com.example.finalprojectacad.databinding.FragmentListCarsBinding
import com.example.finalprojectacad.viewModel.CarViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "ListCarsFragment"

@AndroidEntryPoint
class ListCarsFragment : Fragment() { // our fragment are recreated from bottom navigate

    private val viewModel: CarViewModel by activityViewModels()

    private lateinit var binding: FragmentListCarsBinding
    
    private var carsListAdaptor: CarsListAdaptor? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListCarsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (carsListAdaptor == null){
            carsListAdaptor = CarsListAdaptor(viewModel)
        }



        binding.apply {
            val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationBar)
            navBar.visibility = View.VISIBLE

            val navigation = Navigation.findNavController(view)
            imageButton.setOnClickListener {
                navigation.navigate(R.id.action_listCarsFragment_to_registrationFragment)
            }

            rvListCars.adapter = carsListAdaptor
            rvListCars.layoutManager = LinearLayoutManager(context)
        }

        viewModel.getAllCars.observe(
            viewLifecycleOwner, Observer {
                carsListAdaptor?.submitList(it)
            }
        )

        viewModel.getAllImages.observe(
            viewLifecycleOwner, Observer {
                viewModel.listAllImages = it
                carsListAdaptor?.notifyDataSetChanged()//find better solution
            }
        )


    }

}