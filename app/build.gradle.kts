plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.example.trello"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.trello"
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
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    val room_version = "2.6.1"
    var lottieVersion = "6.4.0"
    var compose_version = "1.5.3"

    implementation ("com.google.android.material:material:1.6.0")
    implementation ("androidx.core:core-splashscreen:1.0.0")
    implementation ("androidx.core:core-ktx:1.12.0")
    implementation("androidx.room:room-runtime:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version")
    implementation("com.airbnb.android:lottie:$lottieVersion")

    implementation ("androidx.core:core-ktx:1.12.0")
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation ("androidx.activity:activity-compose:1.8.1")

    implementation ("androidx.compose.material3:material3:1.2.1-alpha06")
    implementation ("androidx.compose.ui:ui:$compose_version")
    implementation ("androidx.compose.ui:ui-tooling-preview:$compose_version")
    debugImplementation ("androidx.compose.ui:ui-tooling:$compose_version")
    debugImplementation ("androidx.compose.ui:ui-test-manifest:$compose_version")
    implementation ("com.airbnb.android:lottie-compose:$lottieVersion")
    implementation ("androidx.navigation:navigation-compose:2.7.5")
    implementation("androidx.compose.material3:material3-window-size-class:1.2.1")
    implementation("androidx.compose.material3:material3-adaptive-navigation-suite:1.3.0-beta01")
    implementation ("androidx.compose.material:material:1.4.3")
    implementation ("androidx.compose.ui:ui:1.4.3")
    implementation ("androidx.compose.runtime:runtime-livedata:1.4.3")
    implementation ("androidx.activity:activity-compose:1.7.2")
    implementation ("androidx.datastore:datastore-preferences:1.0.0")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}