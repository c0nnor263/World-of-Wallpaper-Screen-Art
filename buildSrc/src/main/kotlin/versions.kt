import org.gradle.api.JavaVersion

object versions {
    object config {
        const val compileSdk = 34
        const val jvmTarget = "17"
        const val minSdk = 26
        const val targetSdk = 34
        val sourceCompatibility = JavaVersion.VERSION_17
        val targetCompatibility = JavaVersion.VERSION_17
    }

    object storage {
        const val room = "2.5.2"
        const val dataStore = "1.0.0"
    }

    object firebase {
        const val bom = "32.3.1"
        const val crashlyticsPlugin = "2.9.9"
        const val performancePlugin = "1.4.2"
        const val servicesPlugin = "4.4.0"
    }

    object compose {
        const val bom = "2023.10.00"
        const val compiler = "1.5.3"
        const val hiltNavigation = "1.0.0"
        const val navigation = "2.7.4"
        const val constraintLayout = "1.0.1"
    }

    object tooling {
        const val composeJunit = "1.5.3"
        const val benchmarkMacroJunit4 = "1.2.0-beta03"
        const val uiautomator = "2.2.0"
        const val androidEspressoCore = "3.5.1"
        const val androidJunit = "1.1.5"
        const val junit = "4.13.2"
    }

    object customPlugin {
        const val benNamesVersions = "0.49.0"

    }

    object playServices {
        const val ads = "22.4.0"
        const val billing = "6.0.1"
        const val integrity = "1.2.0"
    }

    object android {
        const val gradle = "8.1.2"
        const val kotlin = "1.9.10"
        const val coroutines = "1.7.3"
        const val material = "1.10.0"
        const val activity = "1.8.0"
        const val appCompat = "1.6.1"
        const val lifecycle = "2.6.2"
        const val coreKtx = "1.12.0"
    }

    object common {
        const val hilt = "2.48.1"
        const val excludeFiles = "META-INF/*.version"
        const val ksp = "1.9.10-1.0.13"
    }

    object startup {
        const val coreSplashscreen = "1.0.1"
        const val profileinstaller = "1.3.1"
        const val startupRuntime = "1.1.1"
    }

    object network {
        const val httpLoggingInterceptor = "4.11.0"
        const val retrofit = "2.9.0"
        const val converterGson = "2.9.0"
        const val gson = "2.8.8"

    }


}
