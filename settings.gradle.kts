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
include(":androidMain")   // Android uygulaması modülü
project(":androidMain").projectDir = file("src/androidMain")
/*include(":androidApp")
project(":androidApp").projectDir = file("src/androidApp")*/
include(":commonMain")   // Ortak multiplatform kodları içeren modül
project(":commonMain").projectDir = file("src/commonMain")
include(":iosMain")      // iOS uygulaması modülü
project(":iosMain").projectDir = file("src/iosMain")
include(":backend")
project(":backend").projectDir = file("src/backend")
