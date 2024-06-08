package com.dicoding.storysubmission.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.storysubmission.data.response.ListStoryItem
import com.dicoding.storysubmission.databinding.ItemLayoutBinding

class StoryListAdapter(
    private val onItemClick: (ListStoryItem) -> Unit
    ) : PagingDataAdapter<ListStoryItem, StoryListAdapter.UserViewHolder>(DIFF_CALLBACK) {

    class UserViewHolder(
        private val binding: ItemLayoutBinding,
        private val onItemClick: (ListStoryItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(story: ListStoryItem) {
            binding.tvItemName.text = story.name
            binding.tvItemDescription.text = story.description

            Glide
                .with(itemView.context)
                .load(story.photoUrl)
                .fitCenter()
                .into(binding.imgItemPhoto)

            binding.itemLayout.setOnClickListener {
                onItemClick(story)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder(
            ItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ),
            onItemClick
        )
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}