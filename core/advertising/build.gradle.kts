@file:Suppress("UnstableApiUsage")

plugins {
    PluginType.LIBRARY.get(this)
}
android {
    namespace = "com.notdoppler.core.advertising"
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
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.cardview:cardview:1.0.0")
    coreData()
    composeCore()

    libs.playServices.ads.get(this)
    libs.compose.coil.get(this)
}
