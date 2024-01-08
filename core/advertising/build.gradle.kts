@file:Suppress("UnstableApiUsage")

plugins {
    PluginType.LIBRARY.get(this)
}
android {
    namespace = "com.doodle.core.advertising"
    compileSdk = Versions.Config.compileSdk

    defaultConfig {
        minSdk = Versions.Config.minSdk
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
        sourceCompatibility = Versions.Config.sourceCompatibility
        targetCompatibility = Versions.Config.targetCompatibility
    }
    kotlinOptions {
        jvmTarget = Versions.Config.jvmTarget
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Versions.Compose.compiler
    }
    packaging {
        resources {
            excludes += Versions.Compose.exclude
        }
    }
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:data"))
    implementation(project(":core:ui"))
    implementation(
        "androidx.constraintlayout:constraintlayout:${Versions.Android.constraintLayoutXML}"
    )
    implementation("androidx.appcompat:appcompat:${Versions.Android.appCompat}")
    implementation("androidx.cardview:cardview:${Versions.Android.cardView}")
    coreData()
    composeCore()

    libs.playServices.ads.get(this)
    libs.compose.coil.get(this)
}
