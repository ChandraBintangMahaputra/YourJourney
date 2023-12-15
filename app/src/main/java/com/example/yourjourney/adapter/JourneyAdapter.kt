package com.example.yourjourney.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.yourjourney.database.JourneyDb
import com.example.yourjourney.databinding.JourneyListBinding
import com.example.yourjourney.extention.Value
import com.example.yourjourney.extention.setImageUrl
import com.example.yourjourney.presentation.DetailActivity
import timber.log.Timber


class JourneyAdapter: PagingDataAdapter<JourneyDb, JourneyAdapter.StoryViewHolder>(DIFF_CALLBACK) {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<JourneyDb>() {
            override fun areItemsTheSame(oldItem: JourneyDb, newItem: JourneyDb): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: JourneyDb, newItem: JourneyDb): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onBindViewHolder(holder: JourneyAdapter.StoryViewHolder, position: Int) {
        getItem(position)?.let { story ->
            holder.bind(holder.itemView.context, story)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JourneyAdapter.StoryViewHolder {
        val binding = JourneyListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }


    inner class StoryViewHolder(private val binding: JourneyListBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(context: Context, story: JourneyDb) {
            binding.apply {
                imgJourney.setImageUrl(story.photoUrl, true)
                tvStoryTitle.text = story.name
                tvStoryDesc.text = story.description


                root.setOnClickListener {
                    val optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        root.context as Activity,
                        Pair(imgJourney, "thumbnail"),
                        Pair(tvStoryTitle, "title"),
                        Pair(tvStoryDesc, "description"),
                    )
                    val intent = Intent(context, DetailActivity::class.java)
                    intent.putExtra(Value.BUNDLE_KEY_STORY, story)
                    context.startActivity(intent, optionsCompat.toBundle())
                }
            }
        }
    }


}