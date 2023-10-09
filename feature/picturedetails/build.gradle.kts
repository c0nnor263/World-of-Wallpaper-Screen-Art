@file:Suppress("UnstableApiUsage")

plugins {
    PluginType.LIBRARY.get(this)
}

android {
    namespace = "com.notdoppler.feature.picturedetails"
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
            resources.excludes.add(versions.common.excludeFiles)
        }
    }

}
dependencies {
    implementation(project(":core:data"))
    implementation(project(":core:domain"))
    implementation(project(":core:database"))
    implementation(project(":core:ui"))
    composeCore()

    implementation("io.coil-kt:coil-compose:2.4.0")
    implementation("androidx.compose.foundation:foundation:1.6.0-alpha07")
    implementation ("androidx.paging:paging-runtime:3.2.1")
    implementation ("androidx.paging:paging-compose:3.2.1")
}
