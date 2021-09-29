import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.exclude
import org.gradle.kotlin.dsl.project

object Dependencies {

    const val COMPILE_SDK = 31
    const val MIN_SDK = 26
    const val TARGET_SDK = 31

    private const val implementation = "implementation"
    private const val testImplementation = "testImplementation"
    private const val androidTestImplementation = "androidTestImplementation"

    private const val kapt = "kapt"
    private const val kaptTest = "kaptTest"
    private const val kaptAndroidTest = "kaptAndroidTest"



    object Kotlin{
        const val VERSION = "1.5.21"
        const val STD_LIB = "org.jetbrains.kotlin:kotlin-stdlib:$VERSION"
        const val TEST = "org.jetbrains.kotlin:kotlin-test-junit:$VERSION"

        const val COROUTINE = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$VERSION"
        const val COROUTINE_TEST = "org.jetbrains.kotlinx:kotlinx-coroutines-test:1.5.1"
        const val COROUTINE_CORE = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$VERSION"
    }
    object Google{
        const val MATERIAL = "com.google.android.material:material:1.4.0"
    }

    object AndroidX {
        const val APPCOMPAT = "androidx.appcompat:appcompat:1.3.1"
        const val RECYCLER_VIEW = "androidx.recyclerview:recyclerview:1.2.0"
        const val CONSTRAINT_LAYOUT = "androidx.constraintlayout:constraintlayout:2.1.0"
        const val ACTIVITY = "androidx.activity:activity-ktx:1.2.2"
        const val FRAGMENT = "androidx.fragment:fragment-ktx:1.3.2"
        const val CORE = "androidx.core:core-ktx:1.6.0"

        private const val VERSION_PAGING = "3.0.0"
        const val PAGING_RUNTIME = "androidx.paging:paging-runtime-ktx:$VERSION_PAGING"
        const val PAGING_COMMON = "androidx.paging:paging-common:$VERSION_PAGING"

    }

    fun DependencyHandlerScope.applyAndroidX(){
        implementation(AndroidX.APPCOMPAT)
        implementation(AndroidX.CORE)
        implementation(AndroidX.ACTIVITY)
        implementation(AndroidX.FRAGMENT)
        implementation(AndroidX.RECYCLER_VIEW)
        implementation(AndroidX.CONSTRAINT_LAYOUT)
        implementation(AndroidX.PAGING_RUNTIME)
    }

    object Retrofit2{
        const val VERSION = "2.9.0"
        const val CORE = "com.squareup.retrofit2:retrofit:${VERSION}"
        const val MOSHI = "com.squareup.retrofit2:converter-moshi:${VERSION}"
        const val SCALARS = "com.squareup.retrofit2:converter-scalars:${VERSION}"
    }

    fun DependencyHandlerScope.applyRetrofit2(){
        implementation(Retrofit2.CORE)
        implementation(Retrofit2.MOSHI)
        implementation(Retrofit2.SCALARS)
    }


//    object Lifecycle{
//        const val VIEWMODEL = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycle}"
//        const val VIEWMODEL_SAVEDSTATE = "androidx.lifecycle:lifecycle-viewmodel-savedstate:${Versions.lifecycle}"
//        const val RUNTIME = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycle}"
//        const val EXTENSIONS = "androidx.lifecycle:lifecycle-extensions:${Versions.lifecycleExt}"
//        const val COMMON_JAVA8 = "androidx.lifecycle:lifecycle-common-java8:${Versions.lifecycle}"
//    }
//
//    fun DependencyHandlerScope.applyLifecycle(){
//        implementation(Lifecycle.VIEWMODEL)
//        implementation(Lifecycle.VIEWMODEL_SAVEDSTATE)
//        implementation(Lifecycle.COMMON_JAVA8)
//        implementation(Lifecycle.EXTENSIONS)
//        implementation(Lifecycle.RUNTIME)
//    }

//    object Navigation{
//        const val FRAGMENT = "androidx.navigation:navigation-fragment-ktx:${Versions.navigation}"
//        const val UI = "androidx.navigation:navigation-ui-ktx:${Versions.navigation}"
//        const val DYNAMIC_FEATURE_FRAGMENT = "androidx.navigation:navigation-dynamic-features-fragment:${Versions.navigation}"
//    }
//
//    fun DependencyHandlerScope.applyNavigation(){
//        implementation(Navigation.FRAGMENT)
//        implementation(Navigation.UI)
//        implementation(Navigation.DYNAMIC_FEATURE_FRAGMENT)
//    }

    object Glide{
        const val VERSION = "4.12.0"
        const val CORE = "com.github.bumptech.glide:glide:$VERSION"
        const val COMPILER = "com.github.bumptech.glide:compiler:$VERSION"
        const val OKHTTP3_INTEGRATION = "com.github.bumptech.glide:okhttp3-integration:$VERSION"
        const val TRANSFORMATIONS = "jp.wasabeef:glide-transformations:4.3.0"
    }

    fun DependencyHandlerScope.applyGlide(){
        implementation(Glide.CORE)
        kapt(Glide.COMPILER)
        implementation(Glide.OKHTTP3_INTEGRATION)
    }

//    object JakeWharton{
//        const val THREE_TEN_ABP = "com.jakewharton.threetenabp:threetenabp:${Versions.threetenabp}"
//        const val TIMBER = "com.jakewharton.timber:timber:${Versions.timber}"
//        const val RXRELAY = "com.jakewharton.rxrelay2:rxrelay:${Versions.rxrelay2}"
//        const val RXBINDING = "com.jakewharton.rxbinding2:rxbinding:${Versions.rxbinding}"
//    }

    object Test{
        const val JUNIT = "junit:junit:4.13.2"
        const val ANDROID_EXT_JUNIT = "androidx.test.ext:junit:1.1.3"
        const val ANDROID_ESPRESSO_CORE = "androidx.test.espresso:espresso-core:3.4.0"
    }

    fun DependencyHandlerScope.applyTest(){
        Dependencies.testImplementation(Test.JUNIT)
        androidTestImplementation(Test.ANDROID_EXT_JUNIT)
        androidTestImplementation(Test.ANDROID_ESPRESSO_CORE)
    }

    object Hilt{
        const val VERSION = "2.38.1"

        const val CORE = "com.google.dagger:hilt-android:$VERSION"
        const val COMPILER = "com.google.dagger:hilt-compiler:$VERSION"

        // For instrumentation tests
        const val ANDROID_TESTING = "com.google.dagger:hilt-android-testing:$VERSION"
        const val ANDROID_TESTING_COMPILER = "com.google.dagger:hilt-compiler:$VERSION"

        // For local unit tests
        const val LOCAL_TESTING = "com.google.dagger:hilt-android-testing:$VERSION"
        const val LOCAL_TESTING_COMPILER = "com.google.dagger:hilt-compiler:$VERSION"
    }

    fun DependencyHandlerScope.applyHilt(){
        implementation(Hilt.CORE)
        kapt(Hilt.COMPILER)

        kaptTest(Hilt.LOCAL_TESTING_COMPILER)
        testImplementation(Hilt.LOCAL_TESTING)

        androidTestImplementation(Hilt.ANDROID_TESTING)
        kaptAndroidTest(Hilt.ANDROID_TESTING_COMPILER)
    }


}