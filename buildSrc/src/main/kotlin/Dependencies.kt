import dependencies.VersionAndroidX
import dependencies.VersionKotlin
import dependencies.VersionTesting
import dependencies.VersionThirdPart
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.getByType

/**
 *  @author Newki
 *
 * 通过扩展函数的方式导入功能模块的全部依赖
 * 可以自行随意添加或更改
 */

val DependencyHandler.libs
    get(): VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

fun DependencyHandler.appcompat() {
    api(libs.findBundle("appcompatBundles").get())

//    api(VersionAndroidX.appcompat)
//    api(VersionAndroidX.supportV4)
//    api(VersionAndroidX.coreKtx)
//    api(VersionAndroidX.activityKtx)
//    api(VersionAndroidX.fragmentKtx)
}

//生命周期监听
fun DependencyHandler.lifecycle() {
    api(VersionAndroidX.Lifecycle.livedata)
    api(VersionAndroidX.Lifecycle.liveDataKtx)
    api(VersionAndroidX.Lifecycle.runtime)
    api(VersionAndroidX.Lifecycle.runtimeKtx)

    api(VersionAndroidX.Lifecycle.viewModel)
    api(VersionAndroidX.Lifecycle.viewModelKtx)
    api(VersionAndroidX.Lifecycle.viewModelSavedState)

    kapt(VersionAndroidX.Lifecycle.compiler)
}

//Kotlin与协程
fun DependencyHandler.kotlin() {
    api(VersionKotlin.stdlib)
    api(VersionKotlin.reflect)
    api(VersionKotlin.stdlibJdk7)
    api(VersionKotlin.stdlibJdk8)

    api(VersionKotlin.Coroutines.android)
    api(VersionKotlin.Coroutines.core)
}

//依赖注入
fun DependencyHandler.hilt() {
    implementation(VersionAndroidX.Hilt.hiltAndroid)
    implementation(VersionAndroidX.Hilt.javapoet)
    implementation(VersionAndroidX.Hilt.javawriter)
    kapt(VersionAndroidX.Hilt.hiltCompiler)
}

//测试Test依赖
fun DependencyHandler.test() {
    testImplementation(VersionTesting.junit)
    androidTestImplementation(VersionTesting.androidJunit)
    androidTestImplementation(VersionTesting.espresso)
}

//常用的布局控件
fun DependencyHandler.widgetLayout() {
    api(VersionAndroidX.constraintlayout)
    api(VersionAndroidX.cardView)
    api(VersionAndroidX.recyclerView)
    api(VersionThirdPart.baseRecycleViewHelper)
    api(VersionAndroidX.material)
    api(VersionAndroidX.ViewPager.viewpager)
    api(VersionAndroidX.ViewPager.viewpager2)
}

//路由
fun DependencyHandler.router() {
    implementation(VersionThirdPart.ARouter.core)
    kapt(VersionThirdPart.ARouter.compiler)
}

//Work任务
fun DependencyHandler.work() {
    api(VersionAndroidX.Work.runtime)
    api(VersionAndroidX.Work.runtime_ktx)
}

//KV存储
fun DependencyHandler.dataStore() {
    implementation(VersionAndroidX.DataStore.preferences)
    implementation(VersionAndroidX.DataStore.core)
}

//网络请求
fun DependencyHandler.retrofit() {
    api(VersionThirdPart.Retrofit.core)
    implementation(VersionThirdPart.Retrofit.convertGson)
    api(VersionThirdPart.Retrofit.gson)
    api(VersionThirdPart.gsonFactory)
}

//图片加载
fun DependencyHandler.glide() {
    implementation(VersionThirdPart.Glide.core)
    implementation(VersionThirdPart.Glide.annotation)
    implementation(VersionThirdPart.Glide.integration)
    kapt(VersionThirdPart.Glide.compiler)
}

//多媒体相机相册
fun DependencyHandler.imageSelector() {
    implementation(VersionThirdPart.ImageSelector.core)
    implementation(VersionThirdPart.ImageSelector.compress)
    implementation(VersionThirdPart.ImageSelector.ucrop)
}

//弹窗
fun DependencyHandler.xpopup() {
    implementation(VersionThirdPart.XPopup.core)
    implementation(VersionThirdPart.XPopup.picker)
    implementation(VersionThirdPart.XPopup.easyAdapter)
}

//下拉刷新
fun DependencyHandler.refresh() {
    api(VersionThirdPart.SmartRefresh.core)
    api(VersionThirdPart.SmartRefresh.classicsHeader)
}


//fun DependencyHandler.compose() {
//    implementation(dependencies.VersionAndroidX.Compose.composeUi)
//    implementation(dependencies.VersionAndroidX.Compose.composeMaterial)
//    implementation(dependencies.VersionAndroidX.Compose.composeRuntime)
//    implementation(dependencies.VersionAndroidX.Compose.composeUiTooling)
//    implementation(dependencies.VersionAndroidX.Compose.composeUiGraphics)
//    implementation(dependencies.VersionAndroidX.Compose.composeUiToolingPreview)
//}