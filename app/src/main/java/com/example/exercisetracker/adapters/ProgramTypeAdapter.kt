package com.example.exercisetracker.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.ui.layout.Layout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.exercisetracker.databinding.ProgramTypeItemBinding
import com.example.exercisetracker.db.AppProgramType

class ProgramTypeAdapter(private val clickListener: (AppProgramType) -> Unit)
    : ListAdapter<AppProgramType, ProgramTypeAdapter.ProgramViewHolder>(DiffCallback) {


    class ProgramViewHolder(private var binding: ProgramTypeItemBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(appProgramType: AppProgramType) {
            binding.program = appProgramType
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
    : ProgramViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ProgramViewHolder(
            ProgramTypeItemBinding.inflate(layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ProgramViewHolder, position: Int) {
        val programType = getItem(position)

        val color = programType.back_color.substring(1).toInt(16)
        holder.itemView.setBackgroundColor(color)
        holder.itemView.setOnClickListener {
            clickListener(programType)
        }
        holder.bind(programType)
    }

    companion object DiffCallback: DiffUtil.ItemCallback<AppProgramType>() {
        override fun areItemsTheSame(oldItem: AppProgramType, newItem: AppProgramType): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: AppProgramType, newItem: AppProgramType): Boolean {
            return oldItem.description == newItem.description
        }
    }

}


