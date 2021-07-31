plugins {
    `java-library`
    `maven-publish`
    checkstyle
    jacoco
    id("org.springframework.boot") version "2.5.3" apply false
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("com.github.spotbugs") version "4.7.2"
}

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "checkstyle")
    apply(plugin = "com.github.spotbugs")
    apply(plugin = "jacoco")

    group = "jp.gr.java_conf.stardiopside.silver.commons"
    version = "1.0.2-SNAPSHOT"

    repositories {
        mavenCentral()
    }

    dependencyManagement {
        imports {
            mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
        }
        dependencies {
            dependency("org.dbunit:dbunit:2.7.2")
        }
    }

    dependencies {
        compileOnly("org.projectlombok:lombok")
        annotationProcessor("org.projectlombok:lombok")
        testCompileOnly("org.projectlombok:lombok")
        testAnnotationProcessor("org.projectlombok:lombok")
    }

    java {
        withJavadocJar()
        withSourcesJar()
    }

    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["java"])
                versionMapping {
                    usage("java-api") {
                        fromResolutionOf("runtimeClasspath")
                    }
                    usage("java-runtime") {
                        fromResolutionResult()
                    }
                }
            }
        }
    }

    checkstyle {
        configFile = file("${rootDir}/config/checkstyle/checkstyle.xml")
        isIgnoreFailures = true
    }

    spotbugs {
        ignoreFailures.set(true)
    }

    tasks.compileJava {
        options.encoding = "UTF-8"
    }

    tasks.javadoc {
        options.encoding = "UTF-8"
    }

    tasks.spotbugsMain {
        reports {
            create("html")
        }
    }
}
