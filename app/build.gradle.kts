plugins {
    id("problem.android.application")
    id("problem.android.compose")
}

android {
    namespace = "com.example.problemapp"

    defaultConfig {
        applicationId = "com.example.problemapp"
        versionCode = 1
        versionName = "1.0"
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
}

dependencies {
    // Модули проекта
    implementation(projects.coreUi)
    implementation(projects.featureProfilerTest)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
}