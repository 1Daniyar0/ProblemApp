plugins {
    id("problem.android.library")
    id("problem.android.compose")
}

android {
    namespace = "com.example.problemapp.core.ui"
}

dependencies {
    implementation(libs.androidx.core.ktx)
}