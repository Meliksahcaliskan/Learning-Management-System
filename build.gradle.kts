import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.konan.target.HostManager
plugins {
    kotlin("multiplatform") version "1.9.0"
    id("org.jetbrains.compose") version "1.5.0"
    id("com.android.application") version "8.1.1"
    //id("dev.icerock.mobile.multiplatform-resources") version "0.23.0"
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev") // JetBrains Compose repository'si
}

kotlin {
    androidTarget()

    if(HostManager.hostIsMac) {
        iosX64()
        iosArm64()
        iosSimulatorArm64()
    } else {
        println("Skipping iOS targets on non-macOS host")
    }
    

    sourceSets {
        val commonMain by getting {
            //kotlin.srcDir("build/generated/moko/commonMain/src")
            dependencies {
                implementation(kotlin("stdlib"))
                implementation("org.jetbrains.compose.runtime:runtime:1.5.0")
                implementation("org.jetbrains.compose.foundation:foundation:1.5.0")
                implementation("org.jetbrains.compose.material:material:1.5.0")
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.ui)
                //implementation("dev.icerock.moko:resources:0.23.0")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            //kotlin.srcDir("build/generated/moko/androidMain/src")

            dependencies {
                implementation("androidx.core:core-ktx:1.10.1")
                implementation("androidx.compose.ui:ui:1.5.0")
                implementation("androidx.compose.material:material:1.5.0")
                implementation("androidx.compose.ui:ui-tooling-preview:1.5.0")
                implementation("androidx.activity:activity-compose:1.7.2")
                implementation("com.google.android.material:material:1.9.0")
                /*implementation("dev.icerock.moko:resources-compose:0.23.0")
                implementation("dev.icerock.moko:resources:0.23.0")*/
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                //kotlin.srcDirs("build/generated/moko/androidMain/src")
            }
        }
        val androidUnitTest by getting {
            dependencies {
                implementation("junit:junit:4.13.2")
                implementation(kotlin("test-junit"))
                implementation("androidx.test.espresso:espresso-core:3.5.1")
            }
        }
        /*val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
            dependencies {
                implementation("dev.icerock.moko:resources:0.20.1")
            }
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }*/

        androidUnitTest.dependsOn(commonTest)
        //iosTest.dependsOn(commonTest)
    }
}

android {
    namespace = "com.example.loginmultiplatform"
    compileSdk = 34
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        applicationId = "com.example.loginmultiplatform"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
    }
}

/*multiplatformResources {
    multiplatformResourcesPackage = "com.example.loginmultiplatform" // Use your project's package name here
    //useExperimentalPlugin = true
    //disableStaticFrameworkWarning = true
    //disable("iosX64", "iosArm64", "iosSimulatorArm64")
}*/
