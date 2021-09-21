package com.app.consultationpoint.network

import com.app.consultationpoint.patient.userProfile.model.PostalModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface APIService {

    @GET
    fun getPostalDetail(
        @Url pin_code: String
    ): Call<PostalModel>
}