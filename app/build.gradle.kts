import java.util.Properties


plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "dev.stylesync.stylesync"
    compileSdk = 34

    defaultConfig {
        applicationId = "dev.stylesync.stylesync"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        android.buildFeatures.buildConfig = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        val keystoreFile = project.rootProject.file("app/apikey.properties")
        val properties = Properties()
        properties.load(keystoreFile.inputStream())

        val postgresUrl = properties.getProperty("POSTGRES_URL") ?: ""

        val weatherUrl = properties.getProperty("WEATHER_API_URL") ?: ""
        val weatherKey = properties.getProperty("WEATHER_API_KEY") ?: ""
        val chatGptUrl = properties.getProperty("CHATGPT_API_URL") ?: ""
        val chatGptKey = properties.getProperty("CHATGPT_API_KEY") ?: ""
        val imgbbKey = properties.getProperty("IMGBB_API_KEY") ?: ""

        buildConfigField(
                type = "String",
                name = "WEATHER_API_URL",
                value = weatherUrl
        )
        buildConfigField(
                type = "String",
                name = "WEATHER_API_KEY",
                value = weatherKey
        )
        buildConfigField(
                type = "String",
                name = "CHATGPT_API_URL",
                value = chatGptUrl
        )
        buildConfigField(
                type = "String",
                name = "CHATGPT_API_KEY",
                value = chatGptKey
        )
        buildConfigField(
                type = "String",
                name = "IMGBB_API_KEY",
                value = imgbbKey
        )
        buildConfigField(
                type = "String",
                name = "POSTGRES_URL",
                value = postgresUrl
        )
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        buildConfig = true
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.navigation:navigation-fragment:2.7.7")
    implementation("androidx.navigation:navigation-ui:2.7.7")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.postgresql:postgresql:42.2.5")
    implementation(platform("com.google.firebase:firebase-bom:32.8.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.firebaseui:firebase-ui-auth:7.2.0")
    implementation("androidx.annotation:annotation:1.7.1")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("com.android.volley:volley:1.2.1")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}