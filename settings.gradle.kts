@file:Suppress("UnstableApiUsage")

include(":feature:picturedetails")


include(":core:billing")


include(":core:network")


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
include(":core:database")
include(":core:data")
include(":core:domain")
include(":core:ui")
include(":core:navigation")
include(":feature:home")
include(":feature:store")
include(":feature:settings")
include(":baselineprofile")

