package com.meg.listshop.android

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.listshop.analytics.Analytics
import com.listshop.analytics.AppInfo
import com.listshop.analytics.ClientType
import com.listshop.bff.startSDK
import com.meg.listshop.android.models.ListshopViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

class MainApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val analytics = object : Analytics {
            override fun sendEvent(eventName: String, eventArgs: Map<String, Any>) {
                println("eventName: ${eventName}, eventArgs: ${eventArgs.keys.joinToString(",") { key -> "[$key, ${eventArgs[key]}]" }}")
            }
        }

        val appInfo = AppInfo(
            baseUrl = "http://localhost:8080/",
            name = "name",
            model = "model",
            os = "os",
            osVersion = "osVersion",
            clientType = ClientType.IOS,
            clientVersion = "clientVersion",
            buildNumber = "buildNumber",
            deviceId = "deviceId"
        )

        val sdkHandle = startSDK(analytics, this, appInfo)
        val koinApplication = startKoin {
            modules(
                module {
                    single<Context> { this@MainApp }
                    viewModel { ListshopViewModel() }
                    single<SharedPreferences> {
                        get<Context>().getSharedPreferences("KAMPSTARTER_SETTINGS", MODE_PRIVATE)
                    }
                    single {
                        { Log.i("Startup", "Hello from Android/Kotlin!") }
                    }
                }
            )
        }

    }
}
