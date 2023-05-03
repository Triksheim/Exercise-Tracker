package com.example.exercisetracker

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.exercisetracker.adapters.ProgramTypeAdapter
import com.example.exercisetracker.network.AppProgramTypeJSON

/**
 * Updates the data shown in the [RecyclerView].
 */
@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<AppProgramTypeJSON>?) {
    val adapter = recyclerView.adapter as ProgramTypeAdapter
    adapter.submitList(data)
}