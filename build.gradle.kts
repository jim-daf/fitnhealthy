buildscript {
    repositories {
        google()
        mavenCentral()
        //jcenter()
        gradlePluginPortal()
        maven {
            url = uri("https://jitpack.io")
        }
        maven{
            url = uri("https://maven.google.com")
        }
        maven { url = uri("https://jcenter.bintray.com") }
    }
    dependencies {
        classpath("com.google.gms:google-services:4.4.1")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.49")
    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.3.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    id("com.google.gms.google-services") version "4.4.1" apply false
}