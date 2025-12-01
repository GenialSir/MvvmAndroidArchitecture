package com.genialsir.mvvmcommon.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.genialsir.mvvmcommon.R

/**
 * @author genialsir@163.com (GenialSir) on 2025/10/21
 */
class LoadStateAdapter(
    private val retry: () -> Unit
) : androidx.paging.LoadStateAdapter<LoadStateAdapter.LoadStateViewHolder>() {

    inner class LoadStateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val progressBar: ProgressBar = itemView.findViewById(R.id.pb_load_more)
        private val tvLoading: TextView = itemView.findViewById(R.id.tv_loading)
        private val tvError: TextView = itemView.findViewById(R.id.tv_load_more_error)
        private val btnRetry: Button = itemView.findViewById(R.id.btn_load_more_retry)

        fun bind(loadState: LoadState) {
            when (loadState) {
                is LoadState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    tvLoading.visibility = View.VISIBLE
                    tvError.visibility = View.GONE
                    btnRetry.visibility = View.GONE
                }
                is LoadState.Error -> {
                    progressBar.visibility = View.GONE
                    tvLoading.visibility = View.GONE
                    tvError.visibility = View.VISIBLE
                    btnRetry.visibility = View.VISIBLE
                    tvError.text = loadState.error.localizedMessage
                }
                else -> {
                    progressBar.visibility = View.GONE
                    tvLoading.visibility = View.GONE
                    tvError.visibility = View.GONE
                    btnRetry.visibility = View.GONE
                }
            }

            btnRetry.setOnClickListener { retry.invoke() }
        }
    }

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_load_more, parent, false)
        return LoadStateViewHolder(view)
    }
}