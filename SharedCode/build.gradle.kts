import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
    id("kotlinx-serialization")
}
// todo figure out ktlint for native
// apply(from = "../ktlint.gradle")


kotlin {
    // select iOS target platform depending on the Xcode environment variables
    val iOSTarget: (String, KotlinNativeTarget.() -> Unit) -> KotlinNativeTarget =
        if (System.getenv("SDK_NAME")?.startsWith("iphoneos") == true)
            ::iosArm64
        else
            ::iosX64

    iOSTarget("ios") {
        binaries {
            framework {
                baseName = "SharedCode"
            }
        }
    }

    jvm("android")

    val ktorVersion = "1.3.0-beta-2"
    val serialization_version = "0.14.0"
    sourceSets["commonMain"].dependencies {
        implementation("org.jetbrains.kotlin:kotlin-stdlib-common")

        implementation("io.ktor:ktor-client-core:$ktorVersion")
        implementation("io.ktor:ktor-client-json:$ktorVersion")
        implementation("io.ktor:ktor-client-logging:$ktorVersion")
        implementation("io.ktor:ktor-client-serialization:$ktorVersion")
        implementation("io.ktor:ktor-client-gson:$ktorVersion")

        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.2")
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-common:$serialization_version")
        implementation("com.github.florent37:multiplatform-preferences:1.0.0")
    }

    sourceSets["androidMain"].dependencies {
        implementation("org.jetbrains.kotlin:kotlin-stdlib")

        implementation("io.ktor:ktor-client-android:$ktorVersion")
        implementation("io.ktor:ktor-client-logging-jvm:$ktorVersion")
        implementation("io.ktor:ktor-client-serialization-jvm:$ktorVersion")

        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.2")
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:$serialization_version")
        implementation("com.github.florent37:multiplatform-preferences-android:1.0.0")
    }
    sourceSets["iosMain"].dependencies {
        implementation("io.ktor:ktor-client-ios:$ktorVersion")
        implementation("io.ktor:ktor-client-logging-native:$ktorVersion")
        implementation("io.ktor:ktor-client-serialization-native:$ktorVersion")

        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-native:1.3.2")
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-native:$serialization_version")
        implementation("com.github.florent37:multiplatform-preferences-ios:1.0.0")
    }
}

val packForXcode by tasks.creating(Sync::class) {
    group = "build"

    // selecting the right configuration for the iOS framework depending on the Xcode environment variables
    val mode = System.getenv("CONFIGURATION") ?: "DEBUG"
    val framework = kotlin.targets.getByName<KotlinNativeTarget>("ios").binaries.getFramework(mode)

    inputs.property("mode", mode)
    dependsOn(framework.linkTask)

    val targetDir = File(buildDir, "xcode-frameworks")
    from({ framework.outputDirectory })
    into(targetDir)

    doLast {
        val gradlew = File(targetDir, "gradlew")
        gradlew.writeText("#!/bin/bash\nexport 'JAVA_HOME=${System.getProperty("java.home")}'\ncd '${rootProject.rootDir}'\n./gradlew \$@\n")
        gradlew.setExecutable(true)
    }
}

tasks.getByName("build").dependsOn(packForXcode)
