plugins {
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    // Coroutines
    implementation(libs.coroutines.core)

    // Javax Inject for @Inject annotation (Hilt-compatible without Android dependency)
    implementation(libs.javax.inject)

    // Testing
    testImplementation(libs.junit)
}
