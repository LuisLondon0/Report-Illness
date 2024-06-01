plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.example.report_illness"
    compileSdk = 34

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.example.report_illness"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val dbHost: String = System.getenv("DB_HOST") ?: throw GradleException("DB_HOST environment variable not set.")
        val dbName: String = System.getenv("DB_NAME") ?: throw GradleException("DB_NAME environment variable not set.")
        val dbUser: String = System.getenv("DB_USER") ?: throw GradleException("DB_USER environment variable not set.")
        val dbPassword: String = System.getenv("DB_PASSWORD") ?: throw GradleException("DB_PASSWORD environment variable not set.")

        buildConfigField("String", "DB_HOST", "\"$dbHost\"")
        buildConfigField("String", "DB_NAME", "\"$dbName\"")
        buildConfigField("String", "DB_USER", "\"$dbUser\"")
        buildConfigField("String", "DB_PASSWORD", "\"$dbPassword\"")
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(files("libs/jtds-1.3.1.jar"))
    implementation(libs.androidx.activity)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}