import Dependencies.applyAndroidX
import Dependencies.applyGlide
import Dependencies.applyHilt
import Dependencies.applyRetrofit2
import Dependencies.applyTest

plugins {
    id ("com.android.application")
    id ("kotlin-android")
    id ("kotlin-kapt")
    id ("dagger.hilt.android.plugin")

}

android {
    compileSdk = Dependencies.COMPILE_SDK
    buildFeatures {
        dataBinding = true
    }
    defaultConfig {
        applicationId = "com.charlezz.mvvm"
        minSdk = Dependencies.MIN_SDK
        targetSdk = Dependencies.TARGET_SDK
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
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
    implementation (project(":data"))

    implementation(Dependencies.Google.MATERIAL)
    applyAndroidX()
    applyTest()
    applyRetrofit2()
    applyHilt()
    applyGlide()
}
kapt {
    correctErrorTypes = true
}
hilt {
    enableTransformForLocalTests = true
}