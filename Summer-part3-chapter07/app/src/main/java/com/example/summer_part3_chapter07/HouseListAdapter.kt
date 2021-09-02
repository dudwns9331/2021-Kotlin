package com.example.summer_part3_chapter07

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

/**
 * 네이버 맵을 보여주는 MainActivity 에서 보여지는 ViewPager 의 데이터를 처리하는 어댑터
 * itemClicked 를 통해서 Item 이 클릭 당했을 때의 이벤트 처리를 받아온다.
 * differ 유틸을 사용한다.
 */
class HouseListAdapter :
    ListAdapter<HouseModel, HouseListAdapter.ItemViewHolder>(differ) {

    /**
     * ViewHolder RecyclerView : 뷰홀더 내부 클래스로 작성
     */
    inner class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        /**
         * titleTextView, priceTextView, thumbnailImageView 에 대한 바인딩을 한다.
         * 받아온 houseModel 에서 title 과 price 를 가져온다.
         * view 가 클릭당했을 때, itemClicked(houseModel) 을 통해서 해당 데이터를 넘겨준다.
         * Glide 를 통해서 이미지를 업로드 한다.
         */
        fun bind(houseModel: HouseModel) {
            val titleTextView = view.findViewById<TextView>(R.id.titleTextView)
            val priceTextView = view.findViewById<TextView>(R.id.priceTextView)
            val thumbnailImageView = view.findViewById<ImageView>(R.id.thumbnailImageView)

            titleTextView.text = houseModel.title
            priceTextView.text = houseModel.price

            // 썸네일 이미지 업로드
            Glide.with(thumbnailImageView.context)
                .load(houseModel.imgUrl)
                .transform(CenterCrop(), RoundedCorners(dpToPx(thumbnailImageView.context, 12)))
                .into(thumbnailImageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(
            inflater.inflate(
                R.layout.item_house,
                parent,
                false
            )
        )
    }

    /**
     * 뷰 홀더가 생성되었을 때,
     */
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    /**
     * 안드로이드 xml 에서 사용하는 dp 를 px 단위로 변환한다.
     */
    private fun dpToPx(context: Context, dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            context.resources.displayMetrics
        ).toInt()
    }

    /**
     * 아이템에 대한 처리, DiffUtil 을 통해서
     * 이전의 아이템이 새로운 아이템과 일치하는지 검사한다.
     */
    companion object {
        val differ = object : DiffUtil.ItemCallback<HouseModel>() {
            override fun areItemsTheSame(oldItem: HouseModel, newItem: HouseModel): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: HouseModel, newItem: HouseModel): Boolean {
                return oldItem == newItem
            }

        }
    }
}