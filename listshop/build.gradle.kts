plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.sqlDelight)
    alias(libs.plugins.android.library)
    id("dev.mokkery")
    `maven-publish`
}

kotlin {
    androidTarget {
        publishAllLibraryVariants()
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(project(":analytics"))
            implementation(libs.coroutines.core)
            implementation(libs.bundles.ktor.common)
            implementation(libs.multiplatformSettings)
            implementation(libs.kotlinx.dateTime)
            implementation(libs.touchlab.kermit)
            implementation(libs.sqlDelight.coroutinesExt)
        }
        commonTest.dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation(libs.coroutines.test)
                implementation(libs.kotest.framework.engine)
                implementation(libs.kotest.assertions.core)
                implementation("com.goncalossilva:resources:0.4.0")
        }
        androidMain.dependencies {
            implementation(libs.sqlDelight.android)
            implementation(libs.ktor.client.okHttp)
        }

        androidUnitTest.dependencies {
            implementation(kotlin("test-junit"))
            implementation(libs.junit)
            implementation(libs.sqldelight.jdbc.driver)
            implementation(libs.sqldelight.sqlite.driver)
            implementation(libs.mock.server)
            implementation(libs.google.gson)
        }
        iosMain.dependencies {
            implementation(libs.touchlab.stately.common)
            implementation(libs.sqlDelight.native)
            implementation(libs.ktor.client.ios)
        }
    }
    task("testClasses")
}

android {
    compileSdk = libs.versions.compileSdk.get().toInt()

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }
    namespace = "com.listshop.bff"
}

// For publishing Android AAR files to GitHub Packages
addGithubPackagesRepository()

sqldelight {
    databases.create("ListshopDb") {
        packageName.set("com.listshop.bff.db")
    }
}
