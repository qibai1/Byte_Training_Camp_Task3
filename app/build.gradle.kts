plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.my_weather"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.my_weather"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    // 新添加的网络请求和工具库
    implementation(libs.okhttp)
    implementation(libs.gson)
    implementation(libs.glide)
    // 使用 libs 引用高德 SDK
    implementation(libs.amap.search)
    // 引用 RecyclerView
    implementation(libs.androidx.recyclerview)
}