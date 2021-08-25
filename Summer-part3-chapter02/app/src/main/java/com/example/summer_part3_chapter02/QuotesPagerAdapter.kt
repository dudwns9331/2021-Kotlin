package com.example.summer_part3_chapter02

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * 받아오는 파라미터 : quotes, isNameRevealed
 * Adapter Type : RecyclerView.Adapter<QuotesPagerAdapter.QuoteViewHolder>
 */
class QuotesPagerAdapter(
    // quotes : 명언들이 담긴 리스트, 리스트의 데이터 타입은  Quote(data class) 이다.
    private val quotes: List<Quote>,
    // isNameRevealed : 명언을 말한 사람의 이름을 가릴것인지 결정하는 함수
    private val isNameRevealed: Boolean
) : RecyclerView.Adapter<QuotesPagerAdapter.QuoteViewHolder>() {

    /**
     * onCreateViewHolder() : RecyclerView 는 ViewHolder 를 새로 만들어야 할 때마다 이 메서드를 호출한다.
     * 이 메서드는 ViewHolder 와 그에 연결된 View 를 생성하고 초기화하지만 뷰의 콘텐츠를 채우지는 않는다.
     * ViewHolder 아직 특정데이터에 바인딩된 상태가 아니기 때문이다.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuoteViewHolder =
        QuoteViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_quote, parent, false)
        )

    /**
     * onBindViewHolder() : RecyclerView 는 ViewHolder 를 데이터와 연결할 때 이 메서드를 호출한다.
     * 이 메서드는 적절한데이터를 가져와서 그 데이터를 사용하여 뷰 홀더의 레이아웃을 채운다. 예를 들어
     * RecyclerView 가 이름 목록을 표시하는 경우 메서드는 목록에서 적절한 이름을 찾아 뷰 홀더의 TextView 위젯을 채울 수 있다.
     *
     * 여기서는 holder 를 받아 bind 함수에서 목록을 표시하는 메서드를 연결시켰다.
     * quotes(명언이 담긴 리스트) 에 actualPosition(인덱스) 를 넣어서 bind 함수를 호출하였다.
     */
    override fun onBindViewHolder(holder: QuoteViewHolder, position: Int) {
        // 실제 위치는 현재위치에서 quotes 리스트의 size 만큼을 나눈 나머지를 넣어준다.
        val actualPosition = position % quotes.size
        holder.bind(quotes[actualPosition], isNameRevealed)
    }

    /**
     * getItemCount() : RecyclerView 는 데이터 세트 크기를 가져올 때 이 메서드를 호출한다.
     * 예를 들어 주소록 앱에서는 총 주소 개수가 여기에 해당할 수 있다.
     * RecyclerView 는 이 메서드를 사용하여, 항목을 추가로 표시할 수 없는 상황을 확인한다.
     */
    override fun getItemCount() = Int.MAX_VALUE         // 무한으로 넘어가는 것을 사용자가 느끼도록 충분히 큰 수


    /**
     *
     * ViewHolder 는 목록에 있는 개별 항목의 레이아웃을 포함하는 View 의 래퍼이다.
     * Adapter 는 필요에 따라 ViewHolder 객체를 만들고 이러한 뷰에 데이터를 설정하기도 한다.
     * 뷰를 데이터에 연결하는 프로세스를 바인딩이라고 한다.
     *
     * 커스텀 ViewHolder
     */
    class QuoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        // 명언을 보여주는 TextView 바인딩
        private val quoteTextView: TextView = itemView.findViewById(R.id.quoteTextView)

        // 이름을 보여주는 TextView 바인딩
        private val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)


        /**
         *  TextView 에 Firebase remoteConfig 를 통해서 가져온 Quote 값을 넣어준다.
         *  isNameRevealed 에 따라서 nameTextView 를 보여줄지 숨길지 결정한다.
         */
        @SuppressLint("SetTextI18n")
        fun bind(quote: Quote, isNameRevealed: Boolean) {
            quoteTextView.text = "\"${quote.quote}\""

            if (isNameRevealed) {
                nameTextView.text = "- ${quote.name}"
                nameTextView.visibility = View.VISIBLE
            } else {
                nameTextView.visibility = View.GONE
            }
        }

    }
}