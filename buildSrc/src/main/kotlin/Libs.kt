import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.project


object libs {

    object firebase {
        val bom = Dependency("com.google.firebase:firebase-bom", versions.firebase.bom, Type.PLATFORM)
        val crashlyticsKtx = Dependency("com.google.firebase:firebase-crashlytics-ktx")
        val analyticsKtx = Dependency("com.google.firebase:firebase-analytics-ktx")
        val perfKtx = Dependency("com.google.firebase:firebase-perf-ktx")
    }

    object storage {
        val room = Dependency("androidx.room:room-runtime", versions.storage.room)
        val roomCompiler =
            Dependency("androidx.room:room-compiler", versions.storage.room, Type.KSP)
        val roomKtx = Dependency("androidx.room:room-ktx", versions.storage.room)
        val datastorePreferences =
            Dependency("androidx.datastore:datastore-preferences", versions.storage.dataStore)
    }

    object tooling {
        val junit = Dependency("junit:junit", versions.tooling.junit, Type.TEST)
        val androidJunit =
            Dependency("androidx.test.ext:junit", versions.tooling.androidJunit, Type.ANDROID_TEST)
        val espressoCore = Dependency(
            "androidx.test.espresso:espresso-core",
            versions.tooling.androidEspressoCore, Type.ANDROID_TEST
        )
    }

    object compose {
        val bom = Dependency("androidx.compose:compose-bom", versions.compose.bom, Type.PLATFORM)
        val ui = Dependency("androidx.compose.ui:ui")
        val uiGraphics = Dependency("androidx.compose.ui:ui-graphics")
        val uiToolingPreview = Dependency("androidx.compose.ui:ui-tooling-preview")
        val material3 =
            Dependency("androidx.compose.material3:material3")
        val materialIconsExtended =
            Dependency("androidx.compose.material:material-icons-extended")
        val uiTooling = Dependency("androidx.compose.ui:ui-tooling", type = Type.DEBUG)
        val uiTestManifest = Dependency("androidx.compose.ui:ui-test-manifest", type = Type.DEBUG)
        val navigationCompose =
            Dependency("androidx.navigation:navigation-compose", versions.compose.navigation)
        val hiltNavigationCompose =
            Dependency("androidx.hilt:hilt-navigation-compose", versions.compose.hiltNavigation)
        val constraintLayoutCompose = Dependency(
            "androidx.constraintlayout:constraintlayout-compose",
            versions.compose.constraintLayout
        )

        val all = listOf(
            bom,
            ui,
            uiGraphics,
            uiToolingPreview,
            material3,
            materialIconsExtended,
            uiTooling,
            uiTestManifest,
            navigationCompose,
            hiltNavigationCompose,
            constraintLayoutCompose
        )
    }

    object android {
        val coreKtx = Dependency("androidx.core:core-ktx", versions.android.coreKtx)
        val activity = Dependency("androidx.activity:activity-compose", versions.android.activity)
    }

    object lifecycle {
        val runtimeComposeKtx =
            Dependency("androidx.lifecycle:lifecycle-runtime-compose", versions.android.lifecycle)
        val runtimeKtx =
            Dependency("androidx.lifecycle:lifecycle-runtime-ktx", versions.android.lifecycle)
        val viewmodelKtx =
            Dependency("androidx.lifecycle:lifecycle-viewmodel-ktx", versions.android.lifecycle)
        val viewmodelCompose =
            Dependency("androidx.lifecycle:lifecycle-viewmodel-compose", versions.android.lifecycle)
    }

    object hilt {
        val hiltAndroid = Dependency("com.google.dagger:hilt-android", versions.common.hilt)
        val hiltAndroidCompiler =
            Dependency("com.google.dagger:hilt-android-compiler", versions.common.hilt, Type.KSP)
    }

    object coroutines {
        val core =
            Dependency("org.jetbrains.kotlinx:kotlinx-coroutines-core", versions.android.coroutines)
        val android =
            Dependency(
                "org.jetbrains.kotlinx:kotlinx-coroutines-android",
                versions.android.coroutines
            )
    }

    object playServices {
        val billingKtx =
            Dependency("com.android.billingclient:billing-ktx", versions.playServices.billing)
        val playServicesAds =
            Dependency("com.google.android.gms:play-services-ads", versions.playServices.ads)
        val integrity =
            Dependency("com.google.android.play:integrity", versions.playServices.integrity)
    }

    object startup {
        val coreSplashscreen =
            Dependency("androidx.core:core-splashscreen", versions.startup.coreSplashscreen)
        val profileinstaller =
            Dependency(
                "androidx.profileinstaller:profileinstaller",
                versions.startup.profileinstaller
            )
        val startupRuntime =
            Dependency("androidx.startup:startup-runtime", versions.startup.startupRuntime)
    }

    object network{
        val retrofit = Dependency("com.squareup.retrofit2:retrofit", versions.network.retrofit)
        val retrofitConverterGson = Dependency("com.squareup.retrofit2:converter-gson", versions.network.converterGson)
        val httpLoggingInterceptor = Dependency("com.squareup.okhttp3:logging-interceptor", versions.network.httpLoggingInterceptor)
        val gson = Dependency("com.google.code.gson:gson", versions.network.gson)

    }
}


fun DependencyHandler.composeCore() {
    hilt()
    test()
    libs.android.coreKtx.get(this)
    libs.android.activity.get(this)
    libs.lifecycle.runtimeComposeKtx.get(this)
    getAll(this, libs.compose.all)
}

fun DependencyHandler.test() {
    libs.tooling.junit.get(this)
    libs.tooling.androidJunit.get(this)
    libs.tooling.espressoCore.get(this)
}

fun DependencyHandler.firebase() {
    libs.firebase.bom.get(this)
    libs.firebase.crashlyticsKtx.get(this)
    libs.firebase.analyticsKtx.get(this)
    libs.firebase.perfKtx.get(this)
}

fun DependencyHandler.lifecycle() {
    libs.lifecycle.runtimeKtx.get(this)
    libs.lifecycle.viewmodelKtx.get(this)
    libs.lifecycle.viewmodelCompose.get(this)
}

fun DependencyHandler.room() {
    libs.storage.room.get(this)
    libs.storage.roomCompiler.get(this)
    libs.storage.roomKtx.get(this)
}

fun DependencyHandler.fullStorage() {
    room()
    libs.storage.datastorePreferences.get(this)
}

fun DependencyHandler.coroutines() {
    libs.coroutines.core.get(this)
    libs.coroutines.android.get(this)
}

fun DependencyHandler.hilt() {
    libs.hilt.hiltAndroid.get(this)
    libs.hilt.hiltAndroidCompiler.get(this)
}

fun DependencyHandler.coreData() {
    hilt()
    fullStorage()
    libs.playServices.playServicesAds.get(this)
}


fun DependencyHandler.appModule() {
    add("baselineProfile", project(":baselineprofile"))
    composeCore()
    firebase()
    lifecycle()
    coroutines()
    libs.startup.coreSplashscreen.get(this)
    libs.startup.profileinstaller.get(this)
    libs.startup.startupRuntime.get(this)
    libs.playServices.billingKtx.get(this)
    libs.playServices.playServicesAds.get(this)
    libs.playServices.integrity.get(this)
}

fun DependencyHandler.databaseModule() {
    fullStorage()
    hilt()

    libs.playServices.playServicesAds.get(this)
}

fun DependencyHandler.networkModule() {
    network()
    hilt()
}


fun DependencyHandler.network(){
    libs.network.retrofit.get(this)
    libs.network.retrofitConverterGson.get(this)
    libs.network.httpLoggingInterceptor.get(this)
    libs.network.gson.get(this)
}














