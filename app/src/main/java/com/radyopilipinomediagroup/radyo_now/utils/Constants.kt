    package com.radyopilipinomediagroup.radyo_now.utils

class Constants {
    companion object {
        const val BRANCH_LINK_ACCESS = 365
        const val ADD_FAVORITES = 366
        const val FEATURED = 367
        const val LIST_PLAYLIST = 368
        const val ADD_PLAYLIST = 369
        val FACEBOOK_PERMISSIONS = mutableListOf("email", "public_profile")
        const val GOOGLE_SIGN_IN_CODE = 120
        const val ANALYTICS_PARAM = "parameteres"
        const val APP_BASE_URL = "http://core.rnradyopilipino.com/api/v1/"
//        http://core-uat.rnradyopilipino.com:8080/api/v1/
//        http://core-qa.rnradyopilipino.com/api/v1/
//        http://core.rnradyopilipino.com/api/v1/
        const val STATIONS_BASE_URL = "https://www.googleapis.com/youtube/v3/"
        const val GOOGLE_CLIENT_ID = "285059153295-hau2jqekkeqac4vsltgqddl57549uj0o.apps.googleusercontent.com"
        const val STATIC_AUDIO_STREAM_URL = "http://122.3.31.246:89/broadwavehigh.mp3?fbclid=IwAR1wFJjBfu8O1-j8_9eOG72mj-vUZnS73ctOTOVzx_95Qyh_NiMl44Q4FMQ"
        const val STATIC_AUDIO_SECONDARY_STREAM_URL = "http://122.3.31.246:88/broadwavehigh.mp3?fbclid=IwAR04kIkxqPgn8L6NRr9u8k9y1_qdcoCN6JIkBVhWOuImNv7SS9EHw3G-tXY"
        const val GOOGLE_SECRET = "BOeQZt_Cw-sCxaK_a0VMPYu2"
        const val ACCESS_TOKEN = "AIzaSyDKzNp4TylB9cHPi-EYjSwKYo8mf9ARe6A"
        const val CHANNEL_ID = "UCSguV2QCwO2hTsjEbHNDu2w"
        const val STATIC_VIDEO_STREAM_URL = "https://www.youtube.com/watch?v=osKSde6jaaw"
        const val GOOGLE_TOKEN_BASE_URL = "https://www.googleapis.com/oauth2/v4/"
        const val expression = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*"
    }
}