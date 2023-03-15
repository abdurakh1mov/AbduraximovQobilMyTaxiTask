package com.example.abduraximovqobilmytaxitask.ui.fragments.tripspage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.abduraximovqobilmytaxitask.databinding.FragmentTripsBinding

class TripsFragment : Fragment() {
    private lateinit var binding: FragmentTripsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTripsBinding.inflate(inflater, container, false)
        return binding.root
    }

}