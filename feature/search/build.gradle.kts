@file:Suppress("UnstableApiUsage")

plugins {
    PluginType.LIBRARY.get(this)
}

android {
    namespace = "com.doodle.feature.search"
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
    implementation(project(":core:data"))
    implementation(project(":core:database"))
    implementation(project(":core:domain"))
    implementation(project(":core:ui"))
    implementation(project(":core:network"))
    implementation(project(":core:advertising"))
    composeCore()

    implementation("io.coil-kt:coil-compose:${Versions.Compose.coil}")
    implementation("androidx.paging:paging-runtime:${Versions.Common.paging}")
    implementation("androidx.paging:paging-compose:${Versions.Common.paging}")
    testImplementation("androidx.paging:paging-testing:3.2.1")
    testImplementation(project(":core:testing"))
}
