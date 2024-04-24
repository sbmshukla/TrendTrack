// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
    }
    dependencies {
        val navVersion = "2.7.7"
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$navVersion")
    }
}

plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    id ("com.android.library") version "7.3.1" apply false
    id("com.google.devtools.ksp") version "1.9.0-1.0.13" apply false
}