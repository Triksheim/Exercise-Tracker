package com.example.exercisetracker.adapters


import android.provider.Settings.Global.getString
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.exercisetracker.R
import com.example.exercisetracker.databinding.ProgramItemBinding
import com.example.exercisetracker.db.UserProgram
import com.example.exercisetracker.db.UserProgramEntity


class ProgramItemAdapter(
    private val userProgramClickListener: UserProgramClickListener,
    private val onItemClickListener: (UserProgram) -> Unit
) : ListAdapter<UserProgram, ProgramItemAdapter.ProgramViewHolder>(DiffCallback) {

    interface UserProgramClickListener {
        fun onEditButtonClick(userProgram: UserProgram)
    }

    inner class ProgramViewHolder(private val binding: ProgramItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(userProgram: UserProgram) {
            binding.apply {
                programTiming.text = if(userProgram.use_timing == 0) "Timing: no" else "Timing: yes"
                programName.text = userProgram.name
                programDescription.text = userProgram.description
                root.setOnClickListener {
                    onItemClickListener(userProgram)
                }
                editProgramButton.setOnClickListener{userProgramClickListener.onEditButtonClick(userProgram)}
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