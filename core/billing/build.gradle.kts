@file:Suppress("UnstableApiUsage")

import java.io.FileInputStream
import java.util.Properties

plugins {
    PluginType.LIBRARY.get(this)
}


val localProperties = Properties()
localProperties.load(FileInputStream(rootProject.file("local.properties")))
android {
    namespace = "com.notdoppler.core.billing"
    compileSdk = versions.config.compileSdk

    defaultConfig {
        minSdk = versions.config.minSdk
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        buildConfigField(
            "String",
            "verifyPurchases",
            "\"${localProperties.getProperty("verifyPurchases")}\""
        )
        // TODO BuildConfigField verifyPurchases
    }

    buildTypes {
        release {

            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        create("nonMinifiedRelease") {
        }
    }

    compileOptions {
        sourceCompatibility = versions.config.sourceCompatibility
        targetCompatibility = versions.config.targetCompatibility
    }
    kotlinOptions {
        jvmTarget = versions.config.jvmTarget

    }
    composeOptions {
        kotlinCompilerExtensionVersion = versions.compose.compiler
    }
    buildFeatures {
        buildConfig = true
        compose = true
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
    composeCore()
    implementation("com.android.billingclient:billing-ktx:${versions.playServices.billing}")
    implementation("com.android.volley:volley:1.2.1")
    coreData()


    implementation("com.google.android.play:review:2.0.1")
    implementation("com.google.android.play:review-ktx:2.0.1")
}
