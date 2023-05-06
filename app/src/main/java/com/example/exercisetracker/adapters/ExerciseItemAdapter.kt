package com.example.exercisetracker.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.exercisetracker.MyExercisesFragmentDirections
import com.example.exercisetracker.databinding.ExerciseItemBinding
import com.example.exercisetracker.db.Exercise
import androidx.navigation.fragment.findNavController
import com.example.exercisetracker.db.UserExercise

// Legg til- knappen for en øvelse i recycler view er ikke implementert
// En adapter for både MyExercises fragment (da vises edit knappen) og for ProgramDetailsFragment
// (da vises legg til øvelse-knappen)
class ExerciseItemAdapter()
    : ListAdapter<UserExercise, ExerciseItemAdapter.ExerciseViewHolder>(DiffCallback) {

    class ExerciseViewHolder(private var binding: ExerciseItemBinding)
        : RecyclerView.ViewHolder(binding.root){

        fun bind(userExercise: UserExercise) {
            binding.exercise = userExercise
            binding.buttonAdd.visibility= View.GONE
            /** binding.buttonEdit.setOnClickListener {
                val action = MyExercisesFragmentDirections
                    .actionMyExercisesFragmentToNewExerciseFragment(userExercise.id)
                findNavController().navigate(action)
            } **/
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            ExerciseViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ExerciseViewHolder(
            ExerciseItemBinding.inflate(layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val userExercise = getItem(position)
        holder.bind(userExercise)
    }

    companion object DiffCallback: DiffUtil.ItemCallback<UserExercise>() {
        override fun areItemsTheSame(oldItem: UserExercise, newItem: UserExercise): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: UserExercise, newItem: UserExercise): Boolean {
            return oldItem.description == newItem.description
        }
    }
}