@file:Suppress("UnstableApiUsage")

import java.io.FileInputStream
import java.util.Properties

plugins {
    PluginType.LIBRARY.get(this)
}


val localProperties = Properties()
localProperties.load(FileInputStream(rootProject.file("local.properties")))
android {
    namespace = "com.notdoppler.core.data"
    compileSdk = versions.config.compileSdk

    defaultConfig {
        minSdk = versions.config.minSdk
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        buildConfigField(
            "String",
            "API_KEY",
            "\"${localProperties.getProperty("API_KEY")}\""
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
}

dependencies {
    implementation(project(":core:database"))
    implementation(project(":core:domain"))

    implementation("androidx.core:core-ktx:${versions.android.coreKtx}")
    implementation("androidx.appcompat:appcompat:${versions.android.appCompat}")
    implementation("com.google.android.material:material:${versions.android.material}")
    testImplementation("junit:junit:${versions.tooling.junit}")
    androidTestImplementation("androidx.test.ext:junit:${versions.tooling.androidJunit}")
    androidTestImplementation("androidx.test.espresso:espresso-core:${versions.tooling.androidEspressoCore}")
    implementation("com.android.billingclient:billing-ktx:${versions.playServices.billing}")
    implementation("com.android.volley:volley:1.2.1")
    coreData()


    implementation("com.google.android.play:review:2.0.1")
    implementation("com.google.android.play:review-ktx:2.0.1")

    implementation ("androidx.paging:paging-runtime:3.2.1")
    implementation ("androidx.paging:paging-compose:3.2.1")
}
