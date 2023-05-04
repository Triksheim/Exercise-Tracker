package com.example.exercisetracker

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.exercisetracker.adapters.ProgramTypeAdapter
import com.example.exercisetracker.db.AppProgramType

/**
 * Updates the data shown in the [RecyclerView].
 */
@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<AppProgramType>?) {
    val adapter = recyclerView.adapter as ProgramTypeAdapter
    adapter.submitList(data)
}