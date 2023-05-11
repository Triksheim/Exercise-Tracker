package com.example.exercisetracker.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.exercisetracker.R
import com.example.exercisetracker.databinding.StatisticsItemBinding
import com.example.exercisetracker.db.UserProgramSessionData

class StatisticsItemAdapter (private val replayClickListener: ReplayClickListener)
    : ListAdapter<UserProgramSessionData, StatisticsItemAdapter.StatisticsViewHolder>(DiffCallback) {

    interface ReplayClickListener {
        fun onReplayButtonClicked(userProgramSessionDataId: Int)
    }

    class StatisticsViewHolder(private val binding: StatisticsItemBinding, replayClickListener: ReplayClickListener)
        : RecyclerView.ViewHolder(binding.root){

        fun bind(userProgramSessionData: UserProgramSessionData) {
            binding.userProgramSessionData = userProgramSessionData
            binding.executePendingBindings()
        }

        init {
            binding.buttonReplay.setOnClickListener{
                replayClickListener.onReplayButtonClicked(binding.userProgramSessionData!!.user_program_session_id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            StatisticsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return StatisticsViewHolder(
            StatisticsItemBinding.inflate(layoutInflater, parent, false),
            replayClickListener
        )
    }

    override fun onBindViewHolder(holder: StatisticsViewHolder, position: Int) {
        val userProgramSessionData = getItem(position)
        holder.bind(userProgramSessionData)

        val color = ContextCompat.getColor(holder.itemView.context, R.color.bg_statistics)
        holder.itemView.setBackgroundColor(color)
    }


    companion object DiffCallback: DiffUtil.ItemCallback<UserProgramSessionData>() {
        override fun areItemsTheSame(oldItem: UserProgramSessionData, newItem: UserProgramSessionData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: UserProgramSessionData, newItem: UserProgramSessionData): Boolean {
            return oldItem.user_program_session_id == newItem.user_program_session_id
        }
    }
}