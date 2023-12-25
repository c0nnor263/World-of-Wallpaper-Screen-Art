@file:Suppress("UnstableApiUsage")

import java.io.FileInputStream
import java.util.Properties

plugins {
    PluginType.LIBRARY.get(this)
}

val gradleProperties = Properties()
gradleProperties.load(FileInputStream(rootProject.file("gradle.properties")))

android {
    namespace = "com.doodle.core.network"
    compileSdk = versions.config.compileSdk

    defaultConfig {
        minSdk = versions.config.minSdk
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        buildConfigField(
            "String",
            "API_KEY",
            "\"${gradleProperties.getProperty("API_KEY")}\""
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

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = versions.config.sourceCompatibility
        targetCompatibility = versions.config.targetCompatibility
    }
    kotlinOptions {
        jvmTarget = versions.config.jvmTarget
    }
}

dependencies {
    implementation(project(":core:data"))
    implementation(project(":core:domain"))
    networkModule()

    implementation("androidx.paging:paging-runtime:${versions.common.paging}")
    implementation("androidx.paging:paging-compose:${versions.common.paging}")
}
