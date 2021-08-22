package com.example.summer_part3_chapter07

import retrofit2.Call
import retrofit2.http.GET

interface HouseService {
    @GET("/v3/e88aa115-d2cf-45ea-80a3-feffdcded4cb")
    fun getHouseList(): Call<HouseDto>
}