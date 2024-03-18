import java.util.Properties


plugins {
    id("com.android.application")
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

        val postgresPass = properties.getProperty("POSTGRES_PASS") ?: ""
        val postgresUser = properties.getProperty("POSTGRES_USER") ?: ""
        val postgresHost = properties.getProperty("POSTGRES_HOST") ?: ""
        val postgresPort = properties.getProperty("POSTGRES_PORT") ?: ""

        val weatherUrl = properties.getProperty("WEATHER_API_URL") ?: ""
        val weatherKey = properties.getProperty("WEATHER_API_KEY") ?: ""
        val chatGptUrl = properties.getProperty("CHATGPT_API_URL") ?: ""
        val chatGptKey = properties.getProperty("CHATGPT_API_KEY") ?: ""

//        WEATHER_API_URL="https://api.openweathermap.org/data/2.5/weather"
//        WEATHER_API_KEY="8474ee05487a0d67588216334a9cc992"
//        CHATGPT_API_URL="https://api.openai.com/v1/chat/completions"
//        CHATGPT_API_KEY="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"

        buildConfigField(
                type = "String",
                name = "POSTGRES_PASS",
                value = postgresPass
        )
        buildConfigField(
                type = "String",
                name = "POSTGRES_USER",
                value = postgresUser
        )
        buildConfigField(
                type = "String",
                name = "POSTGRES_HOST",
                value = postgresHost
        )
        buildConfigField(
                type = "String",
                name = "POSTGRES_PORT",
                value = postgresPort
        )

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
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.navigation:navigation-fragment:2.6.0")
    implementation("androidx.navigation:navigation-ui:2.6.0")
    implementation("com.google.code.gson:gson:2.10")
    implementation("org.postgresql:postgresql:42.2.5")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}