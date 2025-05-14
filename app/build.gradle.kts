plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.healthmonitor"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.healthmonitor"
        minSdk = 21
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
}

dependencies {
    // Основные зависимости
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation("androidx.core:core-splashscreen:1.0.0")
    implementation ("androidx.viewpager2:viewpager2:1.0.0")
    // RecyclerView и CardView
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.cardview:cardview:1.0.0")

    // ViewPager2
    implementation("androidx.viewpager2:viewpager2:1.0.0")

    // MPAndroidChart для графиков
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // Lifecycle компоненты
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime:2.7.0")

    // Fragment
    implementation("androidx.fragment:fragment:1.6.2")

    // Тестирование
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
