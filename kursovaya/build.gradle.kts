plugins {
    java
    application
    id("org.javamodularity.moduleplugin") version "1.8.15"
    id("org.openjfx.javafxplugin") version "0.0.13"
    id("org.beryx.jlink") version "2.25.0"
}

group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val junitVersion = "5.12.1"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

application {
    mainModule.set("spbmetro.main")
    mainClass.set("com.example.kursovaya.model.AppMain")
}

javafx {
    version = "21.0.6"
    modules = listOf("javafx.controls", "javafx.fxml", "javafx.graphics")
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.0")

    testImplementation("org.junit.jupiter:junit-jupiter-api:${junitVersion}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${junitVersion}")

    implementation("org.apache.logging.log4j:log4j-core:2.20.0")
    implementation("org.apache.logging.log4j:log4j-api:2.20.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

jlink {
    options.set(listOf("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages"))

    launcher {
        name = "SpbMetro"
        jvmArgs = listOf(
            "-Dfile.encoding=UTF-8",
            "-Dprism.order=sw"
        )
    }

    // Добавляем JavaFX модули
    addExtraDependencies("javafx")
}

// Задача для запуска из Gradle
tasks.named<JavaExec>("run") {
    jvmArgs = listOf(
        "-Dfile.encoding=UTF-8",
        "--add-opens", "spbmetro.main/com.example.kursovaya.model=ALL-UNNAMED"
    )
}

tasks.register<Jar>("fatJar") {
    group = "build"
    description = "Creates a fat JAR with all dependencies including JavaFX"

    dependsOn.addAll(listOf("compileJava", "processResources", "classes"))

    archiveBaseName.set("spbmetro")
    archiveVersion.set("1.0")
    archiveClassifier.set("fat")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    manifest {
        attributes(
            "Main-Class" to "com.example.kursovaya.model.AppMain", // Используем AppMain напрямую
            "Implementation-Title" to "SpbMetro",
            "Implementation-Version" to project.version,
            "Multi-Release" to "true",
            "Created-By" to "Gradle ${gradle.gradleVersion}",
            "Build-Jdk" to "${System.getProperty("java.version")}"
        )
    }

    val sourcesMain = sourceSets.main.get()
    val contents = configurations.runtimeClasspath.get()
        .map { if (it.isDirectory) it else zipTree(it) } +
            sourcesMain.output

    from(contents)

    // Исключаем ненужные файлы
    exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")
    exclude("META-INF/NOTICE*", "META-INF/LICENSE*", "META-INF/INDEX.LIST")
    exclude("module-info.class")

    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}