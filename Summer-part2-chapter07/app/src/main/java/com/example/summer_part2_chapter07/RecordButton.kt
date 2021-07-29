package com.example.summer_part2_chapter07

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton

class RecordButton(
    context: Context, attrs: AttributeSet
) : AppCompatImageButton(context, attrs) {
    /* AppCompat 은 이전버전의 호환성을 위해서 사용한다.*/
    init {
        setBackgroundResource(R.drawable.shape_oval_button)
    }


    /* 버튼의 아이콘 상태를 업데이트 하는 함수 */
    fun updateIconWithState(state: State) {
        when (state) {
            State.BEFORE_RECORDING -> {
                setImageResource(R.drawable.ic_record)
            }
            State.ON_RECORDING -> {
                setImageResource(R.drawable.ic_stop)
            }
            State.AFTER_RECORDING -> {
                setImageResource(R.drawable.ic_play)
            }
            State.ON_PLAYING -> {
                setImageResource(R.drawable.ic_stop)
            }
        }
    }

}