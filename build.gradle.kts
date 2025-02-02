// Root build.gradle.kts

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        // Use your desired Android Gradle Plugin version.
        classpath("com.android.tools.build:gradle:8.8.0")
    }
}

plugins {
    // Add any root-level plugins here if needed.
}

// Other global build configurations can go here.
