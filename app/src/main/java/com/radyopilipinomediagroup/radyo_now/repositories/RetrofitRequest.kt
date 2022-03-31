package com.radyopilipinomediagroup.radyo_now.repositories

import com.radyopilipinomediagroup.radyo_now.model.ContentDetailsResponse
import com.radyopilipinomediagroup.radyo_now.model.GeneralResultModel
import com.radyopilipinomediagroup.radyo_now.model.ads.AdsModel
import com.radyopilipinomediagroup.radyo_now.model.comments.CommentsResponse
import com.radyopilipinomediagroup.radyo_now.model.notification.NotificationResponseModel
import com.radyopilipinomediagroup.radyo_now.model.playlist.PlaylistContentsResultModel
import com.radyopilipinomediagroup.radyo_now.model.playlist.PlaylistDetailsResultModel
import com.radyopilipinomediagroup.radyo_now.model.playlist.PlaylistFeaturedContentsRM
import com.radyopilipinomediagroup.radyo_now.model.playlist.PlaylistListResultModel
import com.radyopilipinomediagroup.radyo_now.model.profile.ProfileDetailsResponse
import com.radyopilipinomediagroup.radyo_now.model.programs.FeaturedProgramsResultModel
import com.radyopilipinomediagroup.radyo_now.model.programs.ProgramDetailsModel
import com.radyopilipinomediagroup.radyo_now.model.programs.ProgramsModel
import com.radyopilipinomediagroup.radyo_now.model.security.*
import com.radyopilipinomediagroup.radyo_now.model.stations.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*


interface RetrofitRequest {

    @Headers("Content-Type:application/json", "Accept:application/json")
    @POST("user/signup")
    fun signUp(@Body registration: RegistrationModel): Call<RegistrationResultModel>

    @Headers("Content-Type:application/json", "Accept:application/json")
    @POST("user/login")
    fun login(@Body userLogin: LoginModel,
              @Query("device_id") deviceId: String): Call<LoginResultModel>

    @Headers("Content-Type:application/json", "Accept:application/json")
    @POST("user/forgot-password")
    fun forgotPassword(@Query("email") email: String): Call<ForgotPassResultModel>

    @Headers("Content-Type:application/json", "Accept:application/json")
    @POST("user/change-password")
    fun changePassword(
        @Query("email") email: String,
        @Query("password") password: String,
        @Query("new_password") new_password: String,
        @Query("new_password_confirmation") new_password_confirmation: String
    ): Call<ChangePasswordResponse>

    @Headers("Content-Type:application/json", "Accept:application/json")
    @POST("user/sso")
    fun singleSignOn(
        @Query("provider") provider: String,
        @Query("access_token") token: String,
        @Query("device_id") deviceId: String
    ): Call<SSOResultModel>

    @GET("stations")
    fun stationList(
        @Query("page") page: Int,
        @Query("perPage") perPage: Int,
        @Query("filters[type]") type: String
    ): Call<StationListResultModel>

    @GET("stations")
    fun stationList(@Query("page") page: Int): Call<StationListResultModel>

    @GET("stations/featured-content")
    fun featuredStationContent(): Call<FeaturedStationsResultModel>

    @GET("stations/featured-content")
    fun featuredStationsList(@Query("perPage") perPage: Int): Call<SeeAllStationResponse>

    @GET("stations/featured")
    fun featuredStation(): Call<FeaturedResultModel>

    @GET("app-users/me")
    fun getProfleDetails(): Call<ProfileDetailsResponse>

    @PUT("app-users/me")
    fun updateProfileDetails(
        @Query("first_name") firstName: String,
        @Query("last_name") lastName: String,
        @Query("date_of_birth") dateBirth: String,
        @Query("gender") gender: String,
        @Query("city") city: String,
        @Query("region") region: String,
        @Query("screen") screen: String
    ): Call<ProfileDetailsResponse>

    @PUT("app-users/me")
    fun addDateOfBirth(
        @Query("first_name") firstName: String,
        @Query("last_name") lastName: String,
        @Query("date_of_birth") dateBirth: String,
        @Query("screen") screen: String
    ): Call<ProfileDetailsResponse>

    @Multipart
    @POST("app-users/avatar")
    fun uploadProfilePhoto(@Part avatar: MultipartBody.Part?): Call<ProfileDetailsResponse>

    @GET("contents/{contentId}")
    fun contentDetails(@Path("contentId") contentId: Int?): Call<ContentDetailsResponse>

    @GET("stations/{stationId}")
    fun stationDetails(@Path("stationId") id: Int?): Call<StationDetailsResultModel>

    @GET("programs")
    fun programLists(): Call<ProgramsModel>

    @GET("programs")
    fun programSearch(@Query("searchKey") searchKey: String): Call<ProgramsModel>

    @GET("programs/{programId}")
    fun programDetails(@Path("programId") id: Int?): Call<ProgramDetailsModel>

    @GET("programs/{programId}/contents")
    fun programContents(@Path("programId") id: Int?): Call<StationContentsResultModel>

    @GET("programs/{programId}/contents")
    fun programContents(
        @Path("programId") id: Int?,
        @Query("page") page: Int?,
        @Query("perPage") contentCount: Int?
    )
        : Call<StationContentsResultModel>

    @GET("programs/{programId}/contents")
    fun stationContents(
        @Path("programId") id: Int?,
        @Query("filters[format][]") format: String,
        @Query("excludeIds[]") excludeId: Int
    ): Call<StationContentsResultModel>

    @GET("stations/{stationId}/contents?filters[featured]=0")
    fun stationContents(@Path("stationId") id: Int?): Call<StationContentsResultModel>

    @POST("playlists")
    fun createPlaylist(@Query("name") name: String): Call<GeneralResultModel>

    @DELETE("playlists")
    fun deletePlaylist(@Query("id") pid: Int) : Call<GeneralResultModel>

    @PUT("playlists")
    fun updatePlaylist(
        @Query("name") name: String,
        @Query("playlist_id") pid: Int
    ) : Call<GeneralResultModel>

    @GET("playlists")
    fun playlistList(
        @Query("page") page: Int,
        @Query("perPage") perPage: Int
    ): Call<PlaylistListResultModel>

    @GET("playlists/{playlistId}")
    fun playlistDetails(@Path("playlistId") id: Int): Call<PlaylistDetailsResultModel>

    @GET("playlists/featured-content")
    fun playlistFeaturedContent(): Call<PlaylistFeaturedContentsRM>

    @POST("playlists/remove-content")
    fun removePlaylistContent(
        @Query("playlist_id") pid: Int,
        @Query("content_id") cid: Int
    ): Call<GeneralResultModel>

    @POST("playlists/add-content")
    fun addPlaylistContent(
        @Query("playlist_id") pid: Int,
        @Query("content_id") cid: Int
    ): Call<GeneralResultModel>

    @GET("playlists/{playlistId}/contents")
    fun playlistContents(
        @Path("playlistId") id: Int,
        @Query("sort") sort: String,
        @Query("sortOrder") sortOrder: String
    ): Call<PlaylistContentsResultModel>

    @GET("playlists/{playlistId}/contents")
    fun playlistContents(
        @Path("playlistId") id: Int,
        @Query("sort") sort: String
    ): Call<PlaylistContentsResultModel>

    @GET("playlists/{playlistId}/contents")
    fun playlistContents(@Path("playlistId") id: Int): Call<PlaylistContentsResultModel>

    @GET("stations/tune-in/{stationId}")
    fun tuneIn(@Path("stationId") stationId: Int): Call<TuneInModelResultModel>

    @GET("stations/{stationId}/programs")
    fun stationPrograms(@Path("stationId") id: Int?): Call<StationProgramResultModel>

    @GET("ads")
    fun ads(@Query("section") section: String): Call<AdsModel>

    @GET("ads")
    fun ads(
        @Query("section") section: String,
        @Query("id") id: Int
    ): Call<AdsModel>

    @GET("programs/featured-content")
    fun featuredProgramContent(): Call<FeaturedProgramsResultModel>

    /** COMMENTS */
    @POST("comments")
    fun addToComments(
        @Query("content_id") contentId: String?,
        @Query("comment") comment: String?
    ): Call<GeneralResultModel>
    @GET("comments")
    fun getComments(
        @Query("filters[content_id]") contentId: String?
    ): Call<CommentsResponse>

    /** FAVORITES*/
    @GET("app-users/favorites")
    fun listFavorite(): Call<StationContentsResultModel>
    @POST("app-users/favorites")
    fun addToFavorites(
        @Query("content_id") contentId: String
    ): Call<GeneralResultModel>

    @DELETE("app-users/favorites")
    fun deleteFavorite(
        @Query("content_id") contentId: String
    ): Call<GeneralResultModel>

    @GET("app-users/inbox")
    fun notifications(@Query("page") page: Int,
                       @Query("perPage") perPage: Int): Call<NotificationResponseModel>

    @Headers("Content-Type:application/json", "Accept:application/json")
    @POST("user/guest")
    fun guestSignIn(): Call<LoginResultModel>
}