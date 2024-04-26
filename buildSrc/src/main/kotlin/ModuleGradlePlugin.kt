// 一般子 Module 模块或者 Module-api 模块会用到此 Gradle 模块
class ModuleGradlePlugin : DefaultGradlePlugin() {

    //允许 Library 模块依赖到 Service 模块
    override fun isLibraryNeedService(): Boolean {
        return true
    }
}
