tasks.jar {
    archiveFileName.set("jps-shared.jar")
}

tasks.testClasses {
    enabled = false
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}
