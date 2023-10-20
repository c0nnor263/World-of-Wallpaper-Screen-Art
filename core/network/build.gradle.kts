@file:Suppress("UnstableApiUsage")

plugins {
    PluginType.LIBRARY.get(this)
}

android {
    namespace = "com.notdoppler.core.network"
    compileSdk = versions.config.compileSdk

    defaultConfig {
        minSdk = versions.config.minSdk
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
}


dependencies {
    implementation(project(":core:data"))
    implementation(project(":core:domain"))
    networkModule()

    implementation ("androidx.paging:paging-runtime:3.2.1")
    implementation ("androidx.paging:paging-compose:3.2.1")
}