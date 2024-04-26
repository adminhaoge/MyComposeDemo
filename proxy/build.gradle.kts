import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlinx-serialization")
    id("kotlin-parcelize")
}

apply {
    from(project.rootDir.resolve("base.gradle"))
}

kotlin {
    // 设置语言版本为 1.9
    // 可以使用最新的语言版本，确保包含 "data objects" 功能
    // 也可以直接设置为最新版本，例如 "1.5"
    sourceSets.all {
        languageSettings.languageVersion = "1.9"
    }
}

android {
    namespace = "com.example.proxy"
    compileSdk = 34

    defaultConfig {
        minSdk = 23

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(project(":base-core"))
    implementation("com.tencent.imsdk:imsdk-plus:7.8.5505")
//    implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.3.5")
//
//    val coroutines_version = "1.5.1"
//    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines_version")
//    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutines_version")
}