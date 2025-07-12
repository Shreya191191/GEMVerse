plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "eu.tutorials.gemverse"
    compileSdk = 35

    defaultConfig {
        applicationId = "eu.tutorials.gemverse"
        minSdk = 24
        targetSdk = 35
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
        kotlinCompilerExtensionVersion = "1.4.7"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
  implementation("androidx.core:core-ktx:1.12.0")

    val composeBom = platform("androidx.compose:compose-bom:2024.05.00")
    implementation(composeBom)

    implementation("androidx.core:core-ktx:1.12.0")



    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")

    implementation("androidx.activity:activity-compose:1.10.1")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.ui:ui:1.6.1")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("androidx.compose.material:material:1.6.1")


    implementation("androidx.compose.runtime:runtime-livedata:1.6.1")


    implementation("androidx.navigation:navigation-compose:2.7.5")


    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")


    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-analytics")


    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")


    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")



    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.4")

    implementation("com.google.android.gms:play-services-auth:20.7.0")

    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")


    // Retrofit core
    implementation("com.squareup.retrofit2:retrofit:2.9.0")

// JSON converter using Gson
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

// OkHttp logging (optional, for debug logs)
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

}
