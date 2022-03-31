package com.radyopilipinomediagroup.radyo_now.repositories

import android.util.Log
import com.google.gson.Gson
import com.radyopilipinomediagroup.radyo_now.model.*
import com.radyopilipinomediagroup.radyo_now.model.ads.AdsModel
import com.radyopilipinomediagroup.radyo_now.model.comments.CommentsResponse
import com.radyopilipinomediagroup.radyo_now.model.faq.FAQModel
import com.radyopilipinomediagroup.radyo_now.model.google.GoogleAccessTokenResult
import com.radyopilipinomediagroup.radyo_now.model.notification.NotificationResponseModel
import com.radyopilipinomediagroup.radyo_now.model.playlist.PlaylistContentsResultModel
import com.radyopilipinomediagroup.radyo_now.model.playlist.PlaylistFeaturedContentsRM
import com.radyopilipinomediagroup.radyo_now.model.playlist.PlaylistListResultModel
import com.radyopilipinomediagroup.radyo_now.model.profile.ProfileDetailsModel
import com.radyopilipinomediagroup.radyo_now.model.profile.ProfileDetailsResponse
import com.radyopilipinomediagroup.radyo_now.model.programs.FeaturedProgramsResultModel
import com.radyopilipinomediagroup.radyo_now.model.programs.ProgramDetailsModel
import com.radyopilipinomediagroup.radyo_now.model.programs.ProgramsModel
import com.radyopilipinomediagroup.radyo_now.model.security.*
import com.radyopilipinomediagroup.radyo_now.model.stations.*
import com.radyopilipinomediagroup.radyo_now.utils.Constants
import okhttp3.FormBody
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.io.Reader


class Repositories {
    fun login(userLogin: LoginModel, deviceId: String, result: RetrofitService.ResultHandler<LoginResultModel>){
        RetrofitService.retrofitService(RetrofitRequest::class.java).login(userLogin, deviceId)
            .enqueue(object : Callback<LoginResultModel> {
                override fun onFailure(call: Call<LoginResultModel>, t: Throwable) {
                    result.onFailed(t.localizedMessage!!)
                }
                override fun onResponse(
                    call: Call<LoginResultModel>,
                    response: Response<LoginResultModel>
                ) {
                    when {
                        response.code() == 200 -> {
                            result.onSuccess(response.body())
                            Log.d("Login", Gson().toJson(response.body()))
                        }
                        response.code() < 500 -> result.onError(
                            parseRetrofitError(
                                response.errorBody()!!.charStream(), LoginResultModel::class.java
                            )
                        )
                    }
                }
            })
    }

    fun getSSOResult(
        deviceId: String,
        provider: String,
        token: String,
        result: RetrofitService.ResultHandler<SSOResultModel>
    ) {
        RetrofitService.retrofitService(RetrofitRequest::class.java).singleSignOn(provider, token, deviceId)
            .enqueue(object : Callback<SSOResultModel> {
                override fun onFailure(call: Call<SSOResultModel>, t: Throwable) {
                    result.onFailed(t.localizedMessage!!)
                }

                override fun onResponse(
                    call: Call<SSOResultModel>,
                    response: Response<SSOResultModel>
                ) {
                    when {
                        response.code() == 200 -> result.onSuccess(response.body())
                        response.code() < 500 -> {
                            result.onError(
                                parseRetrofitError(
                                    response.errorBody()!!.charStream(),
                                    SSOResultModel::class.java
                                )
                            )
                        }
                    }
                }
            })
    }

    fun deletePlaylist(
        id: Int,
        token: String,
        result: RetrofitService.ResultHandler<GeneralResultModel>
    ) {
        RetrofitService.retrofitService(RetrofitRequest::class.java, token).deletePlaylist(id).enqueue(
            object :
                Callback<GeneralResultModel> {
                override fun onFailure(call: Call<GeneralResultModel>, t: Throwable) {
                    result.onFailed(t.localizedMessage!!)
                }

                override fun onResponse(
                    call: Call<GeneralResultModel>,
                    response: Response<GeneralResultModel>
                ) {
                    when {
                        response.code() == 200 -> result.onSuccess(response.body())
                        response.code() < 500 -> result.onError(
                            parseRetrofitError(
                                response.errorBody()!!.charStream(), GeneralResultModel::class.java
                            )
                        )
                        else -> result.onFailed(
                            retrofitOtherStatusCode(
                                "deletePlaylist_Error",
                                response
                            )
                        )
                    }
                }
            })
    }

    fun register(
        register: RegistrationModel,
        result: RetrofitService.ResultHandler<RegistrationResultModel>
    ) {
        RetrofitService.retrofitService(RetrofitRequest::class.java).signUp(register).enqueue(object :
            Callback<RegistrationResultModel> {
            override fun onFailure(call: Call<RegistrationResultModel>, t: Throwable) {
                result.onFailed(t.localizedMessage!!)
            }

            override fun onResponse(
                call: Call<RegistrationResultModel>,
                response: Response<RegistrationResultModel>
            ) {
                when {
                    response.code() == 200 -> result.onSuccess(response.body())
                    response.code() < 500 -> result.onError(
                        parseRetrofitError(
                            response.errorBody()!!.charStream(), RegistrationResultModel::class.java
                        )
                    )
                }
            }
        })
    }

    fun forgotPassword(email: String, result: RetrofitService.ResultHandler<ForgotPassResultModel>){
        RetrofitService.retrofitService(RetrofitRequest::class.java).forgotPassword(email).enqueue(
            object :
                Callback<ForgotPassResultModel> {
                override fun onFailure(call: Call<ForgotPassResultModel>, t: Throwable) {
                    result.onFailed(t.localizedMessage!!)
                }

                override fun onResponse(
                    call: Call<ForgotPassResultModel>,
                    response: Response<ForgotPassResultModel>
                ) {
                    when {
                        response.code() == 200 -> result.onSuccess(response.body())
                        response.code() < 500 -> result.onError(
                            parseRetrofitError(
                                response.errorBody()!!.charStream(),
                                ForgotPassResultModel::class.java
                            )
                        )
                    }
                }
            })
    }

    fun changePassword(
        email: String,
        password: String,
        new_password: String,
        new_password_confirmation: String,
        token: String,
        result: RetrofitService.ResultHandler<ChangePasswordResponse>
    ){
        RetrofitService.retrofitService(RetrofitRequest::class.java, token).changePassword(
            email,
            password,
            new_password,
            new_password_confirmation
        ).enqueue(
            object : Callback<ChangePasswordResponse> {
                override fun onFailure(call: Call<ChangePasswordResponse>, t: Throwable) {
                    result.onFailed(t.localizedMessage!!)
                }

                override fun onResponse(
                    call: Call<ChangePasswordResponse>,
                    response: Response<ChangePasswordResponse>
                ) {
                    try {
                        when {
                            response.code() == 200 -> result.onSuccess(response.body())
                            response.code() < 500 -> result.onError(
                                parseRetrofitError(
                                    response.errorBody()!!.charStream(),
                                    ChangePasswordResponse::class.java
                                )
                            )
                        }
                    } catch (e: Exception) {
                        print(e.message)
                    }
                }
            })
    }

    fun getStationsList(
        page: Int,
        token: String,
        result: RetrofitService.ResultHandler<StationListResultModel>
    ){
        RetrofitService.retrofitService(RetrofitRequest::class.java, token).stationList(page).enqueue(
            object : Callback<StationListResultModel> {
                override fun onResponse(
                    call: Call<StationListResultModel>,
                    response: Response<StationListResultModel>
                ) {
                    when {
                        response.code() == 200 -> result.onSuccess(response.body())
                        response.code() < 500 -> result.onError(
                            parseRetrofitError(
                                response.errorBody()!!.charStream(),
                                StationListResultModel::class.java
                            )
                        )
                        response.code() == 500 -> result.onFailed("Can't connect to server.")
                    }
                }

                override fun onFailure(call: Call<StationListResultModel>, t: Throwable) {
                    result.onFailed(t.localizedMessage!!)
                }
            })
    }

    fun getFeaturedLiveContentList(
        page: Int,
        token: String,
        result: RetrofitService.ResultHandler<SeeAllStationResponse>
    ){
        RetrofitService.retrofitService(RetrofitRequest::class.java, token).featuredStationsList(
            page
        ).enqueue(
            object : Callback<SeeAllStationResponse> {
                override fun onResponse(
                    call: Call<SeeAllStationResponse>,
                    response: Response<SeeAllStationResponse>
                ) {
                    try {
                        when {
                            response.code() == 200 -> result.onSuccess(response.body())
                            response.code() < 500 -> result.onError(
                                parseRetrofitError(
                                    response.errorBody()!!.charStream(),
                                    SeeAllStationResponse::class.java
                                )
                            )
                            response.code() == 500 -> result.onFailed("Can't connect to server.")
                        }
                    } catch (e: Exception) {
                        print(e.message)
                    }
                }

                override fun onFailure(call: Call<SeeAllStationResponse>, t: Throwable) {
                    result.onFailed(t.localizedMessage!!)
                }
            })
    }

    fun getFeaturedStation(
        token: String,
        result: RetrofitService.ResultHandler<FeaturedStationsResultModel>
    ){
        RetrofitService.retrofitService(RetrofitRequest::class.java, token).featuredStationContent().enqueue(
            object : Callback<FeaturedStationsResultModel> {
                override fun onResponse(
                    call: Call<FeaturedStationsResultModel>,
                    response: Response<FeaturedStationsResultModel>
                ) {
                    when {
                        response.code() == 200 -> result.onSuccess(response.body())
                        response.code() < 500 -> result.onError(
                            parseRetrofitError(
                                response.errorBody()!!.charStream(),
                                FeaturedStationsResultModel::class.java
                            )
                        )
                        response.code() == 500 -> result.onFailed("Can't connect to server.")
                    }
                }

                override fun onFailure(call: Call<FeaturedStationsResultModel>, t: Throwable) {
                    result.onFailed(t.localizedMessage!!)
                }
            })
    }

    fun getFeaturedPrograms(
        token: String,
        result: RetrofitService.ResultHandler<FeaturedProgramsResultModel>
    ){
        RetrofitService.retrofitService(RetrofitRequest::class.java, token).featuredProgramContent().enqueue(
            object : Callback<FeaturedProgramsResultModel> {
                override fun onResponse(
                    call: Call<FeaturedProgramsResultModel>,
                    response: Response<FeaturedProgramsResultModel>
                ) {
                    when {
                        response.code() == 200 -> result.onSuccess(response.body())
                        response.code() < 500 -> result.onError(
                            parseRetrofitError(
                                response.errorBody()!!.charStream(),
                                FeaturedProgramsResultModel::class.java
                            )
                        )
                        response.code() == 500 -> result.onFailed("Can't connect to server.")
                    }
                }

                override fun onFailure(call: Call<FeaturedProgramsResultModel>, t: Throwable) {
                    result.onFailed(t.localizedMessage!!)
                }
            })
    }

    fun getStationDetails(
        id: Int?,
        token: String,
        result: RetrofitService.ResultHandler<StationDetailsResultModel>
    ){
        RetrofitService.retrofitService(RetrofitRequest::class.java, token).stationDetails(id).enqueue(
            object : Callback<StationDetailsResultModel> {
                override fun onResponse(
                    call: Call<StationDetailsResultModel>,
                    response: Response<StationDetailsResultModel>
                ) {
                    when {
                        response.code() == 200 -> result.onSuccess(response.body())
                        response.code() < 500 -> result.onError(
                            parseRetrofitError(
                                response.errorBody()!!.charStream(),
                                StationDetailsResultModel::class.java
                            )
                        )
                    }
                }

                override fun onFailure(call: Call<StationDetailsResultModel>, t: Throwable) {
                    result.onFailed(t.localizedMessage!!)
                }
            })
    }

    fun getProgramsList(
        token: String,
        result: RetrofitService.ResultHandler<ProgramsModel>
    ){
        RetrofitService.retrofitService(RetrofitRequest::class.java, token).programLists().enqueue(
            object : Callback<ProgramsModel> {
                override fun onResponse(
                    call: Call<ProgramsModel>,
                    response: Response<ProgramsModel>
                ) {
                    when {
                        response.code() == 200 -> result.onSuccess(response.body())
                        response.code() < 500 -> result.onError(
                            parseRetrofitError(
                                response.errorBody()!!.charStream(),
                                ProgramsModel::class.java
                            )
                        )
                    }
                }

                override fun onFailure(call: Call<ProgramsModel>, t: Throwable) {
                    result.onFailed(t.localizedMessage!!)
                }
            })
    }

    fun searchProgramList(
        token: String,
        searchKey: String,
        result: RetrofitService.ResultHandler<ProgramsModel>
    ){
        RetrofitService.retrofitService(RetrofitRequest::class.java, token).programSearch(searchKey).enqueue(
            object : Callback<ProgramsModel> {
                override fun onResponse(
                    call: Call<ProgramsModel>,
                    response: Response<ProgramsModel>
                ) {
                    when {
                        response.code() == 200 -> result.onSuccess(response.body())
                        response.code() < 500 -> result.onError(
                            parseRetrofitError(
                                response.errorBody()!!.charStream(),
                                ProgramsModel::class.java
                            )
                        )
                    }
                }

                override fun onFailure(call: Call<ProgramsModel>, t: Throwable) {
                    result.onFailed(t.localizedMessage!!)
                }
            })
    }

    fun getPlaylistList(
        page: Int,
        perPage: Int,
        token: String,
        result: RetrofitService.ResultHandler<PlaylistListResultModel>
    ){
        RetrofitService.retrofitService(RetrofitRequest::class.java, token).playlistList(
            page,
            perPage
        ).enqueue(object : Callback<PlaylistListResultModel> {
            override fun onResponse(
                call: Call<PlaylistListResultModel>,
                response: Response<PlaylistListResultModel>
            ) {
                when {
                    response.code() == 200 -> result.onSuccess(response.body())
                    response.code() < 500 -> result.onError(
                        parseRetrofitError(
                            response.errorBody()!!.charStream(), PlaylistListResultModel::class.java
                        )
                    )
                }
            }

            override fun onFailure(call: Call<PlaylistListResultModel>, t: Throwable) {
                result.onFailed(t.localizedMessage!!)
            }
        })
    }

    fun getProgramDetails(
        programId: String,
        token: String,
        result: RetrofitService.ResultHandler<ProgramDetailsModel>
    ){
        RetrofitService.retrofitService(RetrofitRequest::class.java, token).programDetails(programId.toInt()).enqueue(
            object : Callback<ProgramDetailsModel> {
                override fun onResponse(
                    call: Call<ProgramDetailsModel>,
                    response: Response<ProgramDetailsModel>
                ) {
                    when {
                        response.code() == 200 -> result.onSuccess(response.body())
                        response.code() < 500 -> result.onError(
                            parseRetrofitError(
                                response.errorBody()!!.charStream(),
                                ProgramDetailsModel::class.java
                            )
                        )
                    }
                }

                override fun onFailure(call: Call<ProgramDetailsModel>, t: Throwable) {
                    result.onFailed(t.localizedMessage!!)
                }
            })
    }

    fun getProgramContents(
        programId: String,
        token: String,
        result: RetrofitService.ResultHandler<StationContentsResultModel>){

        RetrofitService.retrofitService(RetrofitRequest::class.java, token).programContents(
            programId.toInt()
        ).enqueue(
            object : Callback<StationContentsResultModel> {
                override fun onResponse(
                    call: Call<StationContentsResultModel>,
                    response: Response<StationContentsResultModel>
                ) {
                    when {
                        response.code() == 200 -> result.onSuccess(response.body())
                        response.code() < 500 -> result.onError(
                            parseRetrofitError(
                                response.errorBody()!!.charStream(),
                                StationContentsResultModel::class.java
                            )
                        )
                    }
                }

                override fun onFailure(call: Call<StationContentsResultModel>, t: Throwable) {
                    result.onFailed(t.localizedMessage!!)
                }
            })
    }

    fun getProgramContents(
        programId: String,
        page: Int,
        token: String,
        result: RetrofitService.ResultHandler<StationContentsResultModel>){

        RetrofitService.retrofitService(RetrofitRequest::class.java, token).programContents(
            programId.toInt(),
            page,
            15
        ).enqueue(
            object : Callback<StationContentsResultModel> {
                override fun onResponse(
                    call: Call<StationContentsResultModel>,
                    response: Response<StationContentsResultModel>
                ) {
                    when {
                        response.code() == 200 -> result.onSuccess(response.body())
                        response.code() < 500 -> result.onError(
                            parseRetrofitError(
                                response.errorBody()!!.charStream(),
                                StationContentsResultModel::class.java
                            )
                        )
                    }
                }

                override fun onFailure(call: Call<StationContentsResultModel>, t: Throwable) {
                    result.onFailed(t.localizedMessage!!)
                }
            })
    }

    fun getProgramContents(
        programId: String,
        page: Int,
        perPage: Int,
        token: String,
        result: RetrofitService.ResultHandler<StationContentsResultModel>){

        RetrofitService.retrofitService(RetrofitRequest::class.java, token).programContents(
            programId.toInt(),
            page,
            perPage
        ).enqueue(
            object : Callback<StationContentsResultModel> {
                override fun onResponse(
                    call: Call<StationContentsResultModel>,
                    response: Response<StationContentsResultModel>
                ) {
                    when {
                        response.code() == 200 -> result.onSuccess(response.body())
                        response.code() < 500 -> result.onError(
                            parseRetrofitError(
                                response.errorBody()!!.charStream(),
                                StationContentsResultModel::class.java
                            )
                        )
                    }
                }

                override fun onFailure(call: Call<StationContentsResultModel>, t: Throwable) {
                    result.onFailed(t.localizedMessage!!)
                }
            })
    }

    fun getPlaylistList(
        token: String,
        result: RetrofitService.ResultHandler<PlaylistListResultModel>
    ){
        getPlaylistList(1, 15, token, result)
    }

    fun createPlaylist(
        name: String,
        token: String,
        result: RetrofitService.ResultHandler<GeneralResultModel>
    ){
        RetrofitService.retrofitService(RetrofitRequest::class.java, token).createPlaylist(name).enqueue(
            object : Callback<GeneralResultModel> {
                override fun onResponse(
                    call: Call<GeneralResultModel>,
                    response: Response<GeneralResultModel>
                ) {
                    when {
                        response.code() == 200 -> result.onSuccess(response.body())
                        response.code() < 500 -> result.onError(
                            parseRetrofitError(
                                response.errorBody()!!.charStream(), GeneralResultModel::class.java
                            )
                        )
                    }
                }

                override fun onFailure(call: Call<GeneralResultModel>, t: Throwable) {
                    result.onFailed(t.localizedMessage!!)
                }
            })
    }

    fun addContentToPlaylist(
        playlistId: Int,
        contentId: Int,
        token: String,
        result: RetrofitService.ResultHandler<GeneralResultModel>
    ){
        RetrofitService.retrofitService(RetrofitRequest::class.java, token).addPlaylistContent(
            playlistId,
            contentId
        ).enqueue(object : Callback<GeneralResultModel> {
            override fun onResponse(
                call: Call<GeneralResultModel>,
                response: Response<GeneralResultModel>
            ) {
                when {
                    response.code() == 200 -> result.onSuccess(response.body())
                    response.code() < 500 -> result.onError(
                        parseRetrofitError(
                            response.errorBody()!!.charStream(), GeneralResultModel::class.java
                        )
                    )
                    else -> result.onFailed(
                        retrofitOtherStatusCode(
                            "AddToPlaylist_Error",
                            response
                        )
                    )
                }
            }

            override fun onFailure(call: Call<GeneralResultModel>, t: Throwable) {
                result.onFailed(t.localizedMessage!!)
            }
        })
    }


    fun playlistContents(
        playlistId: Int,
        sort: String,
        sortOrder: String,
        token: String,
        result: RetrofitService.ResultHandler<PlaylistContentsResultModel>
    ){
        RetrofitService.retrofitService(RetrofitRequest::class.java, token).playlistContents(
            playlistId,
            sort,
            sortOrder
        ).enqueue(object : Callback<PlaylistContentsResultModel> {
            override fun onResponse(
                call: Call<PlaylistContentsResultModel>,
                response: Response<PlaylistContentsResultModel>
            ) {
                when {
                    response.code() == 200 -> result.onSuccess(response.body())
                    response.code() < 500 -> result.onError(
                        parseRetrofitError(
                            response.errorBody()!!.charStream(),
                            PlaylistContentsResultModel::class.java
                        )
                    )
                    else -> result.onFailed(
                        retrofitOtherStatusCode(
                            "playlistContents_Error",
                            response
                        )
                    )
                }
            }

            override fun onFailure(call: Call<PlaylistContentsResultModel>, t: Throwable) {
                result.onFailed(t.localizedMessage!!)
            }
        })
    }

    fun playlistContents(
        playlistId: Int,
        sort: String,
        token: String,
        result: RetrofitService.ResultHandler<PlaylistContentsResultModel>
    ){
        RetrofitService.retrofitService(RetrofitRequest::class.java, token).playlistContents(
            playlistId,
            sort
        ).enqueue(object : Callback<PlaylistContentsResultModel> {
            override fun onResponse(
                call: Call<PlaylistContentsResultModel>,
                response: Response<PlaylistContentsResultModel>
            ) {
                when {
                    response.code() == 200 -> result.onSuccess(response.body())
                    response.code() < 500 -> result.onError(
                        parseRetrofitError(
                            response.errorBody()!!.charStream(),
                            PlaylistContentsResultModel::class.java
                        )
                    )
                    else -> result.onFailed(
                        retrofitOtherStatusCode(
                            "playlistContents_Error",
                            response
                        )
                    )
                }
            }

            override fun onFailure(call: Call<PlaylistContentsResultModel>, t: Throwable) {
                result.onFailed(t.localizedMessage!!)
            }
        })
    }
    fun playlistContents(
        playlistId: Int,
        token: String,
        result: RetrofitService.ResultHandler<PlaylistContentsResultModel>
    ){
        RetrofitService.retrofitService(RetrofitRequest::class.java, token).playlistContents(
            playlistId
        ).enqueue(object : Callback<PlaylistContentsResultModel> {
            override fun onResponse(
                call: Call<PlaylistContentsResultModel>,
                response: Response<PlaylistContentsResultModel>
            ) {
                when {
                    response.code() == 200 -> result.onSuccess(response.body())
                    response.code() < 500 -> result.onError(
                        parseRetrofitError(
                            response.errorBody()!!.charStream(),
                            PlaylistContentsResultModel::class.java
                        )
                    )
                    else -> result.onFailed(
                        retrofitOtherStatusCode(
                            "playlistContents_Error",
                            response
                        )
                    )
                }
            }

            override fun onFailure(call: Call<PlaylistContentsResultModel>, t: Throwable) {
                result.onFailed(t.localizedMessage!!)
            }
        })
    }

    fun removePlaylistContent(
        playlistId: Int,
        contentId: Int,
        token: String,
        result: RetrofitService.ResultHandler<GeneralResultModel>
    ){
        RetrofitService.retrofitService(RetrofitRequest::class.java, token).removePlaylistContent(
            playlistId,
            contentId
        ).enqueue(object : Callback<GeneralResultModel> {
            override fun onResponse(
                call: Call<GeneralResultModel>,
                response: Response<GeneralResultModel>
            ) {
                when {
                    response.code() == 200 -> result.onSuccess(response.body())
                    response.code() < 500 -> result.onError(
                        parseRetrofitError(
                            response.errorBody()!!.charStream(), GeneralResultModel::class.java
                        )
                    )
                    else -> result.onFailed(
                        retrofitOtherStatusCode(
                            "removePlaylistContent_Error",
                            response
                        )
                    )
                }
            }

            override fun onFailure(call: Call<GeneralResultModel>, t: Throwable) {
                result.onFailed(t.localizedMessage!!)
            }
        })
    }

    fun renamePlaylist(
        playlistId: Int,
        name: String,
        token: String,
        result: RetrofitService.ResultHandler<GeneralResultModel>
    ) {
        RetrofitService.retrofitService(RetrofitRequest::class.java, token).updatePlaylist(
            name,
            playlistId
        ).enqueue(object : Callback<GeneralResultModel> {
            override fun onResponse(
                call: Call<GeneralResultModel>,
                response: Response<GeneralResultModel>
            ) {
                when {
                    response.code() == 200 -> result.onSuccess(response.body())
                    response.code() < 500 -> result.onError(
                        parseRetrofitError(
                            response.errorBody()!!.charStream(), GeneralResultModel::class.java
                        )
                    )
                    else -> result.onFailed(
                        retrofitOtherStatusCode(
                            "renamePlaylist_ERROR",
                            response
                        )
                    )
                }
            }

            override fun onFailure(call: Call<GeneralResultModel>, t: Throwable) {
                result.onFailed(t.localizedMessage!!)
            }
        })
    }

    fun stationContents(
        stationId: Int,
        token: String,
        result: RetrofitService.ResultHandler<StationContentsResultModel>
    ){
        RetrofitService.retrofitService(RetrofitRequest::class.java, token).stationContents(
            stationId
        ).enqueue(object : Callback<StationContentsResultModel> {
            override fun onResponse(
                call: Call<StationContentsResultModel>,
                response: Response<StationContentsResultModel>
            ) {
                when {
                    response.code() == 200 -> result.onSuccess(response.body())
                    response.code() < 500 -> result.onError(
                        parseRetrofitError(
                            response.errorBody()!!.charStream(),
                            StationContentsResultModel::class.java
                        )
                    )
                }
            }

            override fun onFailure(call: Call<StationContentsResultModel>, t: Throwable) {
                result.onFailed(t.localizedMessage!!)
            }
        })
    }

    fun stationContents(
        stationId: Int,
        filter: Int,
        format: String,
        token: String,
        result: RetrofitService.ResultHandler<StationContentsResultModel>
    ){
        RetrofitService.retrofitService(RetrofitRequest::class.java, token).stationContents(
            stationId,
            format,
            filter
        ).enqueue(object : Callback<StationContentsResultModel> {
            override fun onResponse(
                call: Call<StationContentsResultModel>,
                response: Response<StationContentsResultModel>
            ) {
                when {
                    response.code() == 200 -> result.onSuccess(response.body())
                    response.code() < 500 -> result.onError(
                        parseRetrofitError(
                            response.errorBody()!!.charStream(),
                            StationContentsResultModel::class.java
                        )
                    )
                }
            }

            override fun onFailure(call: Call<StationContentsResultModel>, t: Throwable) {
                result.onFailed(t.localizedMessage!!)
            }
        })
    }

    fun playlistFeaturedContent(
        token: String,
        result: RetrofitService.ResultHandler<PlaylistFeaturedContentsRM>
    ){
        RetrofitService.retrofitService(RetrofitRequest::class.java, token).playlistFeaturedContent().enqueue(
            object : Callback<PlaylistFeaturedContentsRM> {
                override fun onResponse(
                    call: Call<PlaylistFeaturedContentsRM>,
                    response: Response<PlaylistFeaturedContentsRM>
                ) {
                    when {
                        response.code() == 200 -> result.onSuccess(response.body())
                        response.code() <= 500 -> result.onError(
                            parseRetrofitError(
                                response.errorBody()!!.charStream(),
                                PlaylistFeaturedContentsRM::class.java
                            )
                        )
                        response.code() == 500 -> result.onFailed("Can't display playlist.")
                    }
                }

                override fun onFailure(call: Call<PlaylistFeaturedContentsRM>, t: Throwable) {
                    result.onFailed(t.localizedMessage!!)
                }
            })
    }

    fun featuredStation(token: String, result: RetrofitService.ResultHandler<FeaturedResultModel>){
        RetrofitService.retrofitService(RetrofitRequest::class.java, token).featuredStation().enqueue(
            object : Callback<FeaturedResultModel> {
                override fun onResponse(
                    call: Call<FeaturedResultModel>,
                    response: Response<FeaturedResultModel>
                ) {
                    when {
                        response.code() == 200 -> result.onSuccess(response.body())
                        response.code() <= 500 -> result.onError(
                            parseRetrofitError(
                                response.errorBody()!!.charStream(), FeaturedResultModel::class.java
                            )
                        )
                    }
                }

                override fun onFailure(call: Call<FeaturedResultModel>, t: Throwable) {
                    result.onFailed(t.localizedMessage!!)
                }
            })
    }

    fun getContentDetails(
        token: String, contentId: String,
        result: RetrofitService.ResultHandler<ContentDetailsResponse>
    ){
        try {
            RetrofitService.retrofitService(RetrofitRequest::class.java, token)
            .contentDetails(contentId.toInt()).enqueue(
                    object : Callback<ContentDetailsResponse> {
                        override fun onResponse(
                            call: Call<ContentDetailsResponse>,
                            response: Response<ContentDetailsResponse>
                        ) {
                            try {
                                when {
                                    response.code() == 200 -> result.onSuccess(response.body())
                                    response.code() <= 500 -> result.onError(
                                        parseRetrofitError(
                                            response.errorBody()!!.charStream(),
                                            ContentDetailsResponse::class.java
                                        )
                                    )
                                }
                            } catch (e: Exception) {
                                print(e.message)
                            }
                        }

                        override fun onFailure(call: Call<ContentDetailsResponse>, t: Throwable) {
                            result.onFailed(t.localizedMessage!!)
                        }
                    })
        } catch (e: Exception) {
            print(e.message)
        }
    }

    fun getProfileDetails(
        token: String,
        result: RetrofitService.ResultHandler<ProfileDetailsResponse>
    ){
        RetrofitService.retrofitService(RetrofitRequest::class.java, token).getProfleDetails().enqueue(
            object : Callback<ProfileDetailsResponse> {
                override fun onResponse(
                    call: Call<ProfileDetailsResponse>,
                    response: Response<ProfileDetailsResponse>
                ) {
                    when {
                        response.code() == 200 -> result.onSuccess(response.body())
                        response.code() <= 500 -> result.onError(
                            parseRetrofitError(
                                response.errorBody()!!.charStream(),
                                ProfileDetailsResponse::class.java
                            )
                        )
                    }
                }

                override fun onFailure(call: Call<ProfileDetailsResponse>, t: Throwable) {
                    result.onFailed(t.localizedMessage!!)
                }
            })
    }

    fun updateProfileDetails(
        token: String,
        profileDetails: ProfileDetailsModel,
        screen: String,
        result: RetrofitService.ResultHandler<ProfileDetailsResponse>
    ){
        RetrofitService.retrofitService(RetrofitRequest::class.java, token)
            .updateProfileDetails(
                profileDetails.firstName!!,
                profileDetails.lastName!!,
                profileDetails.dateBirth!!,
                profileDetails.gender!!,
                profileDetails.city!!,
                profileDetails.region!!,
                screen
            ).enqueue(
                object : Callback<ProfileDetailsResponse> {
                    override fun onResponse(
                        call: Call<ProfileDetailsResponse>,
                        response: Response<ProfileDetailsResponse>
                    ) {
                        when {
                            response.code() == 200 -> result.onSuccess(response.body())
                            response.code() < 500 -> result.onError(
                                parseRetrofitError(
                                    response.errorBody()!!.charStream(),
                                    ProfileDetailsResponse::class.java
                                )
                            )
                            else -> result.onFailed(
                                retrofitOtherStatusCode(
                                    "updateProfileDetails_Error",
                                    response
                                )
                            )
                        }
                    }

                    override fun onFailure(call: Call<ProfileDetailsResponse>, t: Throwable) {
                        result.onFailed(t.localizedMessage!!)
                    }
                })
    }

    fun updateDateOfBirth(
        token: String,
        profileDetails: ProfileDetailsModel,
        screen: String,
        result: RetrofitService.ResultHandler<ProfileDetailsResponse>
    ){
        RetrofitService.retrofitService(RetrofitRequest::class.java, token)
            .addDateOfBirth(
                profileDetails.firstName!!,
                profileDetails.lastName!!,
                profileDetails.dateBirth!!,
                screen
            ).enqueue(
                object : Callback<ProfileDetailsResponse> {
                    override fun onResponse(
                        call: Call<ProfileDetailsResponse>,
                        response: Response<ProfileDetailsResponse>
                    ) {
                        when {
                            response.code() == 200 -> result.onSuccess(response.body())
                            response.code() <= 500 -> result.onError(
                                parseRetrofitError(
                                    response.errorBody()!!.charStream(),
                                    ProfileDetailsResponse::class.java
                                )
                            )
                        }
                    }

                    override fun onFailure(call: Call<ProfileDetailsResponse>, t: Throwable) {
                        result.onFailed(t.localizedMessage!!)
                    }
                })
    }

    fun uploadProfilePhoto(
        token: String,
        data: MultipartBody.Part, result: RetrofitService.ResultHandler<ProfileDetailsResponse>){

        RetrofitService.retrofitService(RetrofitRequest::class.java, token)
            .uploadProfilePhoto(data).enqueue(
                object : Callback<ProfileDetailsResponse> {
                    override fun onResponse(
                        call: Call<ProfileDetailsResponse>,
                        response: Response<ProfileDetailsResponse>
                    ) {
                        when {
                            response.code() == 200 -> result.onSuccess(response.body())
                            response.code() <= 500 -> result.onError(
                                parseRetrofitError(
                                    response.errorBody()!!.charStream(),
                                    ProfileDetailsResponse::class.java
                                )
                            )
                        }
                    }

                    override fun onFailure(call: Call<ProfileDetailsResponse>, t: Throwable) {
                        result.onFailed(t.localizedMessage!!)
                    }
                })
    }

    fun requestAccessToken(
        serverAuthCode: String?,
        result: RetrofitService.ResultHandler<GoogleAccessTokenResult>
    ) {
        val client = OkHttpClient()
        val requestBody: RequestBody = FormBody.Builder()
            .add("grant_type", "authorization_code")
            .add(
                "client_id",
                Constants.GOOGLE_CLIENT_ID
            )
            .add("client_secret", Constants.GOOGLE_SECRET)
            .add("redirect_uri", "")
            .add("code", serverAuthCode)
            .build()
        val request: Request = Request.Builder()
            .url("https://www.googleapis.com/oauth2/v4/token")
            .post(requestBody)
            .build()
        client.newCall(request).enqueue(object : Callback<Any?>, okhttp3.Callback {
            override fun onFailure(call: Call<Any?>, t: Throwable) {
                result.onFailed(t.localizedMessage!!)
            }

            override fun onResponse(call: Call<Any?>, response: Response<Any?>) {
                println(Gson().toJson(response))
            }

            override fun onFailure(call: okhttp3.Call, e: IOException) {
                result.onFailed(e.toString())
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                try {
                    val jsonObject = Gson().fromJson(
                        response?.body()?.string(),
                        GoogleAccessTokenResult::class.java
                    )
                    result.onSuccess(jsonObject)
                } catch (e: Exception) {
                    e.message
                }
            }
        })
    }

    fun getFavorites(): List<FavoritesModel> {
        val favorites: MutableList<FavoritesModel> = mutableListOf()
        favorites += FavoritesModel("", "Title One", "Subtitle One", "7.99")
        favorites += FavoritesModel("", "Title Two", "Subtitle Two", "7.98")
        favorites += FavoritesModel("", "Title Three", "Subtitle Three", "7.97")
        favorites += FavoritesModel("", "Title Four", "Subtitle Four", "7.96")
        favorites += FavoritesModel("", "Title Five", "Subtitle Five", "7.95")
        return favorites
    }

    fun getVideoStreaming(): List<FeaturedStreamModel> {
        val streams: MutableList<FeaturedStreamModel> = mutableListOf()
        streams += FeaturedStreamModel(
            "RTV Tarlac Channel 26",
            "https://i.ytimg.com/vi/J5wk8JPJHno/hqdefault.jpg",
            "J5wk8JPJHno",
            "WATCH LIVE : RTV Tarlac 26 DEPED - ARAL TARLAK HENYO | MARCH 11, 2021 8:30 AM - 5:30 PM",
            "March 11, 2021 Aral TarlakHenyo discusses the required lessons of Grades 4 and 5 in the elementary and Grades 8 and 9 in the Junior High School levels.",
            "7.99"
        )
        streams += FeaturedStreamModel(
            "RTV Tarlac Channel 27",
            "https://i.ytimg.com/vi/KElt1IhFIYg/hqdefault.jpg",
            "KElt1IhFIYg ",
            "WATCH LIVE : RTV Tarlac 26 DEPED - ARAL TARLAK HENYO | MARCH 11, 2021 8:30 AM - 5:30 PM",
            "March 11, 2021 Aral TarlakHenyo discusses the required lessons of Grades 4 and 5 in the elementary and Grades 8 and 9 in the Junior High School levels.",
            "7.98"
        )
        streams += FeaturedStreamModel(
            "RTV Tarlac Channel 28",
            "https://i.ytimg.com/vi/1sxHCPNfza4/hqdefault.jpg",
            "1sxHCPNfza4",
            "WATCH LIVE : RTV Tarlac 26 DEPED - ARAL TARLAK HENYO | MARCH 10, 2021 8:30 AM - 5:30 PM",
            "March 10, 2021 Aral TarlakHenyo discusses the required lessons of Grades 4 and 5 in the elementary and Grades 8 and 9 in the Junior High School levels.",
            "7.97"
        )
        streams += FeaturedStreamModel(
            "RTV Tarlac Channel 29",
            "https://i.ytimg.com/vi/Wumk_7BGqpE/hqdefault.jpg",
            "Wumk_7BGqpE",
            "WATCH LIVE : RTV Tarlac 26 DEPED - ARAL TARLAK HENYO | MARCH 10, 2021 8:30 AM - 5:30 PM",
            "March 10, 2021 Aral TarlakHenyo discusses the required lessons of Grades 4 and 5 in the elementary and Grades 8 and 9 in the Junior High School levels.",
            "7.96"
        )
        streams += FeaturedStreamModel(
            "RTV Tarlac Channel 30",
            "https://i.ytimg.com/vi/e0vm8vjTXgY/hqdefault.jpg",
            "e0vm8vjTXgY",
            "WATCH LIVE : RTV Tarlac 26 DEPED - ARAL TARLAK HENYO | MARCH 9, 2021 8:30 AM - 5:30 PM",
            "No available description.",
            "7.95"
        )
        return streams
    }

    fun getAlbum(): List<AlbumModel> {
        val albums: MutableList<AlbumModel> = mutableListOf()
        albums += AlbumModel("", "Title One", "12", "7.99")
        albums += AlbumModel("", "Title Two", "21", "7.98")
        albums += AlbumModel("", "Title Three", "13", "7.97")
        return albums
    }

    fun getFaqData(): List<FAQModel> {
        val faqs: MutableList<FAQModel> = mutableListOf()
        faqs += FAQModel("What does your app do?", "RadyoNow is the mobile streaming app of Radyo Pilipino Media Group.\n\nRadyoNow is the embodiment of digestible current content that is relevant, easy to navigate, and always accessible. It boasts feature that combine video and radio in one convenient mobile platform for all the programs and stations of Radyo Pilipino Media Group.")
        faqs += FAQModel("What type of content do you publish?", "RadyoNow streams Radyo Pilipino Media Group programs that are genuine, relevant, excellent, accessible, and thoughtful. From news and current affairs, educational, to music and entertainment, we hope these programs bring you delight and inspire positive values and mindset that can change and improve your life!")
        faqs += FAQModel("How frequent do you publish episodes?", "We publish our freshest episodes everyday! You will get notified when new contents are already available.")
        faqs += FAQModel("Do you also publish reading materials", "Yes. You can read news relevant to you, published by the Radyo Pilipino News Team. You may access these by clicking the “News” icon.")
        faqs += FAQModel("Will I be notified if there are new contents?","Yes. The app will notify you when new contents are uploaded.")
        faqs += FAQModel("How can I contact you?","Contact us via wehearyou@radyopilipino.com ")
        faqs += FAQModel("Can I turn off my notifications?","Yes. You can turn off notifications via settings menu.")
        return faqs
    }

    fun getProgramList2(): List<ProgramListModel> {
        val programList: MutableList<ProgramListModel> = mutableListOf()
        programList += ProgramListModel("", "Title One", "Desc One", "7.99", false)
        programList += ProgramListModel("", "Title Two", "Desc Two", "7.98", true)
        programList += ProgramListModel("", "Title Three", "Desc Three", "7.97", true)
        programList += ProgramListModel("", "Title Four", "Desc Four", "7.96", false)
        return programList
    }

    fun <T> parseRetrofitError(errorBody: Reader?, model: Class<T>) : T{
        val errMsg = Gson().fromJson(errorBody, ErrorCodeResponse::class.java)
        Log.d("BODY", Gson().toJson(errMsg))
        return Gson().fromJson(Gson().toJson(errMsg), model)
    }

    fun retrofitOtherStatusCode(requestName: String, response: Response<*>) : String{
        return "\n$requestName: \nStatusCode: ${response.code()} \nmessage: \n\t\"${parseRetrofitError(
            response.errorBody()!!.charStream(), GeneralResultModel::class.java
        ).message}\""
    }

    fun getTuneIn(
        stationID: Int,
        token: String,
        result: RetrofitService.ResultHandler<TuneInModelResultModel>
    ) {
        RetrofitService.retrofitService(RetrofitRequest::class.java, token).tuneIn(stationID).enqueue(
            object : Callback<TuneInModelResultModel> {
                override fun onResponse(
                    call: Call<TuneInModelResultModel>,
                    response: Response<TuneInModelResultModel>
                ) {
                    when {
                        response.code() == 200 -> result.onSuccess(response.body())
                        response.code() <= 500 -> result.onError(
                            parseRetrofitError(
                                response.errorBody()!!.charStream(),
                                TuneInModelResultModel::class.java
                            )
                        )
                    }
                }

                override fun onFailure(call: Call<TuneInModelResultModel>, t: Throwable) {
                    result.onFailed(t.localizedMessage!!)
                }
            })
    }

    fun stationPrograms(
        stationID: Int,
        token: String,
        result: RetrofitService.ResultHandler<StationProgramResultModel>
    ){
        RetrofitService.retrofitService(RetrofitRequest::class.java, token).stationPrograms(
            stationID
        ).enqueue(
            object : Callback<StationProgramResultModel> {
                override fun onResponse(
                    call: Call<StationProgramResultModel>,
                    response: Response<StationProgramResultModel>
                ) {
                    when {
                        response.code() == 200 -> result.onSuccess(response.body())
                        response.code() <= 500 -> result.onError(
                            parseRetrofitError(
                                response.errorBody()!!.charStream(),
                                StationProgramResultModel::class.java
                            )
                        )
                    }
                }

                override fun onFailure(call: Call<StationProgramResultModel>, t: Throwable) {
                    result.onFailed(t.localizedMessage!!)
                }
            })
    }

    fun getAds(
        token: String,
        section: String,
        result: RetrofitService.ResultHandler<AdsModel>
    ) {
        RetrofitService.retrofitService(RetrofitRequest::class.java, token).ads(section).enqueue(
            object : Callback<AdsModel> {
                override fun onResponse(
                    call: Call<AdsModel>,
                    response: Response<AdsModel>
                ) {
                    when {
                        response.code() == 200 -> result.onSuccess(response.body())
                        response.code() <= 500 -> result.onError(
                            parseRetrofitError(
                                response.errorBody()!!.charStream(), AdsModel::class.java
                            )
                        )
                    }
                }

                override fun onFailure(call: Call<AdsModel>, t: Throwable) {
                    result.onFailed(t.localizedMessage!!)
                }
            })
    }

    fun getAds(
        token: String,
        section: String,
        id: Int,
        result: RetrofitService.ResultHandler<AdsModel>
    ) {
        RetrofitService.retrofitService(RetrofitRequest::class.java, token).ads(section, id).enqueue(
            object : Callback<AdsModel> {
                override fun onResponse(
                    call: Call<AdsModel>,
                    response: Response<AdsModel>
                ) {
                    when {
                        response.code() == 200 -> result.onSuccess(response.body())
                        response.code() <= 500 -> result.onError(
                            parseRetrofitError(
                                response.errorBody()!!.charStream(), AdsModel::class.java
                            )
                        )
                    }
                }
                override fun onFailure(call: Call<AdsModel>, t: Throwable) {
                    result.onFailed(t.localizedMessage!!)
                }
            })
    }


    /** FAVORITES */

    fun getFavoriteList(
        token: String,
        result: RetrofitService.ResultHandler<StationContentsResultModel>){

        RetrofitService.retrofitService(RetrofitRequest::class.java, token).listFavorite().enqueue(
            object : Callback<StationContentsResultModel> {
                override fun onResponse(
                    call: Call<StationContentsResultModel>,
                    response: Response<StationContentsResultModel>
                ) {
                    when {
                        response.code() == 200 -> result.onSuccess(response.body())
                        response.code() < 500 -> result.onError(
                            parseRetrofitError(
                                response.errorBody()!!.charStream(),
                                StationContentsResultModel::class.java
                            )
                        )
                    }
                }

                override fun onFailure(call: Call<StationContentsResultModel>, t: Throwable) {
                    result.onFailed(t.localizedMessage!!)
                }
            })
    }

    fun deleteFavorite(
        contentId: String,
        token: String,
        result: RetrofitService.ResultHandler<GeneralResultModel>){

        RetrofitService.retrofitService(RetrofitRequest::class.java, token).deleteFavorite(contentId).enqueue(
            object : Callback<GeneralResultModel> {
                override fun onResponse(
                    call: Call<GeneralResultModel>,
                    response: Response<GeneralResultModel>
                ) {
                    when {
                        response.code() == 200 -> result.onSuccess(response.body())
                        response.code() < 500 -> result.onError(
                            parseRetrofitError(
                                response.errorBody()!!.charStream(),
                                GeneralResultModel::class.java
                            )
                        )
                    }
                }

                override fun onFailure(call: Call<GeneralResultModel>, t: Throwable) {
                    result.onFailed(t.localizedMessage!!)
                }
            })
    }

    fun getCommentList(
        token: String,
        contentId: String?,
        result: RetrofitService.ResultHandler<CommentsResponse>){

        RetrofitService.retrofitService(RetrofitRequest::class.java, token).getComments(contentId).enqueue(
            object : Callback<CommentsResponse> {
                override fun onResponse(
                    call: Call<CommentsResponse>,
                    response: Response<CommentsResponse>
                ) {
                    when {
                        response.code() == 200 -> result.onSuccess(response.body())
                        response.code() < 500 -> result.onError(
                            parseRetrofitError(
                                response.errorBody()!!.charStream(),
                                CommentsResponse::class.java
                            )
                        )
                    }
                }

                override fun onFailure(call: Call<CommentsResponse>, t: Throwable) {
                    result.onFailed(t.localizedMessage!!)
                }
            })
    }

    fun addToComments(
        contentId: String?,
        comment: String,
        token: String,
        result: RetrofitService.ResultHandler<GeneralResultModel>){

        RetrofitService.retrofitService(RetrofitRequest::class.java, token)
        .addToComments(contentId, comment).enqueue(object : Callback<GeneralResultModel> {
            override fun onResponse(
                call: Call<GeneralResultModel>,
                response: Response<GeneralResultModel>
            ) {
                when {
                    response.code() == 200 -> result.onSuccess(response.body())
                    response.code() < 500 -> result.onError(
                        parseRetrofitError(
                            response.errorBody()!!.charStream(),
                            GeneralResultModel::class.java))
                }
            }
            override fun onFailure(call: Call<GeneralResultModel>, t: Throwable) {
                result.onFailed(t.localizedMessage!!)
            }
        })
    }

    fun addToFavorites(
        contentId: String,
        token: String,
        result: RetrofitService.ResultHandler<GeneralResultModel>){

        RetrofitService.retrofitService(RetrofitRequest::class.java, token).addToFavorites(contentId).enqueue(
        object : Callback<GeneralResultModel> {
            override fun onResponse(
                call: Call<GeneralResultModel>,
                response: Response<GeneralResultModel>
            ) {
                when {
                    response.code() == 200 -> result.onSuccess(response.body())
                    response.code() < 500 -> result.onError(
                        parseRetrofitError(
                            response.errorBody()!!.charStream(),
                            GeneralResultModel::class.java
                        )
                    )
                }
            }

            override fun onFailure(call: Call<GeneralResultModel>, t: Throwable) {
                result.onFailed(t.localizedMessage!!)
            }
        })
    }


    fun getNotifications(
        page: Int,
        perPage: Int,
        token: String,
        result: RetrofitService.ResultHandler<NotificationResponseModel>){

        RetrofitService.retrofitService(RetrofitRequest::class.java, token).notifications(page, perPage).enqueue(
            object : Callback<NotificationResponseModel> {
                override fun onResponse(
                    call: Call<NotificationResponseModel>,
                    response: Response<NotificationResponseModel>
                ) {
                    when {
                        response.code() == 200 -> result.onSuccess(response.body())
                        response.code() < 500 -> result.onError(
                            parseRetrofitError(
                                response.errorBody()!!.charStream(),
                                NotificationResponseModel::class.java
                            )
                        )
                    }
                }

                override fun onFailure(call: Call<NotificationResponseModel>, t: Throwable) {
                    result.onFailed(t.localizedMessage!!)
                }
            })
    }

    fun guestSignIn(result: RetrofitService.ResultHandler<LoginResultModel>) {
        println("guestSignIn_repositories")
        RetrofitService.retrofitService(RetrofitRequest::class.java).guestSignIn().enqueue(
            object : Callback<LoginResultModel> {
                override fun onResponse(
                    call: Call<LoginResultModel>,
                    response: Response<LoginResultModel>
                ) {
                    when {
                        response.code() == 200 -> result.onSuccess(response.body())
                        response.code() < 500 -> result.onError(
                            parseRetrofitError(
                                response.errorBody()!!.charStream(),
                                LoginResultModel::class.java
                            )
                        )
                        else -> result.onFailed(
                            retrofitOtherStatusCode(
                                "guestSignIn_Error",
                                response
                            )
                        )
                    }
                }

                override fun onFailure(call: Call<LoginResultModel>, t: Throwable) {
                    result.onFailed(t.localizedMessage!!)
                }
            })
    }
}