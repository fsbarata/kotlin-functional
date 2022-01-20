plugins {
    kotlin("jvm")
    id("maven-publish")
}

dependencies {
    api(project(":base"))
    api("io.reactivex.rxjava3:rxjava:3.0.10")

    testImplementation(project(":laws"))
}



