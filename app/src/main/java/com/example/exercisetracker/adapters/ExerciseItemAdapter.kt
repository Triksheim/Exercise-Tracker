package com.example.exercisetracker.adapters
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.exercisetracker.databinding.ExerciseItemBinding
import com.example.exercisetracker.R
import com.example.exercisetracker.db.UserExercise


class ExerciseItemAdapter(private val exerciseClickListener: ExerciseClickListener, private var addButton: Int)
    : ListAdapter<UserExercise, ExerciseItemAdapter.ExerciseViewHolder>(DiffCallback) {

    interface ExerciseClickListener {
        fun onEditButtonClick(exerciseId: Int)
        fun onAddButtonClick(exerciseId: Int)
    }

    class ExerciseViewHolder(private val binding: ExerciseItemBinding, exerciseClickListener: ExerciseClickListener, addButton: Int)
        : RecyclerView.ViewHolder(binding.root){
        fun bind(userExercise: UserExercise) {
            binding.exercise = userExercise
            binding.executePendingBindings()
        }
        init {
            if(addButton == 0) {
                binding.buttonAdd.visibility = View.INVISIBLE
            } else { binding.buttonAdd.setOnClickListener{
                exerciseClickListener.onAddButtonClick(binding.exercise!!.id) }
            }

            binding.buttonEdit.setOnClickListener{
                exerciseClickListener.onEditButtonClick(binding.exercise!!.id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            ExerciseViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ExerciseViewHolder(
            ExerciseItemBinding.inflate(layoutInflater, parent, false),
            exerciseClickListener, addButton
        )
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val userExercise = getItem(position)
        holder.bind(userExercise)

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