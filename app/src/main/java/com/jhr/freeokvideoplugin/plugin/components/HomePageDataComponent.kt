package com.jhr.freeokvideoplugin.plugin.components

import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import android.view.Gravity
import android.widget.ImageView
import com.jhr.freeokvideoplugin.plugin.components.Const.host
import com.jhr.freeokvideoplugin.plugin.util.JsoupUtil
import com.jhr.freeokvideoplugin.plugin.util.ParseHtmlUtil.getImageUrl
import com.su.mediabox.pluginapi.action.ClassifyAction
import com.su.mediabox.pluginapi.action.CustomPageAction
import com.su.mediabox.pluginapi.action.DetailAction
import com.su.mediabox.pluginapi.components.IHomePageDataComponent
import com.su.mediabox.pluginapi.data.*
import com.su.mediabox.pluginapi.util.UIUtil.dp
import java.lang.StringBuilder

class HomePageDataComponent : IHomePageDataComponent {

    private val layoutSpanCount = 12

    override suspend fun getData(page: Int): List<BaseData>? {
        if (page != 1)
            return null
        val url = host
        val doc = JsoupUtil.getDocument(url)
        val data = mutableListOf<BaseData>()

        //1.横幅
        doc.select("div[class='container-slide']").first()?.select(".swiper")?.first()?.apply {
            val bannerItems = mutableListOf<BannerData.BannerItemData>()
            for (em in children()) {
                Log.e("TAG", em.className())
                when (em.className()) {
                    "swiper-wrapper" -> {
                        em.select("[class=swiper-slide]").forEach { bannerItem ->
                            val nameEm = bannerItem.select(".v-title").first()
                            val ext = StringBuilder()
                            nameEm?.text()?.also {
                                ext.append(it)
                            }
                            bannerItem.select(".v-ins").select("p").first()?.text()?.also {
                                ext.append("\n").append(it)
                            }

                            val videoUrl = bannerItem.getElementsByTag("a").first()?.attr("href")
                            val bannerImage = bannerItem.getElementsByTag("a").attr("style")
                                .substringAfter("(")
                                .substringBefore(")")
                            if (bannerImage.isNotBlank()) {
                                Log.d("添加横幅项", "封面：$bannerImage 链接：$videoUrl")
                                bannerItems.add(
                                    BannerData.BannerItemData(
                                        bannerImage, nameEm?.ownText() ?: "", ext.toString()
                                    ).apply {
                                        if (!videoUrl.isNullOrBlank())
                                            action = DetailAction.obtain(videoUrl)
                                    }
                                )
                            }
                        }
                        break
                    }
                }
            }
            if (bannerItems.isNotEmpty())
                data.add(BannerData(bannerItems, 6.dp).apply {
                    layoutConfig = BaseData.LayoutConfig(layoutSpanCount, 14.dp)
                    spanSize = layoutSpanCount
                })
        }

        //2.菜单第一行
        data.add(
            MediaInfo1Data(
                "", Const.Icon.RANK, "", "电影库",
                otherColor = 0xff757575.toInt(),
                coverScaleType = ImageView.ScaleType.FIT_CENTER,
                coverHeight = 24.dp,
                gravity = Gravity.CENTER
            ).apply {
                spanSize = layoutSpanCount / 4
                action = ClassifyAction.obtain(Const.host+"/vodshow/1-----------.html", "电影库")
            })
        data.add(
            MediaInfo1Data(
                "", Const.Icon.TABLE, "", "剧集库",
                otherColor = 0xff757575.toInt(),
                coverScaleType = ImageView.ScaleType.FIT_CENTER,
                coverHeight = 24.dp,
                gravity = Gravity.CENTER
            ).apply {
                spanSize = layoutSpanCount / 4
                action = ClassifyAction.obtain(Const.host+"/vodshow/2-----------.html", "剧集库")
            })
        data.add(
            MediaInfo1Data(
                "", Const.Icon.TOPIC, "", "动漫库",
                otherColor = 0xff757575.toInt(),
                coverScaleType = ImageView.ScaleType.FIT_CENTER,
                coverHeight = 24.dp,
                gravity = Gravity.CENTER
            ).apply {
                spanSize = layoutSpanCount / 4
                action = ClassifyAction.obtain(Const.host+"/vodshow/3-----------.html", "动漫库")
            })
        data.add(
            MediaInfo1Data(
                "", Const.Icon.UPDATE, "", "综艺库",
                otherColor = 0xff757575.toInt(),
                coverScaleType = ImageView.ScaleType.FIT_CENTER,
                coverHeight = 24.dp,
                gravity = Gravity.CENTER
            ).apply {
                spanSize = layoutSpanCount / 4
                action = ClassifyAction.obtain(Const.host+"/vodshow/4-----------.html", "综艺库")
            })

        //2.菜单第二行
        data.add(
            MediaInfo1Data(
                "", Const.Icon.RANK, "", "今日更新",
                otherColor = 0xff757575.toInt(),
                coverScaleType = ImageView.ScaleType.FIT_CENTER,
                coverHeight = 24.dp,
                gravity = Gravity.CENTER
            ).apply {
                spanSize = layoutSpanCount / 4
                action = CustomPageAction.obtain(UpdatedTodayComponent::class.java)
            })
        data.add(
            MediaInfo1Data(
                "", Const.Icon.RANK, "", "新片上线",
                otherColor = 0xff757575.toInt(),
                coverScaleType = ImageView.ScaleType.FIT_CENTER,
                coverHeight = 24.dp,
                gravity = Gravity.CENTER
            ).apply {
                spanSize = layoutSpanCount / 4
                action = CustomPageAction.obtain(NewReleasesComponent::class.java)
            })
        data.add(
            MediaInfo1Data(
                "", Const.Icon.RANK, "", "最新热门",
                otherColor = 0xff757575.toInt(),
                coverScaleType = ImageView.ScaleType.FIT_CENTER,
                coverHeight = 24.dp,
                gravity = Gravity.CENTER
            ).apply {
                spanSize = layoutSpanCount / 4
                action = CustomPageAction.obtain(LatestHotComponent::class.java)
            })
        data.add(
            MediaInfo1Data(
                "", Const.Icon.RANK, "", "近期热门",
                otherColor = 0xff757575.toInt(),
                coverScaleType = ImageView.ScaleType.FIT_CENTER,
                coverHeight = 24.dp,
                gravity = Gravity.CENTER
            ).apply {
                spanSize = layoutSpanCount / 4
                action = CustomPageAction.obtain(RecentHotComponent::class.java)
            })

        //3.各类推荐
        val modules = doc.select(".content").select("div[class='module']")
        val update = mutableListOf<BaseData>()
        var hasUpdate = false
        for (em in modules){
            val moduleHeading = em.select(".module-heading")
            val type = moduleHeading.select("h2")
            val typeName = type.text()
            if (!typeName.isNullOrBlank()) {
                typeName.contains("热映").also {
                    if (!it && hasUpdate) {
                        //示例使用水平列表视图组件
                        data.add(HorizontalListData(update, 120.dp).apply {
                            spanSize = layoutSpanCount
                        })
                    }
                    hasUpdate = it
                }

                data.add(SimpleTextData(typeName).apply {
                    fontSize = 15F
                    fontStyle = Typeface.BOLD
                    fontColor = Color.BLACK
                    spanSize = layoutSpanCount
                })
            }

            val moduleMain = em.select(".module-main")
            val li = moduleMain.select("a")
            for ((index,video) in li.withIndex()){
                video.apply {
                    val name = select(".module-poster-item-title").text()
                    val videoUrl = attr("href")
                    val coverUrl = select("img").first()?.attr("data-original")?.getImageUrl()
                    val episode = select(".module-item-note").text()

                    if (!name.isNullOrBlank() && !videoUrl.isNullOrBlank() && !coverUrl.isNullOrBlank()) {
                        (if (hasUpdate) update else data).add(
                            MediaInfo1Data(name, coverUrl, videoUrl, episode ?: "")
                                .apply {
                                    spanSize = layoutSpanCount / 3
                                    action = DetailAction.obtain(videoUrl)
                                    if (hasUpdate) {
                                        paddingRight = 8.dp
                                    }
                                })
//                        Log.e("TAG", "添加视频 ($name) ($videoUrl) ($coverUrl) ($episode)")
                    }
                }
                if (index == 11) break
            }
        }
        return data
    }
}