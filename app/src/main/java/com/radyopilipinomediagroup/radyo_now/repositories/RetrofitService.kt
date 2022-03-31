package com.radyopilipinomediagroup.radyo_now.repositories

import com.radyopilipinomediagroup.radyo_now.utils.Constants
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RetrofitService{
    companion object{
        fun <S> retrofitService(serviceClass: Class<S>?): S {
            val retrofit = Retrofit.Builder()
                .baseUrl(Constants.APP_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(serviceClass!!)
        }

        fun <S> retrofitService(serviceClass: Class<S>?, token: String): S {
            val retrofit = Retrofit.Builder()
                .baseUrl(Constants.APP_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(headers(token)!!)
                .build()
            return retrofit.create(serviceClass!!)
        }

        fun <S> retrofitServiceGoogle(serviceClass: Class<S>?): S {
            val retrofit = Retrofit.Builder()
                .baseUrl(Constants.GOOGLE_TOKEN_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(serviceClass!!)
        }

        private fun headers(userToken: String): OkHttpClient? {
            val httpClient = OkHttpClient.Builder()
            httpClient.addInterceptor { chain ->
                val original: Request = chain.request()
                val request: Request = original.newBuilder()
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer $userToken")
                    .method(original.method(), original.body())
                    .build()
                chain.proceed(request)
            }
            return httpClient.build()
        }

        fun <S> retrofitServiceStations(serviceClass: Class<S>?): S {
            val retrofit = Retrofit.Builder()
                .baseUrl(Constants.STATIONS_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(serviceClass)
        }
    }



    interface ResultHandler<T>{
        fun onSuccess(data: T?)
        fun onError(error: T?)
        fun onFailed(message: String)
    }
}