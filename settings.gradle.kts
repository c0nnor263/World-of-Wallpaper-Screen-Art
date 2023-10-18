@file:Suppress("UnstableApiUsage")




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
include(":core:navigation")
include(":feature:home")
include(":feature:picturedetails")
include(":feature:search")
include(":baselineprofile")












