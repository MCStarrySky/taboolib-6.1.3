import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

dependencies {
    implementation("org.tabooproject.reflex:analyser:1.0.8")
    implementation("org.tabooproject.reflex:reflex:1.0.8")
    implementation("org.tabooproject.reflex:fast-instance-getter:1.0.8")
    // Test
    testImplementation(project(":common-core-impl"))
    testImplementation(project(":common-environment"))
}

shrinking {
    shadow = true
}

tasks {
    withType<ShadowJar> {
        dependencies {
            include(dependency("org.tabooproject.reflex:analyser:1.0.8"))
            include(dependency("org.tabooproject.reflex:reflex:1.0.8"))
            include(dependency("org.tabooproject.reflex:fast-instance-getter:1.0.8"))
        }
    }
    build {
        dependsOn("shadowJar")
    }
}