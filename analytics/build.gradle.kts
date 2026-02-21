plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
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
            implementation(libs.touchlab.stately.concurrency)
        }
    }
}

// For publishing Android AAR files to GitHub Packages
addGithubPackagesRepository()


