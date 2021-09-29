import Dependencies.applyAndroidX
import Dependencies.applyHilt
import Dependencies.applyRetrofit2
import Dependencies.applyTest

plugins {
    id ("com.android.library")
    id ("kotlin-android")
    id("kotlin-kapt")
}

android {
    compileSdk = Dependencies.COMPILE_SDK

    buildFeatures {
        dataBinding = true
    }

    defaultConfig {
        minSdk = Dependencies.MIN_SDK
        targetSdk = Dependencies.TARGET_SDK

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }
}





dependencies {
    implementation (project(":domain"))

    testImplementation(Dependencies.Kotlin.COROUTINE_TEST)
    testImplementation(Dependencies.Kotlin.TEST)

    implementation(Dependencies.Google.MATERIAL)
    implementation(Dependencies.AndroidX.PAGING_RUNTIME)
    applyAndroidX()
    applyTest()
    applyRetrofit2()
    applyHilt()
}