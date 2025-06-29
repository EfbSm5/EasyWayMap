plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.21"
    alias(libs.plugins.ksp)
}


android {
    namespace = "com.efbsm5.easyway"
    compileSdk = 36
    buildFeatures {
        compose = true
    }
    defaultConfig {
        applicationId = "com.efbsm5.easyway"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.6.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
        getByName("debug") {
            buildConfigField("String", "BASE_URL", "\"https://egret-knowing-chimp.ngrok-free.app\"")
            buildConfigField("boolean", "IS_LOG_ENABLED", "true")
        }
        getByName("release") {
            buildConfigField("String", "BASE_URL", "\"http://47.122.123.42:5000/\"")
            buildConfigField("boolean", "IS_LOG_ENABLED", "false")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_19
        targetCompatibility = JavaVersion.VERSION_19
    }
    kotlinOptions {
        jvmTarget = "19"
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.androidx.material3)
    implementation("io.github.TheMelody:gd_compose:1.0.7")
    debugImplementation(libs.leakcanary.android)
    implementation(platform(libs.compose.bom))
    api(libs.androidx.runtime)
    api(libs.core.ktx)
    api(libs.lifecycle.runtime.ktx)
    api(libs.lifecycle.viewmodel.compose)
    api(libs.activity.compose)
    api(libs.accompanist.permissions)
    api(libs.startup.runtime)
    api(libs.compose.ui)
    api(libs.compose.ui.tooling.preview)
    api(libs.foundation)
    api(libs.accompanist.flowlayout)
    implementation("com.google.android.material:material:1.12.0")
    implementation(libs.androidx.room.runtime)
    implementation(libs.gson)
    implementation(libs.coil.compose)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.androidx.navigation.compose)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.accompanist.permissions)

    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    testImplementation(libs.junit)


}