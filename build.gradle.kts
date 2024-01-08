import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

apply(plugin = "com.github.ben-manes.versions")

plugins {
    kotlin("jvm") version Versions.Android.kotlin apply false
    kotlin("android") version Versions.Android.kotlin apply false
    id("com.android.test") version Versions.Android.gradle apply false
    id("com.android.library") version Versions.Android.gradle apply false
    id("com.android.application") version Versions.Android.gradle apply false
    id("com.google.dagger.hilt.android") version Versions.Common.hilt apply false
    id("com.google.devtools.ksp") version Versions.Common.ksp apply false
    id("com.github.ben-manes.versions") version Versions.CustomPlugin.benNamesVersions apply false
    id("androidx.baselineprofile") version Versions.Tooling.benchmarkMacroJunit4 apply false
    id("com.google.gms.google-services") version Versions.Firebase.servicesPlugin apply false
    id("com.google.firebase.crashlytics") version Versions.Firebase.crashlyticsPlugin apply false
    id("com.google.firebase.firebase-perf") version Versions.Firebase.performancePlugin apply false
}

tasks.withType<DependencyUpdatesTask> {
    rejectVersionIf {
        val version = candidate.version
        val stableKeyword = listOf("RELEASE", "FINAL", "GA").any {
            version.uppercase(java.util.Locale.getDefault())
                .contains(it)
        }
        val regex = "^[0-9,.v-]+(-r)?$".toRegex()
        val isStable = stableKeyword || regex.matches(version)
        isStable.not()
    }
}

allprojects {
    tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class.java).configureEach {
        kotlinOptions {
            if (project.findProperty("composeCompilerReports") == "true") {
                freeCompilerArgs += listOf(
                    "-P",
                    "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=" +
                        project.buildDir.absolutePath + "/compose_compiler"
                )
            }
            if (project.findProperty("composeCompilerMetrics") == "true") {
                freeCompilerArgs += listOf(
                    "-P",
                    "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=" +
                        project.buildDir.absolutePath + "/compose_compiler"
                )
            }
        }
    }
}
