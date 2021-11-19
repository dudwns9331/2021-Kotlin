package com.example.summer_part4_chapter01.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.summer_part4_chapter01.R
import com.example.summer_part4_chapter01.model.VideoModel

/**
 * 비디오를 보여주는 리스트의 데이터를 제어하는 어댑터
 */
class VideoAdapter(val callback: (String, String) -> Unit) :
    ListAdapter<VideoModel, VideoAdapter.ViewHolder>(diffUtil) {
    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        /**
         * item 은 retrofit 을 VideoModel
         */
        fun bind(item: VideoModel) {
            // 제목 TextView
            val titleTextView = view.findViewById<TextView>(R.id.titleTextView)
            // 부제목 TextView
            val subTitleTextView = view.findViewById<TextView>(R.id.subTitleTextView)
            // 썸네일 보여주는 ImageView
            val tumbnailImageView = view.findViewById<ImageView>(R.id.thumbnailImageView)

            titleTextView.text = item.title
            subTitleTextView.text = item.subtitle

            // 썸네일 이미지 가져오기
            Glide.with(tumbnailImageView.context)
                .load(item.thumb)
                .into(tumbnailImageView)

            // 영상을 눌렀을 때
            view.setOnClickListener {
                // 눌렀을 떄 callback
                callback(item.sources, item.title)
            }

        }
    }

    // onCreateViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_video, parent, false)
        )
    }

    // onBindViewHolder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        return holder.bind(currentList[position])
    }

    /**
     * diffUtil - 아이템 구분
     */
    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<VideoModel>() {
            override fun areItemsTheSame(oldItem: VideoModel, newItem: VideoModel): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: VideoModel, newItem: VideoModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}