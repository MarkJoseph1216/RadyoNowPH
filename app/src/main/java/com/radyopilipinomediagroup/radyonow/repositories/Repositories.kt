package com.radyopilipinomediagroup.radyonow.repositories

import android.content.Context
import com.radyopilipinomediagroup.radyonow.model.AlbumModel
import com.radyopilipinomediagroup.radyonow.model.FavoritesModel
import com.radyopilipinomediagroup.radyonow.model.LoginModel
import com.radyopilipinomediagroup.radyonow.model.PostModel
import java.util.ArrayList

class Repositories {

    fun getLogin() : LoginModel{
        return LoginModel("radyouser","12345")
    }

    fun getTrending() : List<PostModel>{
        val posts : MutableList<PostModel> = mutableListOf()
        posts += PostModel("", "Sample One", "1hr", "", "Sample - Song", "", "")
        posts += PostModel("", "Sample Two", "2hr", "", "Sample - Song", "", "")
        posts += PostModel("", "Sample Three", "3hr", "", "Sample - Song", "", "")
        posts += PostModel("", "Sample Four", "4hr", "", "Sample - Song", "", "")
        return posts
    }

    fun getFeatured() : List<String> {
        val featured : MutableList<String> = mutableListOf()
        featured += ""
        featured += ""
        featured += ""
        featured += ""
        return featured
    }

    fun getFavorites() : List<FavoritesModel> {
        val favorites : MutableList<FavoritesModel> = mutableListOf()
        favorites += FavoritesModel("", "Title One", "Subtitle One","7.99")
        favorites += FavoritesModel("", "Title Two", "Subtitle Two","7.98")
        favorites += FavoritesModel("", "Title Three", "Subtitle Three","7.97")
        favorites += FavoritesModel("", "Title Four", "Subtitle Four","7.96")
        favorites += FavoritesModel("", "Title Five", "Subtitle Five","7.95")
        return favorites
    }

    fun getAlbum(): List<AlbumModel> {
        val albums : MutableList<AlbumModel> = mutableListOf()
        albums += AlbumModel("", "Title One", "12","7.99")
        albums += AlbumModel("", "Title Two", "21","7.98")
        albums += AlbumModel("", "Title Three", "13","7.97")
        return albums
    }
}