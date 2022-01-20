plugins {
    kotlin("multiplatform")
    id("maven")
}

kotlin {
    jvm {
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":base"))
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



