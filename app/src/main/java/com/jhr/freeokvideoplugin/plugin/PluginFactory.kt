package com.jhr.freeokvideoplugin.plugin

import com.jhr.freeokvideoplugin.plugin.components.Const
import com.jhr.freeokvideoplugin.plugin.components.HomePageDataComponent
import com.jhr.freeokvideoplugin.plugin.components.LatestHotComponent
import com.jhr.freeokvideoplugin.plugin.components.MediaClassifyPageDataComponent
import com.jhr.freeokvideoplugin.plugin.components.MediaDetailPageDataComponent
import com.jhr.freeokvideoplugin.plugin.components.MediaSearchPageDataComponent
import com.jhr.freeokvideoplugin.plugin.components.MediaUpdateDataComponent
import com.jhr.freeokvideoplugin.plugin.components.NewReleasesComponent
import com.jhr.freeokvideoplugin.plugin.components.RecentHotComponent
import com.jhr.freeokvideoplugin.plugin.components.UpdatedTodayComponent
import com.jhr.freeokvideoplugin.plugin.components.VideoPlayPageDataComponent
import com.su.mediabox.pluginapi.components.*
import com.su.mediabox.pluginapi.IPluginFactory
import com.su.mediabox.pluginapi.util.PluginPreferenceIns
import com.jhr.freeokvideoplugin.plugin.danmaku.OyydsDanmaku

/**
 * 每个插件必须实现本类
 *
 * 注意包和类名都要相同，且必须提供公开的无参数构造方法
 */
class PluginFactory : IPluginFactory() {

    override val host: String = Const.host

    override fun pluginLaunch() {
        PluginPreferenceIns.initKey(OyydsDanmaku.OYYDS_DANMAKU_ENABLE, defaultValue = true)
    }

    override fun <T : IBasePageDataComponent> createComponent(clazz: Class<T>) = when (clazz) {
        IHomePageDataComponent::class.java -> HomePageDataComponent()  // 主页
        IMediaSearchPageDataComponent::class.java -> MediaSearchPageDataComponent()  // 搜索
        IMediaDetailPageDataComponent::class.java -> MediaDetailPageDataComponent()  // 详情
        IMediaClassifyPageDataComponent::class.java -> MediaClassifyPageDataComponent()  // 媒体分类
        IMediaUpdateDataComponent::class.java -> MediaUpdateDataComponent
        IVideoPlayPageDataComponent::class.java -> VideoPlayPageDataComponent() // 视频播放
//        //自定义页面，需要使用具体类而不是它的基类（接口）
        UpdatedTodayComponent::class.java -> UpdatedTodayComponent()  // 今日更新
        NewReleasesComponent::class.java -> NewReleasesComponent()  // 新片上线
        LatestHotComponent::class.java -> LatestHotComponent()  // 最新热门
        RecentHotComponent::class.java -> RecentHotComponent()  // 近期热门
        else -> null
    } as? T

}