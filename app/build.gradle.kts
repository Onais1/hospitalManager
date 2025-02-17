plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.loginencryptionapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.loginencryptionapp"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Android Core Libraries
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    // Unit Testing (JUnit, Mockito)
    testImplementation(libs.junit)
    testImplementation("org.mockito:mockito-core:4.8.0")

    // UI Testing (Espresso)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("org.mockito:mockito-android:4.8.0")

    // RecyclerView actions and contrib (needed for RecyclerView testing)
    androidTestImplementation ("androidx.test.espresso:espresso-contrib:3.5.1")

    // Optional: Bouncy Castle for cryptography (Uncomment if needed)
    // implementation("org.bouncycastle:bcprov-jdk15to18:1.68")
}
