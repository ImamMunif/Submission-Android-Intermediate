package com.dicoding.storysubmission.view.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.storysubmission.data.response.ListStoryItem
import com.dicoding.storysubmission.databinding.ItemLayoutBinding

class StoryListAdapter(private val storyList: List<ListStoryItem>) :
    RecyclerView.Adapter<StoryListAdapter.UserViewHolder>() {

    inner class UserViewHolder(private val binding: ItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(story: ListStoryItem) {
            binding.tvItemName.text = story.name
            binding.tvItemDescription.text = story.description

            Glide
                .with(itemView.context)
                .load(story.photoUrl)
                .fitCenter()
                .into(binding.imgItemPhoto)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder(
            ItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return storyList.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val githubUser = storyList[position]
        holder.bind(githubUser)
    }
}