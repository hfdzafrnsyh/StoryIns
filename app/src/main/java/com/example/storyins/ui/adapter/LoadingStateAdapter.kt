package com.example.storyins.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.storyins.R
import com.example.storyins.databinding.ItemLoadingBarBinding

class LoadingStateAdapter(private val retry: () -> Unit) : LoadStateAdapter<LoadingStateAdapter.LoadingStateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadingStateViewHolder {
        val itemLoadingBarBinding = ItemLoadingBarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LoadingStateViewHolder(itemLoadingBarBinding, retry)
    }

    override fun onBindViewHolder(
        holder: LoadingStateViewHolder,
        loadState: LoadState
    ) {
        holder.bind(loadState)
    }

    class LoadingStateViewHolder(private val binding: ItemLoadingBarBinding, retry: () -> Unit) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.ibRefresh.setOnClickListener { retry.invoke() }
        }

        fun bind(loadState: LoadState) {

            if (loadState is LoadState.Error) {
                binding.errorMsg.text =itemView.context.getString(R.string.error_connection)
            }

            binding.progressBar.isVisible = loadState is LoadState.Loading
            binding.ibRefresh.isVisible = loadState is LoadState.Error
            binding.errorMsg.isVisible = loadState is LoadState.Error

        }
    }
}