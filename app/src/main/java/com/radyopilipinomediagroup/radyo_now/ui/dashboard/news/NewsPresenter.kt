package com.radyopilipinomediagroup.radyo_now.ui.dashboard.news

import com.radyopilipinomediagroup.radyo_now.ui.AbstractPresenter

class NewsPresenter(var view: NewsFragment): AbstractPresenter<NewsFragment>(view) {

    interface View: AbstractPresenter.AbstractView{

    }
}