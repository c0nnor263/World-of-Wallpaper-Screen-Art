@file:Suppress("UnstableApiUsage")

plugins {
    PluginType.LIBRARY.get(this)
}
android {
    namespace = "com.doodle.core.advertising"
    compileSdk = versions.config.compileSdk

    defaultConfig {
        minSdk = versions.config.minSdk
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        vectorDrawables {
            useSupportLibrary = true
        }
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
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = versions.compose.compiler
    }
    packaging {
        resources {
            excludes += versions.compose.exclude
        }
    }
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:data"))
    implementation(project(":core:ui"))
    implementation(
        "androidx.constraintlayout:constraintlayout:${versions.android.constraintLayoutXML}"
    )
    implementation("androidx.appcompat:appcompat:${versions.android.appCompat}")
    implementation("androidx.cardview:cardview:${versions.android.cardView}")
    coreData()
    composeCore()

    libs.playServices.ads.get(this)
    libs.compose.coil.get(this)
}
