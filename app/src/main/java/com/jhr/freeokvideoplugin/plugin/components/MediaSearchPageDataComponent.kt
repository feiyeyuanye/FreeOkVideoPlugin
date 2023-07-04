package com.jhr.freeokvideoplugin.plugin.components

import android.util.Log
import com.jhr.freeokvideoplugin.plugin.components.Const.host
import com.jhr.freeokvideoplugin.plugin.util.JsoupUtil
import com.jhr.freeokvideoplugin.plugin.util.ParseHtmlUtil
import com.su.mediabox.pluginapi.components.IMediaSearchPageDataComponent
import com.su.mediabox.pluginapi.data.BaseData

class MediaSearchPageDataComponent : IMediaSearchPageDataComponent {

    override suspend fun getSearchData(keyWord: String, page: Int): List<BaseData> {
        val searchResultList = mutableListOf<BaseData>()

        val url = "${host}/okso/${keyWord}----------${page}---.html"
        Log.e("TAG", url)

        val document = JsoupUtil.getDocument(url)
        searchResultList.addAll(ParseHtmlUtil.parseSearchEm(document, url))
        return searchResultList
    }

}