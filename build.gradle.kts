plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.dokka)
    alias(libs.plugins.kover)
}

group = "dev.ruipereira.opennbs"
version = "1.0.0-dev"

kotlin {
    explicitApi()

    jvmToolchain(8)
}

dependencies {
    testImplementation(kotlin("test"))
}
