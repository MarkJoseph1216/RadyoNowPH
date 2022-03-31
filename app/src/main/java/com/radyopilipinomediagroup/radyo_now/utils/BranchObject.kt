package com.radyopilipinomediagroup.radyo_now.utils

import com.radyopilipinomediagroup.radyo_now.model.programs.FeaturedProgramsResultModel
import io.branch.indexing.BranchUniversalObject
import io.branch.referral.util.ContentMetadata
import io.branch.referral.util.LinkProperties


object BranchObject {

    var buo = BranchUniversalObject()
        .setCanonicalIdentifier("content/12345")
        .setTitle("Radyo Now")
        .setContentDescription("Radyo Pilipino")
        .setContentImageUrl("http://core-uat.rnradyopilipino.com:8080/images/logo-radyo.PNG")
        .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PRIVATE)
//        .setLocalIndexMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
    var linkProperties: LinkProperties? = null

    init {

    }

    fun setLinkProperties(title: String, id: String, format: String){
        linkProperties = LinkProperties()
            .setChannel("facebook")
            .setAlias(id)
            .addControlParameter("\$marketing_title", title)
            .addControlParameter("\$deeplink_path", id)
            .addControlParameter("\$content_type", format)
            .addControlParameter("\$uri_redirect_mode", "2")
            .addControlParameter("\$android_deepview", "branch_passive_default")
            .setDuration(100)
    }
}