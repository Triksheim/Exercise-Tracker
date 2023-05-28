package com.example.exercisetracker.adapters
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.exercisetracker.*
import com.example.exercisetracker.databinding.ExerciseItemBinding
import com.example.exercisetracker.db.UserExercise

// This adapter is used in three recyclerViews. Visibility of views are dependent on what recyclerView is to be populated
// The calling fragment is specified in recyclerLocation

const val SET_COLOR_INDOOR_EXERCISE = R.color.purple_200
class ExerciseItemAdapter(
    private val exerciseClickListener: ExerciseClickListener,
    private val recyclerLocation: String)
    : ListAdapter<UserExercise, ExerciseItemAdapter.ExerciseViewHolder>(DiffCallback) {

    interface ExerciseClickListener {
        fun onEditButtonClick(userExercise: UserExercise)
        fun onAddButtonClick(userExercise: UserExercise)
        fun onRemoveButtonClick(userExercise: UserExercise)
    }

    class ExerciseViewHolder(private val binding: ExerciseItemBinding,
                             exerciseClickListener: ExerciseClickListener,
                             recyclerLocation: String)
        : RecyclerView.ViewHolder(binding.root){
        private lateinit var userExercise: UserExercise

        fun bind(userExercise: UserExercise) {
            this.userExercise = userExercise
            binding.exercise = userExercise
            val resourceId = itemView.context.resources.getIdentifier(
                userExercise.icon,
                "drawable",
                itemView.context.packageName
            )
            binding.exerciseIcon.setImageResource(resourceId)
            binding.executePendingBindings()
        }

        init {
            when (recyclerLocation) {
                EXERCISES_FRAGMENT -> {
                    binding.buttonAdd.visibility = View.GONE
                    binding.buttonRemove.visibility = View.GONE
                    binding.buttonEdit.visibility = View.VISIBLE
                    binding.buttonEdit.setOnClickListener {
                        exerciseClickListener.onEditButtonClick(binding.exercise!!)
                    }
                }
                DETAIL_FRAGMENT_BOTTOM -> {
                    binding.buttonRemove.visibility = View.GONE
                    binding.buttonAdd.visibility = View.VISIBLE
                    binding.buttonAdd.setOnClickListener {
                        exerciseClickListener.onAddButtonClick(binding.exercise!!)
                    }
                    binding.buttonEdit.visibility = View.VISIBLE
                    binding.buttonEdit.setOnClickListener {
                        exerciseClickListener.onEditButtonClick(binding.exercise!!)
                    }
                }
                DETAIL_FRAGMENT_UPPER -> {
                    binding.buttonAdd.visibility = View.GONE
                    binding.buttonEdit.visibility = View.GONE
                    binding.buttonRemove.visibility = View.VISIBLE
                    binding.buttonRemove.setOnClickListener {
                        exerciseClickListener.onRemoveButtonClick(binding.exercise!!)
                    }
                }
                PROGRAM_SESSION_FRAGMENT -> {
                    binding.buttonAdd.visibility = View.GONE
                    binding.buttonRemove.visibility = View.GONE
                    binding.buttonEdit.visibility = View.GONE
                }
                SESSION_DETAILS -> {
                    binding.buttonAdd.visibility = View.GONE
                    binding.buttonRemove.visibility = View.GONE
                    binding.buttonEdit.visibility = View.GONE
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            ExerciseViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ExerciseViewHolder(
            ExerciseItemBinding.inflate(layoutInflater, parent, false),
            exerciseClickListener, recyclerLocation
        )
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val userExercise = getItem(position)
        holder.bind(userExercise)

        // Set indoor-exercise backgoundcolor
        val color = holder.itemView.context.getColor(SET_COLOR_INDOOR_EXERCISE)
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