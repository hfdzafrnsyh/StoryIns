package com.example.storyins.ui.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.storyins.R
import com.example.storyins.databinding.ItemListStoryBinding
import com.example.storyins.source.model.local.entity.StoryEntity
import com.example.storyins.utils.Helpers.formatDate


class StoryViewAdapter(private val storyClickCallback: StoryClickCallback) :
    PagingDataAdapter<StoryEntity,StoryViewAdapter.StoryViewHolder>(DIFF_CALLBACK) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val itemListStoryBinding = ItemListStoryBinding.inflate(LayoutInflater.from(parent.context) , parent , false)
        return StoryViewHolder(itemListStoryBinding)

    }

    override fun onBindViewHolder(storyViewHolder: StoryViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            storyViewHolder.bind(data,storyClickCallback)
        }
    }


     inner class StoryViewHolder(private val binding: ItemListStoryBinding) : RecyclerView.ViewHolder(binding.root) {


        fun bind(story : StoryEntity, storyClickCallback: StoryClickCallback){
            with(binding){

                tvName.text = story.name

                val date = formatDate(story.createdAt)
                tvDate.text = itemView.context.getString(R.string.date , date)


                Glide.with(itemView.context)
                    .load(story.photoUrl)
                    .placeholder(R.drawable.shape_background_image)
                    .transform(CenterCrop(),RoundedCorners(20))
                    .into(ivStory)

                itemView.setOnClickListener {
                    val optionCompact : ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(tvName , "name"),
                        Pair(tvDate, "date"),
                        Pair(ivStory, "story")
                    )

                    storyClickCallback.showDetail(story,optionCompact)

                }

                ibLocation.setOnClickListener {

                    storyClickCallback.showDialogLocation(story)

                }

            }
        }

    }


    interface StoryClickCallback{
        fun showDetail(story : StoryEntity, optionCompact : ActivityOptionsCompat)
        fun showDialogLocation(story : StoryEntity)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryEntity>() {
            override fun areItemsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}