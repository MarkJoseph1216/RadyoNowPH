package com.radyopilipinomediagroup.radyo_now.ui

import androidx.recyclerview.widget.RecyclerView
import com.radyopilipinomediagroup.radyo_now.model.ads.AdsModel

interface AbstractInterface {

    interface DataHandler<T> {
        fun passData(data: T)
        fun getBackStacked()
    }

    interface BottomSheetHandler{
        fun onDone()
        fun onCreatePlaylist()
        fun playlistRecycler(recycler : RecyclerView)
    }

    interface PlaylistHandler{
        fun updatePlaylistData()
    }

    interface PlaylistSettings{
        fun onRenamePlaylist(playlistId: Int, oldName: String)
        fun onDeletePlaylist(playlistId: Int)
    }

    interface PlaylistContentSettings{
        fun onRemoveContent(contentId: Int)
    }

    interface OrientationHandler{
        fun videoLandscape()
        fun videoPortrait()
    }

    interface AdsInterface{
        fun onPopupReady(data: AdsModel.Data)
        fun onSliderReady(data: AdsModel.Data)
        fun onBannerReady(data: AdsModel.Data)
    }
}