@file:Suppress("UnstableApiUsage")

import java.io.FileInputStream
import java.util.Properties

plugins {
    PluginType.LIBRARY.get(this)
}

val admobDataPropertiesFile: File = rootProject.file("admobdata.properties")
val admobDataProperties = Properties().apply {
    load(FileInputStream(admobDataPropertiesFile))
}
android {
    namespace = "com.doodle.core.data"
    compileSdk = Versions.Config.compileSdk

    defaultConfig {
        minSdk = Versions.Config.minSdk

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        resValue(
            "string",
            "admob_application_id",
            admobDataProperties.getProperty("admob_application_id")
        )
        resValue(
            "string",
            "admob_banner_ad_unit_id",
            admobDataProperties.getProperty("admob_banner_ad_unit_id")
        )
        resValue(
            "string",
            "admob_app_open_ad_unit_id",
            admobDataProperties.getProperty("admob_app_open_ad_unit_id")
        )
        resValue(
            "string",
            "admob_native_ad_unit_id",
            admobDataProperties.getProperty("admob_native_ad_unit_id")
        )
        resValue(
            "string",
            "admob_rewarded_ad_unit_id",
            admobDataProperties.getProperty("admob_rewarded_ad_unit_id")
        )
    }

    buildTypes {
        release {

            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = Versions.Config.sourceCompatibility
        targetCompatibility = Versions.Config.targetCompatibility
    }
    kotlinOptions {
        jvmTarget = Versions.Config.jvmTarget
    }

    buildFeatures {
        buildConfig = true
    }
    packaging {
        resources {
            excludes += Versions.Compose.exclude
        }
    }
}

dependencies {
    implementation(project(":core:database"))
    implementation(project(":core:domain"))

    implementation("androidx.core:core-ktx:${Versions.Android.coreKtx}")
    implementation("androidx.appcompat:appcompat:${Versions.Android.appCompat}")
    implementation("com.google.android.material:material:${Versions.Android.material}")
    testImplementation("junit:junit:${Versions.Tooling.junit}")
    androidTestImplementation("androidx.test.ext:junit:${Versions.Tooling.junitKtx}")
    androidTestImplementation(
        "androidx.test.espresso:espresso-core:${Versions.Tooling.androidEspressoCore}"
    )
    coreData()

    // Play Billing
    implementation("com.android.billingclient:billing-ktx:${Versions.PlayServices.billing}")

    // Volley
    implementation("com.android.volley:volley:${Versions.Network.volley}")

    // Kotlin Immutable Collections
    libs.android.kotlinImmutableCollections.get(this)

    // Play Review
    implementation("com.google.android.play:review-ktx:${Versions.PlayServices.playReview}")

    // Paging
    implementation("androidx.paging:paging-runtime:${Versions.Common.paging}")
    implementation("androidx.paging:paging-compose:${Versions.Common.paging}")
}
