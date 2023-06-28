package com.jhr.freeokvideoplugin.plugin.util

import android.util.Log
import com.jhr.freeokvideoplugin.plugin.components.Const.host
import com.jhr.freeokvideoplugin.plugin.components.Const.layoutSpanCount
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import com.su.mediabox.pluginapi.action.ClassifyAction
import com.su.mediabox.pluginapi.action.DetailAction
import com.su.mediabox.pluginapi.data.*
import com.su.mediabox.pluginapi.util.UIUtil.dp
import java.net.URL

object ParseHtmlUtil {

    fun getCoverUrl(cover: String, imageReferer: String): String {
        return when {
            cover.startsWith("//") -> {
                try {
                    "${URL(imageReferer).protocol}:$cover"
                } catch (e: Exception) {
                    e.printStackTrace()
                    cover
                }
            }
            cover.startsWith("/") -> {
                //url不全的情况
                host + cover
            }
            else -> cover
        }
    }

    /**
     * 解析搜索的元素
     *
     * @param element ul的父元素
     */
    fun parseSearchEm(
        element: Element,
        imageReferer: String
    ): List<BaseData> {
        val videoInfoItemDataList = mutableListOf<BaseData>()

        val lpic: Elements = element
            .select("div[class='module-main module-page']")
            .select("div[class='module-items module-card-items']")

        val results: Elements = lpic.select("div[class='module-card-item module-item']")
        for (i in results.indices) {
            var cover = results[i].select("img").attr("data-original").getImageUrl()
            if (imageReferer.isNotBlank())
                cover = getCoverUrl(cover, imageReferer)
            val title = results[i].select(".module-card-item-title").text()
            val url = results[i].select(".module-card-item-poster").attr("href")
            val episode = results[i].select(".module-card-item-class").text()+ " [" +results[i].select(".module-item-note").text()+"]"

            val tags = mutableListOf<TagData>()
            val tag = results[i].select(".module-info-item-content")[0].text().replace(" ","").split("/")
            for (type in tag)
                tags.add(TagData(type))
            val describe = results[i].select(".module-info-item-content")[1].text()
            val item = MediaInfo2Data(
                title, cover, host + url,
                episode, describe, tags
            ).apply {
                    action = DetailAction.obtain(url)
                }
            videoInfoItemDataList.add(item)
        }
        return videoInfoItemDataList
    }
    /**
     * 解析分类下的元素
     *
     * @param element ul的父元素
     */
    fun parseClassifyEm(
        element: Element,
        imageReferer: String
    ): List<BaseData> {
        val videoInfoItemDataList = mutableListOf<BaseData>()
        val results: Elements = element.select("div[class='module-items module-poster-items-base']").select("a")
        Log.e("TAG","分类下列表长度 ${results.size}")  // 40
        for (i in results.indices) {
            var cover = results[i].select("img").attr("data-original").getImageUrl()
            if (imageReferer.isNotBlank())
                cover = getCoverUrl(cover, imageReferer)
            val title = results[i].select(".module-poster-item-title").text()
            val url = results[i].attr("href")
            val episode = results[i].select(".module-item-note").text()

//            val tags = mutableListOf<TagData>()
//            val describe = ""
            val item = MediaInfo1Data(title, cover, host + url, episode ?: "")
                .apply {
                    layoutConfig = BaseData.LayoutConfig(layoutSpanCount, 14.dp)

                    spanSize = layoutSpanCount / 3
                    action = DetailAction.obtain(url)
                }
//            val item = MediaInfo2Data(
//                title, cover, host + url,
//                episode, describe, tags
//            ).apply {
//                action = DetailAction.obtain(url)
//            }
            videoInfoItemDataList.add(item)
        }
        return videoInfoItemDataList
    }
    /**
     * 解析分类元素
     */
    fun parseClassifyEm(element: Element): List<ClassifyItemData> {
        val classifyItemDataList = mutableListOf<ClassifyItemData>()
        var classifyCategory = ""
        //分类类别
        classifyCategory = element.select(".module-class-item").select(".module-item-title").text()
        //分类项
        element.select(".module-item-box").select("a").forEach {
            classifyItemDataList.add(ClassifyItemData().apply {
                action = ClassifyAction.obtain(
                    it.attr("href").apply {
                        Log.d("分类链接", this)
                    },
                    classifyCategory,
                    it.text()
                )
            })
        }
        return classifyItemDataList
    }

    fun String.getImageUrl() = if (startsWith("http")) this else "https:$this"
}