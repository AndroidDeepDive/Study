buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.0.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Dependencies.Kotlin.VERSION}")
        classpath ("com.google.dagger:hilt-android-gradle-plugin:${Dependencies.Hilt.VERSION}")
    }
}

//task clean(type: Delete) {
//    delete rootProject.buildDir
//}
