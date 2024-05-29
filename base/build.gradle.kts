plugins {
    kotlin("multiplatform")
    id("maven-publish")
}

kotlin {
    applyDefaultHierarchyTemplate()

    jvm()
    mingwX64()
    linuxX64()

    sourceSets {
        val commonMain by getting {}
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(project(":laws"))
            }
        }
        val jvmMain by getting {}
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(project(":laws"))
            }
        }
        val nativeMain by getting {}
        val mingwX64Main by getting {}
        val linuxX64Main by getting {}
    }
}




