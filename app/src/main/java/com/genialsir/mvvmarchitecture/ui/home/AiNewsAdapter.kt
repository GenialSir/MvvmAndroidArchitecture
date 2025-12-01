package com.genialsir.mvvmarchitecture.ui.home

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.genialsir.mvvmarchitecture.data.dto.home.AINewsItem
import com.genialsir.mvvmarchitecture.databinding.ItemAiNewsBinding
import com.genialsir.mvvmarchitecture.ui.web.NewsWebActivity

/**
 * @author genialsir@163.com (GenialSir) on 2025/12/1
 */



class AiNewsAdapter :
    PagingDataAdapter<AINewsItem, AiNewsAdapter.NewsViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<AINewsItem>() {
            override fun areItemsTheSame(oldItem: AINewsItem, newItem: AINewsItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: AINewsItem, newItem: AINewsItem): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = ItemAiNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    inner class NewsViewHolder(private val binding: ItemAiNewsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AINewsItem) {
            binding.tvTitle.text = item.title
            binding.tvSource.text = item.source
            binding.tvTime.text = item.ctime
            binding.tvDescription.text = item.description

            Glide.with(binding.ivPic.context)
                .load(item.picUrl)
                .into(binding.ivPic)

            // 点击跳转新闻详情
            binding.root.setOnClickListener {
                val intent = Intent(it.context, NewsWebActivity::class.java)
                intent.putExtra("url", item.url)
                it.context.startActivity(intent)
            }
        }
    }
}
