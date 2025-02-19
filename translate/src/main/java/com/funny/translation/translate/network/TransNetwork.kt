package com.funny.translation.translate.network

import com.funny.compose.ai.service.AIService
import com.funny.translation.network.ServiceCreator
import com.funny.translation.translate.network.service.AppRecommendationService
import com.funny.translation.translate.network.service.AppUpdateService
import com.funny.translation.translate.network.service.ImageTranslateService
import com.funny.translation.translate.network.service.NoticeService
import com.funny.translation.translate.network.service.PayService
import com.funny.translation.translate.network.service.PluginService
import com.funny.translation.translate.network.service.SponsorService

object TransNetwork {
    val sponsorService by lazy {
        ServiceCreator.create(SponsorService::class.java)
    }

    val appUpdateService by lazy {
        ServiceCreator.create(AppUpdateService::class.java)
    }

    val pluginService by lazy {
        ServiceCreator.create(PluginService::class.java)
    }

    val noticeService by lazy {
        ServiceCreator.create(NoticeService::class.java)
    }

    val imageTranslateService by lazy {
        ServiceCreator.create(ImageTranslateService::class.java)
    }

    val payService by lazy {
        ServiceCreator.create(PayService::class.java)
    }

    val appRecommendationService by lazy {
        ServiceCreator.create(AppRecommendationService::class.java)
    }

    val aiService by lazy {
        ServiceCreator.create(AIService::class.java)
    }
}