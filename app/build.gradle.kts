import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    val keystoreProperties = Properties().apply {
        load(File(rootProject.rootDir, "keystore.properties").inputStream())
    }

    namespace = "com.panopoker"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.panopoker"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            keyAlias = keystoreProperties["KEY_ALIAS"] as String
            keyPassword = keystoreProperties["KEY_PASSWORD"] as String
            storeFile = file(keystoreProperties["STORE_FILE"] as String)
            storePassword = keystoreProperties["STORE_PASSWORD"] as String
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }

    // Mágica de renomear o APK
    android.applicationVariants.all {
        if (buildType.name == "release") {
            outputs.all {
                val outputImpl = this as? com.android.build.gradle.internal.api.BaseVariantOutputImpl
                outputImpl?.outputFileName = "PanoPoker-v${versionName}.apk"
            }
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

    implementation ("androidx.media:media:1.2.1")

    implementation("com.google.accompanist:accompanist-flowlayout:0.30.1")
    implementation ("androidx.compose.foundation:foundation:1.5.0") // ou superior

    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    implementation("io.coil-kt:coil-compose:2.5.0") // ou versão atual

    // google oauth
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Compose core
    implementation("androidx.compose.ui:ui:1.5.1")
    implementation("androidx.compose.ui:ui-graphics:1.5.1")
    implementation("androidx.compose.ui:ui-tooling-preview:1.5.1")
    implementation("androidx.compose.ui:ui-util:1.5.1")
    implementation("androidx.compose.foundation:foundation:1.5.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation(libs.androidx.material3)

    // Outros essenciais
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Testes e Preview
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.5.1")
    debugImplementation("androidx.compose.ui:ui-tooling:1.5.1")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.5.1")
}
