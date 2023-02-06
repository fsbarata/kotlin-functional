plugins {
	kotlin("jvm")
	id("maven-publish")
}

dependencies {
	api(project(":base"))
	api("io.reactivex.rxjava3:rxjava:3.1.6")

	testImplementation(project(":laws"))
	testImplementation("junit:junit:4.13.2")
}

java {
	withJavadocJar()
	withSourcesJar()
}

publishing {
	publications {
		create<MavenPublication>("maven") {
			from(components["java"])
		}
	}
}

