rootProject.name = "aigent"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        mavenCentral()
        gradlePluginPortal()
//        maven("https://maven.aliyun.com/repository/public")
//        maven("https://maven.aliyun.com/repository/gradle-plugin")
//        maven("https://maven.aliyun.com/repository/google")
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        mavenCentral()
//        maven("https://maven.aliyun.com/repository/public")
        maven("https://jitpack.io")
//        maven("https://maven.aliyun.com/repository/google")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

include(":composeApp")
