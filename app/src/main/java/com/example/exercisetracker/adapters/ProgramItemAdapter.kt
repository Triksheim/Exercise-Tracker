package com.example.exercisetracker.adapters


import android.graphics.Color
import android.provider.Settings.Global.getString
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.exercisetracker.INDOORCOLOR
import com.example.exercisetracker.OUTDOORCOLOR
import com.example.exercisetracker.R
import com.example.exercisetracker.databinding.ProgramItemBinding
import com.example.exercisetracker.db.AppProgramType
import com.example.exercisetracker.db.UserProgram
import com.example.exercisetracker.db.UserProgramEntity
import com.google.android.material.card.MaterialCardView
import com.google.android.material.color.MaterialColors.getColor

const val SET_COLOR_OUTDOOR = R.color.green_100
const val SET_COLOR_INDOOR = R.color.purple_100

class ProgramItemAdapter(
    private val userProgramType: UserProgramType,
    private val userProgramClickListener: UserProgramClickListener,
    private val onItemClickListener: (UserProgram) -> Unit
) : ListAdapter<UserProgram, ProgramItemAdapter.ProgramViewHolder>(DiffCallback) {

    interface UserProgramClickListener {
        fun onEditButtonClick(userProgram: UserProgram)
        fun onStartProgramButtonClick(userProgram: UserProgram)
    }

    interface UserProgramType {
        fun getProgramTypeForProgram(userProgram: UserProgram): AppProgramType?
    }

    inner class ProgramViewHolder(private val binding: ProgramItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val cardView: CardView = binding.root.findViewById(R.id.programCardView)

        fun bind(userProgram: UserProgram) {
            binding.apply {
                programTiming.text = if(userProgram.use_timing == 0) "Timing: No" else "Timing: Yes"
                programName.text = userProgram.name
                programDescription.text = userProgram.description
                root.setOnClickListener {
                    onItemClickListener(userProgram)
                }
                editProgramButton.setOnClickListener{userProgramClickListener.onEditButtonClick(userProgram)}
                startProgramButton.setOnClickListener{userProgramClickListener.onStartProgramButtonClick(userProgram)}

                // Set icon and backgound color corresponding the app_program_type
                val programType = userProgramType.getProgramTypeForProgram(userProgram)
                if (programType != null) {
                    val resourceId = itemView.context.resources.getIdentifier(
                        programType.icon,
                        "drawable",
                        itemView.context.packageName)
                    programIcon.setImageResource(resourceId)

                    when(programType.back_color) {
                        INDOORCOLOR -> cardView.setCardBackgroundColor(itemView.context.getColor(SET_COLOR_INDOOR))
                        OUTDOORCOLOR -> cardView.setCardBackgroundColor(itemView.context.getColor(SET_COLOR_OUTDOOR))
                    }
                }
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            ProgramItemAdapter.ProgramViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ProgramViewHolder(
            ProgramItemBinding.inflate(layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ProgramItemAdapter.ProgramViewHolder, position: Int) {
        val userProgram = getItem(position)
        holder.bind(userProgram)
    }

    companion object DiffCallback: DiffUtil.ItemCallback<UserProgram>() {
        override fun areItemsTheSame(oldItem: UserProgram, newItem: UserProgram): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: UserProgram, newItem: UserProgram): Boolean {
            return oldItem.description == newItem.description
        }
    }
}