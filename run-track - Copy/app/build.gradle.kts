plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin)
    alias(libs.plugins.maps.secrets)
    alias(libs.plugins.dagger.hilt.android)
    alias(libs.plugins.jetbrains.kotlin.kapt)
}

android {
    namespace = "com.sdevprem.runtrack"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.sdevprem.runtrack"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)

    // Compose
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material)

    // Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // Kotlin
    implementation(platform(libs.kotlin.bom))

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Maps
    implementation(libs.maps.compose)
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)
    implementation(libs.play.services.auth)


    // Hilt
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // Lifecycle
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.service)

    // Timber
    implementation(libs.timber)

    // Room
    implementation(libs.androidx.room.runtime)
    annotationProcessor(libs.androidx.room.compiler)
    kapt(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.paging)

    // Datastore
    implementation(libs.androidx.datastore.preferences)

    // Coil
    implementation(libs.coil.compose)

    // Paging
    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose)

    // Permission
    implementation(libs.accompanist.permissions)
}

kapt {
    correctErrorTypes = true
}
