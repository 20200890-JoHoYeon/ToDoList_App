plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.ksp) // KSP 플러그인 적용 (alias 사용)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.hottak.todoList"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.hottak.todoList"
        minSdk = 24
        targetSdk = 35
        versionCode = 6
        versionName = "3.2"

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
        compose = true
    }
}

dependencies {

    implementation("com.google.firebase:firebase-firestore-ktx:25.1.2")
    implementation("com.google.android.gms:play-services-auth:18.4.0")
    implementation(platform(libs.firebase.bom))
    // Firebase 라이브러리들은 BoM을 사용하므로 버전을 명시하지 않습니다.
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth.ktx) // 버전 제거
    // Google Play Services Auth는 BoM의 영향을 받지 않으므로 버전 명시 가능
    implementation(libs.play.services.auth)
    //androidx.multidex 버전 명시 가능
    implementation(libs.androidx.multidex)
    implementation (libs.androidx.foundation)
    implementation (libs.material3)
    implementation(libs.kotlin.stdlib.jdk8)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.google.firebase.auth.ktx)
    implementation(libs.androidx.media3.common.ktx)
    implementation(libs.firebase.firestore.ktx)
    ksp(libs.symbol.processing.api)
    ksp(libs.androidx.room.common)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.room.common)
    implementation(libs.androidx.room.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}