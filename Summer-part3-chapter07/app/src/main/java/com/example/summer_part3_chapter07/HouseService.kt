package com.example.summer_part3_chapter07

import retrofit2.Call
import retrofit2.http.GET

/**
 * retrofit2 를 사용하기 위한 인터페이스
 */
interface HouseService {
    /**
     * HTTP GET 을 통해서 해당 주소에 있는 JSON 데이터를 가져오는 함수
     * getHouseList 는 GET 처리를 통해서 HouseDto 를 Call 해서 가져온다.
     * HouseDto 는 val items: List<HouseModel> 값을 가진다.
     */
    @GET("/v3/e88aa115-d2cf-45ea-80a3-feffdcded4cb")
    fun getHouseList(): Call<HouseDto>

}