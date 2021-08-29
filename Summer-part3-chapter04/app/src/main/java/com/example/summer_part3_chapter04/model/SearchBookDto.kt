package com.example.summer_part3_chapter04.model

import com.google.gson.annotations.SerializedName

/**
 * 해당 값은 Gson 라이브러리를 통해서 인터파크 API 에서 받아온 JSON 값을
 * title 과 item 값에 Book Dto 형태로 넣어준다.
 */
data class SearchBookDto(
    @SerializedName("title") val title: String,
    @SerializedName("item") val books: List<Book>,
)
