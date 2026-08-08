import com.android.build.api.variant.VariantOutputConfiguration
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.serialization)
    alias(libs.plugins.ksp)
}

androidComponents {
    onVariants(selector().withBuildType("release")) { variant ->
        val mainOutput = variant.outputs.single { it.outputType == VariantOutputConfiguration.OutputType.SINGLE }

        @Suppress("UnstableApiUsage")
        mainOutput.outputFileName = "Chocola_${mainOutput.versionName.get()}.apk"
    }
}

android {
    namespace = "com.sosauce.chocola"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.sosauce.cutemusic"
        minSdk = 28
        targetSdk = 37
        versionCode = 50007
        versionName = "4.3.2"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        ndk {
            //noinspection ChromeOsAbiSupport
            abiFilters += arrayOf("arm64-v8a", "armeabi-v7a")
        }
    }

    signingConfigs {
        create("release") {

            val keystoreFile = file("release_key.jks")

            if (keystoreFile.exists()) {
                storeFile = keystoreFile
                storePassword = System.getenv("SIGNING_STORE_PASSWORD")
                keyAlias = System.getenv("SIGNING_KEY_ALIAS")
                keyPassword = System.getenv("SIGNING_KEY_PASSWORD")
            } else {
                println("No keystore found, APK will be unsigned")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.findByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "debug"
        }
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }
        kotlin {
            compilerOptions {
                jvmTarget = JvmTarget.JVM_17
            }
        }
        buildFeatures {
            compose = true
        }
        packaging {
            resources {
                excludes += "/META-INF/{AL2.0,LGPL2.1}"
            }
        }
        dependenciesInfo {
            includeInApk = false
            includeInBundle = false
        }
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.coil.compose)
    implementation(libs.androidx.media3.common)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.session)
    implementation(libs.androidx.media3.transformer)
    implementation(libs.androidx.media3.effect)
    implementation(libs.androidx.compose.animation)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
    implementation(libs.material.kolor)
    implementation(libs.koin.androidx.startup)
    implementation(libs.taglib)
    debugImplementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.emoji2.emojipicker)
    implementation(libs.kmpalette.core)
    implementation(libs.androidx.glance)
    implementation(libs.androidx.glance.appwidget)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.reorderable)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.haze)
    implementation(libs.haze.materials)
    implementation(libs.androidx.compose.animation.graphics.android)
    implementation(libs.lyrics.core)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.colorpicker.compose)
    implementation(libs.sweetselect.compose)
    implementation(libs.squircle.shape)
    implementation(libs.cloudy)
}

