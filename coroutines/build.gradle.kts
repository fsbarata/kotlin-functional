plugins {
	kotlin("multiplatform")
	id("maven-publish")
}

kotlin {
	jvm()
	mingwX64()
	linuxX64()

	sourceSets {
		val commonMain by getting {
			dependencies {
				implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
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
