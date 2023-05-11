package com.example.exercisetracker.adapters
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.exercisetracker.databinding.ExerciseItemBinding
import com.example.exercisetracker.R
import com.example.exercisetracker.db.UserExercise
import kotlinx.coroutines.NonDisposableHandle.parent


class ExerciseItemAdapter(private val exerciseClickListener: ExerciseClickListener)
    : ListAdapter<UserExercise, ExerciseItemAdapter.ExerciseViewHolder>(DiffCallback) {

    interface ExerciseClickListener {
        fun onEditButtonClick(position: Int, exerciseId: Int)
        fun onAddButtonClick(position: Int,exerciseId: Int)
    }

    class ExerciseViewHolder(private val binding: ExerciseItemBinding, exerciseClickListener: ExerciseClickListener)
        : RecyclerView.ViewHolder(binding.root){
        fun bind(userExercise: UserExercise,  position: Int) {
            binding.exercise = userExercise
            binding.executePendingBindings()
        }
        init {
            binding.buttonAdd.setOnClickListener{
            exerciseClickListener.onAddButtonClick(position, binding.exercise!!.id)
            }
            binding.buttonEdit.setOnClickListener{
                exerciseClickListener.onEditButtonClick(position, binding.exercise!!.id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            ExerciseViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ExerciseViewHolder(
            ExerciseItemBinding.inflate(layoutInflater, parent, false),
            exerciseClickListener
        )
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val userExercise = getItem(position)
        holder.bind(userExercise, position)

        val color = ContextCompat.getColor(holder.itemView.context, R.color.bg_exercise)
        holder.itemView.setBackgroundColor(color)

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