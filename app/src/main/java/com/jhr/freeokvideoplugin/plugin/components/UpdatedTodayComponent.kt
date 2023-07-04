package com.jhr.freeokvideoplugin.plugin.components

import com.jhr.freeokvideoplugin.plugin.components.Const.host
import com.jhr.freeokvideoplugin.plugin.components.Const.layoutSpanCount
import com.jhr.freeokvideoplugin.plugin.util.JsoupUtil
import com.su.mediabox.pluginapi.action.DetailAction
import com.su.mediabox.pluginapi.components.ICustomPageDataComponent
import com.su.mediabox.pluginapi.data.BaseData
import com.su.mediabox.pluginapi.data.MediaInfo1Data
import com.su.mediabox.pluginapi.util.UIUtil.dp

/**
 * FileName: UpdatedTodayComponent
 * Founder: Jiang Houren
 * Create Date: 2023/6/27 13:24
 * Profile: 今日更新
 */
class UpdatedTodayComponent : ICustomPageDataComponent {

    override val pageName: String
        get() = "今日更新"

    /**
     * bug -> 这里只有一页，所以上拉加载会添加重复数据。
     */
    override suspend fun getData(page: Int): List<BaseData>? {
        if (page != 1)
            return null
        val hostUrl = Const.host + "/label/new.html"
        val document = JsoupUtil.getDocument(hostUrl)

        val data = mutableListOf<BaseData>()

        val li = document.select(".module-main")[0].select("a")
        for (liE in li){
            val title =  liE.attr("title")
            val cover = liE.select("img").attr("data-original")
            val url = liE.attr("href")
            val episode = liE.select(".module-item-note").text()
            data.add(
                MediaInfo1Data(title, cover, host + url, episode ?: "")
                    .apply {
                        layoutConfig = BaseData.LayoutConfig(layoutSpanCount, 14.dp)

                        spanSize = layoutSpanCount / 3
                        action = DetailAction.obtain(url)
                    })
        }
        return data
    }
}