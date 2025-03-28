
val kotlin_version: String by project
val logback_version: String by project

plugins {
    kotlin("jvm") version "2.1.20"
    kotlin("plugin.serialization") version "1.8.20"
    id("io.ktor.plugin") version "3.1.1"
}





group = "com.example.com"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    // Ktor server dependencies
    implementation("io.ktor:ktor-server-core:2.3.1")
    implementation("io.ktor:ktor-server-netty:2.3.1")
    implementation("io.ktor:ktor-server-content-negotiation:2.3.1")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.1")

    // Logging
    implementation("ch.qos.logback:logback-classic:1.4.8")

    // PostgreSQL JDBC Driver
    implementation("org.postgresql:postgresql:42.6.0")

    // HikariCP for connection pooling
    implementation("com.zaxxer:HikariCP:6.2.1")

    // Exposed ORM for database interaction
    implementation("org.jetbrains.exposed:exposed-core:0.41.1")
    implementation("org.jetbrains.exposed:exposed-dao:0.41.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.41.1")

    // Supabase client libraries
    implementation("io.github.jan-tennert.supabase:postgrest-kt:0.9.0") // For database queries
    implementation("io.github.jan-tennert.supabase:storage-kt:0.9.0")   // For file storage
    implementation("io.github.jan-tennert.supabase:gotrue-kt:0.9.0")    // For authentication

    // Ktor client dependencies
    implementation("io.ktor:ktor-client-core:2.3.1")
    implementation("io.ktor:ktor-client-cio:2.3.1")
    implementation("io.ktor:ktor-client-serialization:2.3.1")

    // Test dependencies
    testImplementation("io.ktor:ktor-server-tests-jvm:2.3.1")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
    implementation("io.ktor:ktor-server-config-yaml:3.1.1")
}


