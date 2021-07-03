package com.example.finalprojectacad.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.finalprojectacad.R
import com.example.finalprojectacad.databinding.FragmentListTracksBinding

class ListTracksFragment : Fragment(R.layout.fragment_list_tracks) {

    private lateinit var binding: FragmentListTracksBinding

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


        val navController = Navigation.findNavController(view)
        binding.apply {
            buttonToTrackingFragment.setOnClickListener {
                navController.navigate(R.id.action_listTracksFragment_to_trackTripFragment)
            }
        }
    }

}