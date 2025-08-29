plugins {
    alias(libs.plugins.android.application)

}

android {
    namespace = "com.android.mygrade"
    compileSdk = 36
    packagingOptions {
        exclude("META-INF/INDEX.LIST")
        exclude("META-INF/DEPENDENCIES")
    }
    defaultConfig {
        applicationId = "com.android.mygrade"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "0.0.1 - alpha"

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

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation("com.google.api-client:google-api-client-android:1.33.0")
    implementation("com.google.apis:google-api-services-sheets:v4-rev20250616-2.0.0")
    implementation("com.google.http-client:google-http-client-android:1.39.2")
    implementation("com.google.http-client:google-http-client-gson:1.39.2")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.0")
    implementation ("androidx.work:work-runtime-ktx:2.8.1")
    implementation(libs.navigation.fragment)
    annotationProcessor("androidx.room:room-compiler:2.6.1")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation(libs.constraintlayout)
    implementation("androidx.room:room-runtime:2.6.1")
    implementation(libs.room.runtime.android)
    implementation(libs.volley)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}