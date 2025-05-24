package com.listshop.bff

import app.cash.sqldelight.db.SqlDriver
import com.listshop.analytics.AnalyticsHandle
import com.listshop.analytics.AppAnalytics
import com.listshop.analytics.AppInfo
import com.listshop.analytics.HttpClientAnalytics
import com.listshop.analytics.ListShopAnalytics
import com.listshop.bff.remote.ListShopRemoteApi
import com.listshop.bff.remote.ShoppingListApi
import com.listshop.bff.remote.TagApi
import com.listshop.bff.remote.UserApi
import com.listshop.bff.remote.impl.ListShopRemoteApiImpl
import com.listshop.bff.remote.impl.ShoppingListApiImpl
import com.listshop.bff.remote.impl.TagApiImpl
import com.listshop.bff.remote.impl.UserApiImpl
import com.listshop.bff.repositories.ListShopDatabase
import com.listshop.bff.repositories.SessionInfoRepository
import com.listshop.bff.repositories.TagRepository
import com.listshop.bff.repositories.impl.SessionInfoRepositoryImpl
import com.listshop.bff.services.ListService
import com.listshop.bff.services.UserService
import com.listshop.bff.services.UserSessionService
import com.listshop.bff.services.impl.ListServiceImpl
import com.listshop.bff.services.impl.UserServiceImpl
import com.listshop.bff.services.impl.UserSessionServiceImpl
import com.listshop.bff.ucp.DashboardUCP
import com.listshop.bff.ucp.OnboardingUCP
import com.listshop.bff.ucp.TagUCP
import com.russhwolf.settings.Settings
import io.ktor.client.engine.HttpClientEngine

internal const val SETTINGS_KEY = "KMMBridgeKickStartSettings"
internal const val DB_NAME = "ListshopDb"

internal abstract class BaseServiceLocator(private val analyticsHandle: AnalyticsHandle,
                                           private val appInfo: AppInfo
) :
    ServiceLocator {

    protected abstract val sqlDriver: SqlDriver
    protected abstract val clientEngine: HttpClientEngine
    protected abstract val settings: Settings

    override val tagUCP: TagUCP by lazy {
        TagUCP(
            dataRepo = tagRepository,
            tagApi = tagApi,
            listShopAnalytics = listShopAnalytics
        )
    }

    override val onboardingUCP: OnboardingUCP by lazy {
        OnboardingUCP(
            sessionService = sessionService,
            listService = listService,  // remote repo
            userService = userService,
            listShopAnalytics = listShopAnalytics
        )
    }

    override val dashboardUCP: DashboardUCP by lazy {
        DashboardUCP(
            sessionService = sessionService,
            userService = userService,
            listShopAnalytics = listShopAnalytics
        )
    }

    override val sessionService: UserSessionService by lazy {
        UserSessionServiceImpl(
            sessionRepo = sessionInfoRepository,
            appInfo = appInfo
        )
    }

    override val appAnalytics: AppAnalytics
        get() = analyticsHandle.appAnalytics


    override val listShopAnalytics: ListShopAnalytics
        get() = analyticsHandle.listShopAnalytics


    override val httpClientAnalytics: HttpClientAnalytics
        get() = analyticsHandle.httpClientAnalytics


    private val listService: ListService by lazy {
        ListServiceImpl(
            remoteApi = shoppingListApi,
            sessionService = sessionService
        )
    }

    private val userService: UserService by lazy {
        UserServiceImpl(
            remoteApi = userApi,
            sessionService = sessionService,
            listShopAnalytics = listShopAnalytics
        )
    }

    private val tagRepository: TagRepository by lazy {
        TagRepository(
            listShopDatabase = listShopDatabase
        )
    }

    private val sessionInfoRepository: SessionInfoRepository by lazy {
        SessionInfoRepositoryImpl(
            listShopDatabase = listShopDatabase
        )
    }


    protected val listShopDatabase: ListShopDatabase by lazy {
        ListShopDatabase(
            sqlDriver = sqlDriver,
            analytics = listShopAnalytics
        )
    }

    private val listShopRemoteApi: ListShopRemoteApi by lazy {
        ListShopRemoteApiImpl(
            engine = clientEngine,
            sessionService = sessionService,
            appInfo = appInfo,
            httpClientAnalytics = httpClientAnalytics,
            listShopAnalytics = listShopAnalytics
        )
    }

    private val tagApi: TagApi by lazy {
        TagApiImpl(
            remoteApi = listShopRemoteApi
        )
    }

    private val userApi: UserApi by lazy {
        UserApiImpl(
            remoteApi = listShopRemoteApi
        )
    }

    private val shoppingListApi: ShoppingListApi by lazy {
        ShoppingListApiImpl(
            remoteApi = listShopRemoteApi,
            listShopAnalytics = listShopAnalytics
        )
    }


}
