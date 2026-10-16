plugins {
    id("problem.android.library")
    id("problem.android.compose")
}

android {
    namespace = "com.example.problemapp.feature.profilertest"
}

dependencies {
    implementation(projects.coreUi)

    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.android)
}