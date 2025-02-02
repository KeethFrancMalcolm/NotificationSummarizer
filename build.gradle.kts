// build.gradle.kts (root)

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        // Set your desired Android Gradle Plugin version.
        classpath("com.android.tools.build:gradle:8.8.0")
    }
}

plugins {
    // You can add any root-level plugins here if needed.
}

// (Optional) Global configurations can go here.
// Do NOT include settings DSL like dependencyResolutionManagement, rootProject.name, or include().
