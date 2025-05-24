package com.listshop.bff

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.listshop.analytics.AnalyticsHandle
import com.listshop.analytics.AppInfo
import com.listshop.bff.client.MySettingsImpl
import com.listshop.bff.db.ListshopDb
import com.listshop.bff.services.UserSessionService
import com.listshop.bff.ucp.DashboardUCP
import com.listshop.bff.ucp.OnboardingUCP
import com.listshop.bff.ucp.TagUCP
import com.russhwolf.settings.Settings
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp

internal fun tagUCPStartup(analyticsHandle: AnalyticsHandle, appInfo: AppInfo): TagUCP {
    val locator = TestServiceLocator( analyticsHandle, appInfo)
    return locator.tagUCP
}

internal fun onboardingUCPStartup(analyticsHandle: AnalyticsHandle, appInfo: AppInfo): OnboardingUCP {
    val locator = TestServiceLocator( analyticsHandle, appInfo)
    return locator.onboardingUCP
}

internal fun dashboardUCPStartup(
    analyticsHandle: AnalyticsHandle,
    appInfo: AppInfo
): DashboardUCP {
    val locator = TestServiceLocator( analyticsHandle, appInfo)
    return locator.dashboardUCP
}
internal fun sessionServiceStartup(analyticsHandle: AnalyticsHandle, appInfo: AppInfo): UserSessionService {
    val locator = TestServiceLocator( analyticsHandle, appInfo)
    return locator.sessionService
}
internal class TestServiceLocator(
    analyticsHandle: AnalyticsHandle,
    appInfo: AppInfo
) : BaseServiceLocator(analyticsHandle, appInfo) {
    override public val sqlDriver: SqlDriver by lazy {
        JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
            .also { ListshopDb.Schema.create(it) }
    }

    override val settings: Settings by lazy {
        MySettingsImpl()
    }

    override val clientEngine: HttpClientEngine by lazy { OkHttp.create() }

    val testDBHelper: TestDatabaseHelper by lazy {
        TestDatabaseHelper(
            listShopDatabase = listShopDatabase
        )
    }
}
