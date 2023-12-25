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
    compileSdk = versions.config.compileSdk

    defaultConfig {
        minSdk = versions.config.minSdk

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
        sourceCompatibility = versions.config.sourceCompatibility
        targetCompatibility = versions.config.targetCompatibility
    }
    kotlinOptions {
        jvmTarget = versions.config.jvmTarget
    }

    buildFeatures {
        buildConfig = true
    }
    packaging {
        resources {
            excludes += versions.compose.exclude
        }
    }
}

dependencies {
    implementation(project(":core:database"))
    implementation(project(":core:domain"))

    implementation("androidx.core:core-ktx:${versions.android.coreKtx}")
    implementation("androidx.appcompat:appcompat:${versions.android.appCompat}")
    implementation("com.google.android.material:material:${versions.android.material}")
    testImplementation("junit:junit:${versions.tooling.junit}")
    androidTestImplementation("androidx.test.ext:junit:${versions.tooling.androidJunit}")
    androidTestImplementation(
        "androidx.test.espresso:espresso-core:${versions.tooling.androidEspressoCore}"
    )
    coreData()

    // Play Billing
    implementation("com.android.billingclient:billing-ktx:${versions.playServices.billing}")

    // Volley
    implementation("com.android.volley:volley:${versions.network.volley}")

    // Kotlin Immutable Collections
    libs.android.kotlinImmutableCollections.get(this)

    // Play Review
    implementation("com.google.android.play:review-ktx:${versions.playServices.playReview}")

    // Paging
    implementation("androidx.paging:paging-runtime:${versions.common.paging}")
    implementation("androidx.paging:paging-compose:${versions.common.paging}")
}
