import com.android.build.api.dsl.androidLibrary

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    //alias(libs.plugins.android.library)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    `maven-publish`
}

kotlin {
    androidLibrary {
        namespace = "com.listshop.bff.anayltics"
        compileSdk = libs.versions.compileSdk.get().toInt()
        compilations.getByName("main")
    }
    targets.configureEach {
        if (this.name == "android") {
            (this as? org.jetbrains.kotlin.gradle.plugin.mpp.KotlinAndroidTarget)?.publishLibraryVariants("release", "debug")
        }
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.touchlab.stately.concurrency)
        }
    }
}

// For publishing Android AAR files to GitHub Packages
addGithubPackagesRepository()


