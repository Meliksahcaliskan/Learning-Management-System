// settings.gradle.kts

pluginManagement {
    repositories {
        // Gradle Plugin Portal (Multiplatform ve Compose için gerekli)
        gradlePluginPortal()
        // Android Gradle Plugin ve diğer Google bileşenleri için
        google()
        // Maven Central'dan bağımlılıkları çözmek için
        mavenCentral()
        // Compose Multiplatform için JetBrains Space Maven
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

dependencyResolutionManagement {
    repositories {
        // Proje bağımlılıklarının çözümleneceği kaynaklar
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

// Kök projenin adını belirtiyoruz
rootProject.name = "LoginMultiplatform"

// Proje modüllerini ekliyoruz
include(":androidApp")   // Android uygulaması modülü
include(":commonMain")   // Ortak multiplatform kodları içeren modül
include(":iosMain")      // iOS uygulaması modülü
