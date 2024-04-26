plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.10")
    implementation("com.android.tools.build:gradle:8.2.2")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.10")
}


configurations.all {
    resolutionStrategy.force("androidx.transition:transition:1.4.1")
    resolutionStrategy.force("org.jetbrains.kotlin:kotlin-stdlib:1.6.0")
    resolutionStrategy.force("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.6.0")
    resolutionStrategy.force("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.10")
    resolutionStrategy.force("org.jetbrains.kotlin:kotlin-reflect:1.8.10")
    resolutionStrategy.force("org.jetbrains:annotations:23.0.0")
    resolutionStrategy.force("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.7.1")
    resolutionStrategy.force("com.google.code.gson:gson:2.10.1")
    resolutionStrategy.force("com.google.dagger:dagger:2.45")
    resolutionStrategy.force("com.squareup:javapoet:1.13.0")
    resolutionStrategy.force("com.squareup:javawriter:2.5.0")
}