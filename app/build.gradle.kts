plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android) version "2.1.0"
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.connectme"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.connectme"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    // JUnit rules & runner
    androidTestImplementation ("androidx.test:runner:1.4.0")
    androidTestImplementation ("androidx.test.ext:junit:1.1.3")

    // Espresso core
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.4.0")

    // If you need Intents verification
    androidTestImplementation ("androidx.test.espresso:espresso-intents:3.4.0")
    implementation ("io.agora.rtc:full-sdk:4.5.1")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database.ktx)
    implementation(libs.firebase.database)
    implementation(libs.firebase.storage.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}