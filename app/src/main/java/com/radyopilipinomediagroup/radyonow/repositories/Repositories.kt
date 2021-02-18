package com.radyopilipinomediagroup.radyonow.repositories

import android.content.Context
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
}