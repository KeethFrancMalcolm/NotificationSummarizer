// Remove any duplicate buildscript blocks—keep only one here.
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        // Set your desired AGP version here (for example, 8.8.0)
        classpath("com.android.tools.build:gradle:8.8.0")
    }
}

// (Optional) Plugins block at the root level if needed.
plugins {
    // You can leave this empty or add root-level plugins if necessary.
}

// Use dependency resolution management if desired.
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "NotificationSummarizer"
// Include your modules (assuming you have an "app" module):
include(":app")
