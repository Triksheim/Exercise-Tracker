package com.example.exercisetracker.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.exercisetracker.databinding.ProgramTypeItemBinding
import com.example.exercisetracker.db.AppProgramType

class ProgramTypeAdapter(private val onItemCLicked: (AppProgramType) -> Unit)
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
        return ProgramViewHolder(
            ProgramTypeItemBinding.inflate(
                LayoutInflater.from(
                    parent.context)))
    }

    override fun onBindViewHolder(holder: ProgramViewHolder, position: Int) {
        val programType = getItem(position)
        holder.itemView.setOnClickListener {
            onItemCLicked(programType)
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
