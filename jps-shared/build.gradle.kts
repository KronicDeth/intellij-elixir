plugins {
    java
}

tasks.jar {
    archiveFileName.set("jps-shared.jar")
}

tasks.withType<Test> {
    enabled = false
}