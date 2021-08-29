package com.example.summer_part3_chapter04.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.summer_part3_chapter04.databinding.ItemBookBinding
import com.example.summer_part3_chapter04.model.Book

/**
 *  책에 대한 내용을 담는 List 에 대한 어댑터
 *  itemClickedListener 를 받아서 아이템을 클릭했을 때의 이벤트 처리를 한다.
 */
class BookAdapter(private val itemClickedListener: (Book) -> Unit) :
    ListAdapter<Book, BookAdapter.BookItemViewHolder>(diffUtil) {

    inner class BookItemViewHolder(private val binding: ItemBookBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(bookModel: Book) {
            // 책 제목과 설명을 보여준다.
            binding.titleTextView.text = bookModel.title
            binding.descriptionTextView.text = bookModel.description

            // 아이템을 눌렀을 떄, itemClickedListener 에서 처리하도록 함
            binding.root.setOnClickListener {
                itemClickedListener(bookModel)
            }

            // Glide 를 통해서 리스트에 이미지가 표시되도록 한다.
            // 리스트는 이미지가 좌측, 제목과 설명이 우측에 정렬되어 있다.
            Glide
                .with(binding.coverImageView.context)
                .load(bookModel.coverSmallUrl)
                .into(binding.coverImageView)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookItemViewHolder {
        return BookItemViewHolder(
            ItemBookBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ),
        )
    }

    override fun onBindViewHolder(holder: BookItemViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<Book>() {
            override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}