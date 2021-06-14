package com.example.finalprojectacad.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.Navigation
import com.example.finalprojectacad.R

class ListCarsFragment : Fragment(R.layout.fragment_list_cars) {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragBut = view.findViewById<Button>(R.id.buttonFragment)
        fragBut.setOnClickListener {
            val navigation = Navigation.findNavController(view)
            navigation.navigate(R.id.action_listCarsFragment_to_registrationFragment)
        }
    }

}