package com.listshop.bff

import app.cash.sqldelight.db.SqlDriver
import com.listshop.analytics.*
import com.listshop.bff.remote.*
import com.listshop.bff.remote.impl.*
import com.listshop.bff.repositories.LayoutRepository
import com.listshop.bff.repositories.ListShopDatabase
import com.listshop.bff.repositories.SessionInfoRepository
import com.listshop.bff.repositories.impl.LayoutRepositoryImpl
import com.listshop.bff.repositories.impl.ListRepositoryImpl
import com.listshop.bff.repositories.impl.SessionInfoRepositoryImpl
import com.listshop.bff.repositories.impl.TagRepositoryImpl
import com.listshop.bff.services.*
import com.listshop.bff.services.impl.*
import com.listshop.bff.ucp.DashboardUCP
import com.listshop.bff.ucp.OnboardingUCP
import com.listshop.bff.ucp.SystemUCP
import com.listshop.bff.ucp.TagUCP
import com.russhwolf.settings.Settings
import io.ktor.client.engine.*

internal const val SETTINGS_KEY = "KMMBridgeKickStartSettings"
internal const val DB_NAME = "ListshopDb"

internal abstract class BaseServiceLocator(
    private val analyticsHandle: AnalyticsHandle,
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
            listService = listService,
            userService = userService,
            syncService = syncService,
            listShopAnalytics = listShopAnalytics,
            analyticsHandle = analyticsHandle
        )
    }

    override val systemUCP: SystemUCP by lazy {
        SystemUCP(
            sessionService = sessionService,
            listService = listService,
            dishService = dishService,
            analyticsHandle = analyticsHandle
        )
    }

    override val dashboardUCP: DashboardUCP by lazy {
        DashboardUCP(
            sessionService = sessionService,
            userService = userService,
            tagService = tagService,
            syncService = syncService,
            analyticsHandle = analyticsHandle,
            listService = listService
        )
    }

    override val sessionService: SessionService by lazy {
        SessionServiceImpl(
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
            sessionService = sessionService,
            listRepo = listRepository,
            listShopAnalytics = listShopAnalytics
        )
    }

    private val dishService: DishService by lazy {
        DishServiceImpl(
            remoteApi = dishApi,
            sessionService = sessionService,
            analyticsHandle = analyticsHandle
        )
    }

    private val userService: UserService by lazy {
        UserServiceImpl(
            remoteApi = userApi,
            sessionService = sessionService,
            analyticsHandle = analyticsHandle
        )
    }

    private val syncService: SyncService by lazy {
        SyncServiceImpl(
            sessionService = sessionService,
            userApi = userApi,
            tagService = tagService,
            listService = listService,
            layoutService = layoutService,
            appInfo = appInfo,
            listShopAnalytics = listShopAnalytics
        )
    }

    private val tagService: TagService by lazy {
        TagServiceImpl(
            tagApi = tagApi,
            tagRepo = tagRepository,
            layoutService = layoutService,
            sessionService = sessionService,
            appInfo = appInfo,
            listShopAnalytics = listShopAnalytics
        )
    }

    private val layoutService: LayoutService by lazy {
        LayoutServiceImpl(
            layoutApi = layoutApi,
            layoutRepo = layoutRepository,
            appInfo = appInfo,
            sessionService = sessionService,
            listShopAnalytics = listShopAnalytics
        )
    }

    private val layoutApi: LayoutApi by lazy {
        LayoutApiImpl(
            remoteApi = listShopRemoteApi
        )
    }
    private val dishApi: DishApi by lazy {
        DishApiImpl(
            remoteApi = listShopRemoteApi,
            analyticsHandle = analyticsHandle

        )
    }

    private val layoutRepository: LayoutRepository by lazy {
        LayoutRepositoryImpl(
            listShopDatabase = listShopDatabase
        )
    }

    private val tagRepository: TagRepositoryImpl by lazy {
        TagRepositoryImpl(
            listShopDatabase = listShopDatabase
        )
    }


    private val listRepository: ListRepositoryImpl by lazy {
        ListRepositoryImpl(
            listShopDatabase = listShopDatabase,
            sessionService = sessionService
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
