@file:Suppress("UnstableApiUsage")

plugins {
    PluginType.LIBRARY.get(this)
}

android {
    namespace = "com.doodle.feature.picturedetails"
    compileSdk = versions.config.compileSdk

    defaultConfig {
        minSdk = versions.config.minSdk
        testInstrumentationRunner = "com.doodle.core.picturedetails.di.HiltTestRunner"
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
    implementation(project(":core:advertising"))
    implementation(project(":core:data"))
    implementation(project(":core:domain"))
    implementation(project(":core:database"))
    implementation(project(":core:network"))
    implementation(project(":core:ui"))
    composeCore()

    implementation("io.coil-kt:coil-compose:${versions.compose.coil}")
    //noinspection GradleDependency
    implementation("androidx.compose.foundation:foundation:${versions.compose.foundation}")
    implementation("androidx.paging:paging-runtime:${versions.common.paging}")
    implementation("androidx.paging:paging-compose:${versions.common.paging}")
    androidTestImplementation("androidx.paging:paging-testing:3.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.5.1")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    libs.network.gson.get(this)

    libs.playServices.ads.get(this)
}
