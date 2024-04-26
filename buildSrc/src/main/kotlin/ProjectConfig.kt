

object ProjectConfig {

    const val minSdk = 23
    const val compileSdk = 34
    const val targetSdk = 33

    const val versionCode = 100
    const val versionName = "1.0.0"

    const val applicationId = "com.funny.translation.tmp"
    const val testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
}


object SigningConfigs {
    //密钥文件路径
    const val store_file = "key/newki.jks"

    //密钥密码
    const val store_password = "123456"

    //密钥别名
    const val key_alias = "newki"

    //别名密码
    const val key_password = "123456"
}