plugins {
    kotlin("multiplatform")
    id("maven-publish")
}

kotlin {
    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(project(":laws"))
            }
        }
    }
}




