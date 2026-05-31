plugins {
    application
}

dependencies {
    implementation(project(":orm"))
    implementation("com.h2database:h2:2.2.224")
}

application {
    mainClass.set("Main")
}
