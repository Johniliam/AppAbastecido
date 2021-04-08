package com.example.abastecido

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface APIService {
    @GET
    suspend fun getJSONObject(@Url url:String):Response<ApiResponse>
}