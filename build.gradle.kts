plugins {
    java
    application
}

group = "com.example"
version = "1.0.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

sourceSets {
    main {
        java {
            setSrcDirs(listOf("DVAApp/src", "DVACommon/src", "PlasmaCommon/src", "Core/src"))
            exclude("jb/common/nativefilechooser/**")
        }
        resources {
            setSrcDirs(emptyList<String>())
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.microsoft.azure:azure-storage:2.2.0")
    implementation("com.microsoft.azure:azure-core:0.7.0")
    implementation("com.google.protobuf:protobuf-java:3.19.1")
    implementation("net.java.dev.jna:jna:5.13.0")
    implementation("net.java.dev.jna:jna-platform:5.13.0")
    implementation("com.lmax:disruptor:3.4.4")
    implementation("com.formdev:flatlaf:3.0")
    implementation("com.formdev:flatlaf-extras:3.0")
    implementation("com.formdev:svgSalamander:1.1.4")
    implementation("org.apache.logging.log4j:log4j-api:2.20.0")
    implementation("org.apache.logging.log4j:log4j-core:2.20.0")
    implementation("org.javatuples:javatuples:1.2")
    implementation("com.googlecode.soundlibs:mp3spi:1.9.5.4")
    implementation("com.googlecode.soundlibs:jlayer:1.0.1.4")
    implementation("org.swixml:swixml:1.5.144")
}

application {
    mainClass.set("jb.dvacommon.DVA")
}

tasks.processResources {
    from("DVAApp/src") {
        include("**/*.xml", "**/*.properties", "**/*.png", "**/*.svg")
    }
    from("Core/src") {
        include("**/*.xml", "**/*.properties")
    }
    from("DVAApp/resources") {
        include("**")
    }
}

tasks.compileJava {
    options.compilerArgs.addAll(listOf("-Xlint:unchecked", "-Xlint:deprecation"))
}

tasks.jar {
    entryCompression = ZipEntryCompression.STORED
    manifest {
        attributes(
            "Main-Class" to "jb.dvacommon.DVA",
            "Class-Path" to configurations.runtimeClasspath.get().files.joinToString(" ") { it.name }
        )
    }
}
