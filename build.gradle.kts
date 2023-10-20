import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

apply(plugin = "com.github.ben-manes.versions")

plugins {
    kotlin("jvm") version versions.android.kotlin apply false
    kotlin("android") version versions.android.kotlin apply false
    id("com.android.test") version versions.android.gradle apply false
    id("com.android.library") version versions.android.gradle apply false
    id("com.android.application") version versions.android.gradle apply false
    id("com.google.dagger.hilt.android") version versions.common.hilt apply false
    id("com.google.devtools.ksp") version versions.common.ksp apply false
    id("com.github.ben-manes.versions") version versions.customPlugin.benNamesVersions apply false
    id("androidx.baselineprofile") version versions.tooling.benchmarkMacroJunit4 apply false
    id("com.google.gms.google-services") version versions.firebase.servicesPlugin apply false
    id("com.google.firebase.crashlytics") version versions.firebase.crashlyticsPlugin apply false
    id("com.google.firebase.firebase-perf") version versions.firebase.performancePlugin apply false
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