plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("dagger.hilt.android.plugin")
    kotlin("kapt")
}

android {
    namespace = "com.example.productivityapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.productivityapp"
        minSdk = ProjectConfig.minSdk
        targetSdk = ProjectConfig.targetSdk
        versionCode = ProjectConfig.versionCode
        versionName = ProjectConfig.versionName

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
        sourceCompatibility = JavaVersion.VERSION_18
        targetCompatibility = JavaVersion.VERSION_18
    }
    kotlinOptions {
        jvmTarget = "18"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Versions.composeCompiler
    }
}

hilt {
    enableAggregatingTask = false
}

dependencies {

    implementation(project(":core:data:network"))
    implementation(project(":core:data:data_store"))
    implementation(project(":feature:auth:sign_in"))
    implementation(project(":feature:auth:sign_up"))
    implementation(project(":feature:user:user_app_statistics"))
    implementation(project(":feature:user:profile"))
    implementation(project(":feature:user:chat_list"))
    implementation(project(":feature:user:app_blocker"))
    implementation(project(":feature:user:blocked_app_list"))
    implementation(project(":feature:chat"))
    implementation(project(":feature:doctor:profile"))
    implementation(project(":feature:doctor:patient_list"))
    implementation(project(":feature:doctor:patient_statistics"))
    implementation(project(":core:util"))

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.7.2")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    //navigation
    implementation("androidx.navigation:navigation-compose:2.5.3")

    compose()
    room()
    retrofit()
    hilt()
    dataStore()

}