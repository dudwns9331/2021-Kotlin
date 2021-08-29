package com.example.summer_part3_chapter04.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


/**
 * 커스텀 객체를 intent 로 넘기기 위해서는
 * Parcelable 객체로 만들어 직렬화를 시켜서 내보내준다.
 */
@Parcelize
data class Book(
    // 해당 값들은 인터파크 API 를 통해서 얻을 수 있는 JSON 값들이다.
    @SerializedName("itemId") val id: Long,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("coverSmallUrl") val coverSmallUrl: String,
) : Parcelable
