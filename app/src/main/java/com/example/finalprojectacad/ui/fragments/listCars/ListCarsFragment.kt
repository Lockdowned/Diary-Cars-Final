package com.example.finalprojectacad.ui.fragments.listCars

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalprojectacad.R
import com.example.finalprojectacad.databinding.FragmentListCarsBinding
import com.example.finalprojectacad.other.utilities.FragmentsHelper
import com.example.finalprojectacad.other.utilities.RemoteSynchronizeUtils
import com.example.finalprojectacad.ui.SharedViewModel
import com.example.finalprojectacad.viewModel.CarViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val TAG = "ListCarsFragment"

@AndroidEntryPoint
class ListCarsFragment : Fragment() {

    private val viewModel: CarViewModel by activityViewModels()
    private val newViewModel: ListCarsViewModel by activityViewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var binding: FragmentListCarsBinding? = null

    private var carsListAdaptor: CarsListAdaptor? = null

    private var searchText = ""

    @Inject
    lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListCarsBinding.inflate(layoutInflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        carsListAdaptor = viewModel.createOrGetCarsRVAdaptor()
        carsListAdaptor = newViewModel.getCarsRVAdaptor()
        if (carsListAdaptor == null) {
            carsListAdaptor = CarsListAdaptor(newViewModel, sharedViewModel)
            newViewModel.setCarsRVAdaptor(carsListAdaptor!!)
        }

        binding?.apply {

            textViewSearchBarCarsList.doOnTextChanged { text, _, _, _ ->
                searchText = text.toString()
                Log.d(TAG, "onViewCreated: search text: $searchText")
                setFilteredCarsInAdaptor()
            }

            val navigation = Navigation.findNavController(view)

            imageButtonToAuthorizationOrSettings.setOnClickListener {
                if (RemoteSynchronizeUtils.checkLoginUser(auth)) {
                    navigation.navigate(R.id.action_listCarsFragment_to_profileSetingsFragment)
                } else {
                    navigation.navigate(R.id.action_listCarsFragment_to_registrationFragment)
                }
            }

            imageButtonToCarAddFragment.setOnClickListener {
                navigation.navigate(R.id.action_listCarsFragment_to_addCarFragment)
            }

            rvListCars.adapter = carsListAdaptor
            rvListCars.layoutManager = LinearLayoutManager(context)
        }

//        viewModel.getAllCars.observe(
//            viewLifecycleOwner, Observer { list ->
//                viewModel.listAllCars = list
//                carsListAdaptor?.submitList(list)
//            }
//        )
        newViewModel.allCarsLiveData.observe(
            viewLifecycleOwner, Observer { list ->
                newViewModel.listAllCars = list
                carsListAdaptor?.submitList(list)
            }
        )


//        viewModel.getAllImages.observe(
//            viewLifecycleOwner, Observer { list ->
//                viewModel.listAllImages = list
//                carsListAdaptor?.notifyDataSetChanged()
//            }
//        )
        newViewModel.allImagesLiveData.observe(
            viewLifecycleOwner, Observer { list ->
                newViewModel.listAllImages = list
                carsListAdaptor?.notifyDataSetChanged()
            }
        )
    }

    private fun setFilteredCarsInAdaptor() {
        val carsList = newViewModel.listAllCars
        if (searchText.isEmpty()) {
            carsListAdaptor?.submitList(carsList)
            return
        }
        val filteredCarList = FragmentsHelper.filterCarList(carsList, searchText)
        carsListAdaptor?.submitList(filteredCarList)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        val searchText = binding?.textViewSearchBarCarsList?.text?.toString()
        searchText?.let {
            outState.putString("searchText", searchText)
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        val savedSearchText = savedInstanceState?.getString("searchText")
        savedSearchText?.let {
            binding?.textViewSearchBarCarsList?.setText(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}