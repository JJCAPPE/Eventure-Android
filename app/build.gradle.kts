import java.util.regex.Pattern
import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") version "2.0.1"
}

android {
    namespace = "com.example.eventure"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.eventure"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        // Load API key from local.properties
        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localProperties.load(FileInputStream(localPropertiesFile))
            val apiKey = localProperties.getProperty("ticketmaster.api.key") ?: ""
            buildConfigField("String", "TICKETMASTER_API_KEY", "\"$apiKey\"")
        } else {
            buildConfigField("String", "TICKETMASTER_API_KEY", "\"\"")
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    
    // Retrofit for API calls
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    
    // Glide for image loading
    implementation("com.github.bumptech.glide:glide:4.16.0")
    
    // Google Play Services Location
    implementation("com.google.android.gms:play-services-location:21.0.1")
    
    // CardView
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}