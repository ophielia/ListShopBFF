import com.android.build.api.dsl.androidLibrary

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.sqlDelight)
    //alias(libs.plugins.android.library)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.mokkery)
    alias(libs.plugins.kotlinx.kover)
    `maven-publish`
}

kotlin {
    jvmToolchain(21)

    androidLibrary {
        namespace = "com.listshop.bff"
        compileSdk = libs.versions.compileSdk.get().toInt()
        compilations.getByName("main")
        // Opt-in to enable and configure host-side (unit) tests
        withHostTest {
            isIncludeAndroidResources = true
        }
        minSdk = libs.versions.minSdk.get().toInt()
    }
    targets.configureEach {
        if (this.name == "android") {
            (this as? org.jetbrains.kotlin.gradle.plugin.mpp.KotlinAndroidTarget)?.publishLibraryVariants("release", "debug")
        }
    }
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
                implementation(libs.mokkery.test.coroutines)
                implementation("com.goncalossilva:resources:0.4.0")

        }
        androidMain.dependencies {
            implementation(libs.sqlDelight.android)
            implementation(libs.ktor.client.okHttp)
        }

        getByName("androidHostTest") {
            resources.srcDir("src/commonTest/resources")
            dependencies {
                implementation(kotlin("test-junit"))
                implementation(libs.junit)
                implementation(libs.sqldelight.jdbc.driver)
                implementation(libs.sqldelight.sqlite.driver)
                implementation(libs.mock.server)
                implementation(libs.google.gson)
            }
        }

        iosMain.dependencies {
            implementation(libs.touchlab.stately.common)
            implementation(libs.sqlDelight.native)
            implementation(libs.ktor.client.ios)
        }
    }
    task("testClasses")
}

// Disable tests on ARM64 hosts that are incompatible
tasks.configureEach {
    if (name == "iosSimulatorArm64Test") {
        enabled = true
        // Set a default device for the simulator tests if not already set
        if ((this as? org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeSimulatorTest)?.device?.orNull == null) {
            (this as? org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeSimulatorTest)?.device?.set("iPhone 16")
        }
    }
}

// Copy resources for native tests so they can be found by goncalossilva:resources
tasks.withType<org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeTest>().configureEach {
    val testTask = this
    if (testTask is org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeSimulatorTest) {
        testTask.doFirst {
            project.copy {
                from("src/commonTest/resources")
                into(testTask.executable.parentFile)
            }
        }
    }
}

// For publishing Android AAR files to GitHub Packages
addGithubPackagesRepository()

sqldelight {
    databases.create("ListshopDb") {
        packageName.set("com.listshop.bff.db")
    }
}
