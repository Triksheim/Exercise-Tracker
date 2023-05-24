package com.example.exercisetracker.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.exercisetracker.databinding.SessionItemBinding
import com.example.exercisetracker.db.DisplayableSession

class SessionItemAdapter(
    private val onItemClickListener: (DisplayableSession) -> Unit
) : ListAdapter<DisplayableSession, SessionItemAdapter.SessionViewHolder>(DiffCallback) {

    inner class SessionViewHolder(private val binding: SessionItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                onItemClickListener(getItem(adapterPosition))
            }
        }

        fun bind(displayableSession: DisplayableSession) {
            binding.apply {
                programName.text = displayableSession.userProgramName
                sessionStartTime.text = displayableSession.sessionStartTime
                val resourceId = itemView.context.resources.getIdentifier(
                    displayableSession.programTypeIcon,
                    "drawable",
                    itemView.context.packageName
                )
                sessionIcon.setImageResource(resourceId)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return SessionViewHolder(
            SessionItemBinding.inflate(layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
        val session = getItem(position)
        holder.bind(session)
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<DisplayableSession>() {
            override fun areItemsTheSame(oldItem: DisplayableSession, newItem: DisplayableSession): Boolean {
                return oldItem.sessionStartTime == newItem.sessionStartTime
            }

            override fun areContentsTheSame(oldItem: DisplayableSession, newItem: DisplayableSession): Boolean {
                return oldItem == newItem
            }
        }
    }
}
