

import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.internal.impldep.junit.runner.Version.id
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.plugins
import org.gradle.kotlin.dsl.project
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.util.GradleVersion.version
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions
import org.jetbrains.kotlin.gradle.plugin.KaptExtension
import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion

open class DefaultGradlePlugin: Plugin<Project> {
    override fun apply(project: Project) {
        setProjectConfig(project)
        setConfigurations(project)
    }

    //项目配置
    private fun setProjectConfig(project: Project) {
        val isApplicationModule = project.plugins.hasPlugin("com.android.application")

        if (isApplicationModule) {
            // 处理 com.android.application 模块逻辑
            println("===> Handle Project Config by [com.android.application] Logic")
            setProjectConfigByApplication(project)
        } else {
            // 处理 com.android.library 模块逻辑
            println("===> Handle Project Config by [com.android.library] Logic")
            setProjectConfigByLibrary(project)
        }
    }

    //指定依赖版本
    private fun setConfigurations(project: Project) {
        //配置ARouter的Kapt配置
        project.configureKapt()

        project.configurations.all {
            resolutionStrategy.force("androidx.transition:transition:1.4.1")
            resolutionStrategy.force("org.jetbrains.kotlin:kotlin-stdlib:1.8.10")
            resolutionStrategy.force("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.8.10")
            resolutionStrategy.force("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.10")
            resolutionStrategy.force("org.jetbrains.kotlin:kotlin-reflect:1.8.10")
            resolutionStrategy.force("org.jetbrains:annotations:23.0.0")
            resolutionStrategy.force("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.7.1")
            resolutionStrategy.force("com.google.code.gson:gson:2.10.1")
            resolutionStrategy.force("com.google.dagger:dagger:2.45")
            resolutionStrategy.force("com.squareup:javapoet:1.13.0")
            resolutionStrategy.force("com.squareup:javawriter:2.5.0")
            resolutionStrategy.force("androidx.appcompat:appcompat:1.6.1")
        }
    }

    //设置 library 的相关配置
    private fun setProjectConfigByLibrary(project: Project) {
        //添加插件
        project.apply {
            plugin("kotlin-android")
            plugin("kotlin-kapt")
            plugin("org.jetbrains.kotlin.android")
            plugin("dagger.hilt.android.plugin")
        }

        project.library().apply {

            compileSdk = ProjectConfig.compileSdk

            defaultConfig {
                minSdk = ProjectConfig.minSdk
                testInstrumentationRunner = ProjectConfig.testInstrumentationRunner
                vectorDrawables {
                    useSupportLibrary = true
                }
                ndk {
                    //常用构建目标 'x86_64','armeabi-v7a','arm64-v8a'
                    abiFilters.addAll(arrayListOf("armeabi-v7a", "arm64-v8a", "x86_64"))
                }
                multiDexEnabled = true
            }

            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_17
                targetCompatibility = JavaVersion.VERSION_17
            }

            kotlinOptions {
                jvmTarget = "17"
            }

            buildFeatures {
                compose = true
                buildConfig = true
            }

//            packaging {
//                resources {
//                    excludes += "/META-INF/{AL2.0,LGPL2.1}"
//                }
//            }

        }

        //默认 library 的依赖
        project.dependencies {
            hilt()
            router()
            test()
            appcompat()
            lifecycle()
            kotlin()
            widgetLayout()

            if (isLibraryNeedService()) {
                //依赖 Service 服务
                //implementation(project(":cs-service"))
            }
        }

    }

    //设置 application 的相关配置
    private fun setProjectConfigByApplication(project: Project) {
        //添加插件
        project.apply {
            plugin("kotlin-android")
            plugin("kotlin-kapt")
            plugin("org.jetbrains.kotlin.android")
            plugin("kotlinx-serialization")
            plugin("kotlin-parcelize")
        }

        project.application().apply {
            compileSdk = ProjectConfig.compileSdk
            setCompileSdkVersion(ProjectConfig.compileSdk)
            defaultConfig {
                minSdk = ProjectConfig.minSdk
                targetSdk = ProjectConfig.targetSdk
                versionCode = ProjectConfig.versionCode
                versionName = ProjectConfig.versionName
                testInstrumentationRunner = ProjectConfig.testInstrumentationRunner
                vectorDrawables {
                    useSupportLibrary = true
                }
                ndk {
                    //常用构建目标 'x86_64','armeabi-v7a','arm64-v8a'
                    abiFilters.addAll(arrayListOf("armeabi-v7a", "arm64-v8a", "x86_64"))
                }
                multiDexEnabled = true
            }

            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_17
                targetCompatibility = JavaVersion.VERSION_17
            }

            // 设置 Kotlin JVM 目标版本
            kotlinOptions {
                jvmTarget = "17"
            }

            buildFeatures {
                compose = true
                buildConfig = true
            }

//            packaging {
//                resources {
//                    excludes += "/META-INF/{AL2.0,LGPL2.1}"
//                }
//            }

            signingConfigs {
                create("release") {
                    keyAlias = SigningConfigs.key_alias
                    keyPassword = SigningConfigs.key_password
                    storeFile = project.rootDir.resolve(SigningConfigs.store_file)
                    storePassword = SigningConfigs.store_password
                    enableV1Signing = true
                    enableV2Signing = true
                    enableV3Signing = true
                    enableV4Signing = true
                }
            }

            buildTypes {
                release {
                    isDebuggable = false    //是否可调试
                    isMinifyEnabled = true  //是否启用混淆
                    isShrinkResources = true   //是否移除无用的resource文件
                    isJniDebuggable = false // 是否打开jniDebuggable开关

                    proguardFiles(
                        getDefaultProguardFile("proguard-android-optimize.txt"),
                        "proguard-rules.pro"
                    )
                    signingConfig = signingConfigs.findByName("release")
                }
                debug {
                    isDebuggable = true
                    isMinifyEnabled = false
                    isShrinkResources = false
                    isJniDebuggable = true
                }
            }

            flavorDimensions.addAll(arrayListOf("default"))
            productFlavors.apply {
                create("common") {
                    dimension = "default"
                    if (buildFeatures.buildConfig == true) {
                        buildConfigField("String", "FLAVOR", "\"common\"")
                    }
                }

                create("google") {
                    dimension = "default"
                    if (buildFeatures.buildConfig == true) {
                        buildConfigField("String", "FLAVOR", "\"google\"")
                    }
                }
            }
        }

        //默认 application 的依赖
        project.dependencies {
//            hilt()
//            router()
//            test()
//            appcompat()
//            lifecycle()
//            kotlin()
//            widgetLayout()
            glide()

            //依赖 other module
            implementation(project(":base-core"))
            implementation(project(":codeeditor"))
            implementation(project(":login"))
            implementation(project(":jet-setting-core"))
            implementation(project(":ai"))
            implementation(project(":proxy"))
        }

    }

    //根据组件模块的类型给出不同的对象去配置
    private fun Project.library(): LibraryExtension {
        return extensions.getByType(LibraryExtension::class.java)
    }

    private fun Project.application(): BaseAppModuleExtension {
        return extensions.getByType(BaseAppModuleExtension::class.java)
    }

    // Application 级别 - 扩展函数来设置 KotlinOptions
    private fun BaseAppModuleExtension.kotlinOptions(action: KotlinJvmOptions.() -> Unit) {
        (this as org.gradle.api.plugins.ExtensionAware).extensions.configure(
            "kotlinOptions",
            action
        )
    }

    // Library 级别 - 扩展函数来设置 KotlinOptions
    private fun LibraryExtension.kotlinOptions(action: KotlinJvmOptions.() -> Unit) {
        (this as org.gradle.api.plugins.ExtensionAware).extensions.configure(
            "kotlinOptions",
            action
        )
    }

    //配置 Project 的 kapt
    private fun Project.configureKapt() {
        this.extensions.findByType(KaptExtension::class.java)?.apply {
            arguments {
                arg("AROUTER_MODULE_NAME", name)
            }
        }
    }

    //Library模块是否需要依赖底层 Service 服务，一般子 Module 模块或者 Module-api 模块会依赖到
    protected open fun isLibraryNeedService(): Boolean = false
}