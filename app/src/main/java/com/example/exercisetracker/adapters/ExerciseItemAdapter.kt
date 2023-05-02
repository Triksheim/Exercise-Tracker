package com.example.exercisetracker.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.exercisetracker.databinding.ExerciseItemBinding
import com.example.exercisetracker.db.Exercise

// Legg til- knappen for en øvelse i rececler view er ikke implementert
class ExerciseItemAdapter()
    : ListAdapter<Exercise, ExerciseItemAdapter.ExerciseViewHolder>(DiffCallback) {

    class ExerciseViewHolder(private var binding: ExerciseItemBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(userExercise: Exercise) {
            binding.exercise = userExercise
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            ExerciseViewHolder {
        return ExerciseViewHolder(ExerciseItemBinding.inflate(
            LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val userExercise = getItem(position)
        holder.bind(userExercise)
    }

    companion object DiffCallback: DiffUtil.ItemCallback<Exercise>() {
        override fun areItemsTheSame(oldItem: Exercise, newItem: Exercise): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Exercise, newItem: Exercise): Boolean {
            return oldItem.description == newItem.description
        }
    }
}