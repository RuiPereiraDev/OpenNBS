plugins {
    id("maven-publish")
    alias(libs.plugins.kotlin)
    alias(libs.plugins.dokka)
    alias(libs.plugins.kover)
}

group = "dev.ruipereira.opennbs"
version = "1.0.1-dev"

dependencies {
    testImplementation(kotlin("test"))
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = "opennbs"
            from(components["java"])

            pom {
                name = "OpenNBS"
                description = "A Kotlin library for reading and writing Note Block Song (.nbs) files."
                url = "https://github.com/RuiPereiraDev/OpenNBS"
                inceptionYear = "2026"

                licenses {
                    license {
                        name = "MIT License"
                        url = "https://opensource.org/licenses/MIT"
                        distribution = "https://opensource.org/licenses/MIT"
                    }
                }

                developers {
                    developer {
                        name = "Rui Pereira"
                        email = "me@ruipereira.dev"
                    }
                }

                scm {
                    url = "https://github.com/RuiPereiraDev/OpenNBS"
                    connection = "scm:git:git://github.com/RuiPereiraDev/OpenNBS.git"
                    developerConnection = "scm:git:ssh://github.com:RuiPereiraDev/OpenNBS.git"
                }
            }
        }
    }
}

java {
    withSourcesJar()
}

kotlin {
    explicitApi()

    jvmToolchain(8)
}
