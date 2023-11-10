@file:Suppress("UnstableApiUsage")

include(":feature:splash")


include(":feature:favorites")

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Wallpapers"
include(":app")
include(":core:billing")
include(":core:database")
include(":core:data")
include(":core:domain")
include(":core:network")
include(":core:ui")
include(":feature:home")
include(":feature:picturedetails")
include(":feature:search")
include(":baselineprofile")












