package com.jhr.freeokvideoplugin.plugin.components

import com.jhr.freeokvideoplugin.plugin.util.JsoupUtil
import com.su.mediabox.pluginapi.action.DetailAction
import com.su.mediabox.pluginapi.components.ICustomPageDataComponent
import com.su.mediabox.pluginapi.data.BaseData
import com.su.mediabox.pluginapi.data.MediaInfo2Data
import com.su.mediabox.pluginapi.data.TagData

/**
 * FileName: LatestHotComponent
 * Founder: Jiang Houren
 * Create Date: 2023/6/27 19:59
 * Profile: 最新热门
 */
class LatestHotComponent: ICustomPageDataComponent {
    override val pageName: String
        get() = "最新热门"

    override suspend fun getData(page: Int): List<BaseData> {
        val hostUrl = Const.host + "/label/hot.html"
        val document = JsoupUtil.getDocument(hostUrl)

        val data = mutableListOf<BaseData>()

        val li = document.select(".module-card-items")[0].select(".module-card-item")
        for (liE in li){
            val title = liE.select(".module-item-top").text() +" "+ liE.select(".module-card-item-title").text()
            val cover = liE.select("img").attr("data-original")
            val url = liE.select(".module-card-item-poster").attr("href")
            val episode = liE.select(".module-card-item-class").text()+ " [" +liE.select(".module-item-note").text()+"]"
            val describe = liE.select(".module-info-item-content")[1].text()
            val tag = liE.select(".module-info-item-content")[0].text().replace(" ","").split("/")
            val tags = mutableListOf<TagData>()
            for (type in tag)
                tags.add(TagData(type))
            data.add(
                MediaInfo2Data(
                    title, cover, Const.host + url, episode, describe, tags
                ).apply {
                    action = DetailAction.obtain(url)
                })
        }
        return data
    }
}