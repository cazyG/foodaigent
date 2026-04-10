//rootProject.name = "aigent"
//enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
//
//pluginManagement {
//    repositories {
//        google {
//            mavenContent {
//                includeGroupAndSubgroups("androidx")
//                includeGroupAndSubgroups("com.android")
//                includeGroupAndSubgroups("com.google")
//            }
//        }
//        mavenCentral()
//        gradlePluginPortal()
//        maven("https://maven.google.com")
//    }
//}
//
//dependencyResolutionManagement {
//    repositories {
//        google {
//            mavenContent {
//                includeGroupAndSubgroups("androidx")
//                includeGroupAndSubgroups("com.android")
//                includeGroupAndSubgroups("com.google")
//            }
//        }
//        mavenCentral()
//        maven("https://maven.google.com")
//    }
//}
//
//plugins {
//    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
//}
//
//include(":composeApp")
rootProject.name = "aigent"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.aliyun.com/repository/google")
        maven("https://maven.aliyun.com/repository/gradle-plugin")
        maven("https://maven.aliyun.com/repository/public")
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven("https://jitpack.io") // 添加JitPack仓库，用于一些第三方库
        maven("https://maven.aliyun.com/repository/google")
        maven("https://maven.aliyun.com/repository/public")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

include(":composeApp")