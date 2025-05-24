package com.listshop.bff

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.listshop.analytics.AnalyticsHandle
import com.listshop.analytics.AppInfo
import com.listshop.bff.db.ListshopDb
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp


internal class AndroidServiceLocator(
    context: Context,
    analyticsHandle: AnalyticsHandle,
    appInfo: AppInfo
) : BaseServiceLocator(analyticsHandle, appInfo) {

    override val sqlDriver: SqlDriver by lazy {
        AndroidSqliteDriver(
            schema = ListshopDb.Schema,
            context = context,
            name = DB_NAME
        )
    }

    override val settings: Settings by lazy {
        SharedPreferencesSettings(
            delegate = context.getSharedPreferences(
                SETTINGS_KEY,
                Context.MODE_PRIVATE
            )
        )
    }

    override val clientEngine: HttpClientEngine by lazy { OkHttp.create() }
}
