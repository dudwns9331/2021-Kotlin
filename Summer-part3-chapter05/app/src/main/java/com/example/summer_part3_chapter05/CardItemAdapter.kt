package com.example.summer_part3_chapter05

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

/**
 * CardView 가 들어가는 어댑터
 */
class CardItemAdapter : ListAdapter<CardItem, CardItemAdapter.ViewHolder>(diffUtil) {

    /**
     * ViewHolder RecyclerView : 뷰홀더 내부 클래스로 작성
     */
    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        /**
         * bind 함수를 통해서 nameTextView 에 cardItem 의 name 을 넣는다.
         */
        fun bind(cardItem: CardItem) {
            view.findViewById<TextView>(R.id.nameTextView).text = cardItem.name
        }
    }

    /**
     * 뷰 홀더가 생성되었을 때,
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflater 에 Layout 을 불러온다.
        val inflater = LayoutInflater.from(parent.context)
        // 뷰홀더를 리턴함. item_card.xml 파일을 inflate 한다.
        return ViewHolder(inflater.inflate(R.layout.item_card, parent, false))

    }

    /**
     * ViewHolder 를 bind 한다.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    /**
     * 아이템에 대한 처리, DiffUtil 을 통해서
     * 이전의 아이템이 새로운 아이템과 일치하는지 검사한다.
     */
    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<CardItem>() {
            override fun areItemsTheSame(oldItem: CardItem, newItem: CardItem): Boolean {
                return oldItem.userId == newItem.userId
            }

            override fun areContentsTheSame(oldItem: CardItem, newItem: CardItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}