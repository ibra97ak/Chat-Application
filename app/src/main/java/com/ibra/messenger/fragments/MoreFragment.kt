package com.ibra.messenger.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ibra.messenger.R
import kotlinx.android.synthetic.main.activity_main.*


class MoreFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val nameFragment =activity?.findViewById<TextView>(R.id.ToolBarNameFragment)
        nameFragment?.text = "More"
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_more, container, false)
    }


}