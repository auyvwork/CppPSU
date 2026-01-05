plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    // Добавлен необходимый плагин для Kotlin 2.0+
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0"
}

android {
    namespace = "com.example.cpppsu"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.cpppsu"
        minSdk = 24
        targetSdk = 36
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

    // Настройка пути к вашему CMakeLists.txt [cite: 1, 3]
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }

    buildFeatures {
        // Включаем поддержку как ViewBinding, так и Compose [cite: 11]
        viewBinding = true
        compose = true
    }

    // Внимание: блок composeOptions удален, так как используется плагин выше
}

dependencies {
    // Стандартные библиотеки из вашего проекта
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.ui.graphics)

    // Библиотеки для Jetpack Compose (нужны для UI фракталов)
    val composeBom = platform("androidx.compose:compose-bom:2024.02.00")
    implementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.activity:activity-compose")

    // Тестирование
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.7.0")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.7.0")
    debugImplementation("androidx.compose.ui:ui-tooling")
}