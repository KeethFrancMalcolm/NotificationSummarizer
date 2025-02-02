// Build script configuration
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.1.1")  // Ensure compatibility with your project
    }
}

// Plugin declarations (if needed)
plugins {
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "NotificationSummarizer"
include(":app")
