import java.sql.*

buildscript {
    dependencies {
        classpath("org.postgresql:postgresql:42.7.3")
    }
}

plugins {
	kotlin("jvm") version "2.2.0"
	kotlin("plugin.spring") version "2.2.0"
	id("org.springframework.boot") version "4.0.0-SNAPSHOT"
	id("io.spring.dependency-management") version "1.1.7"
	kotlin("plugin.jpa") version "2.2.0"
	id("org.liquibase.gradle") version "2.2.2"
}

group = "com.foodblr"
version = "0.0.1-SNAPSHOT"
description = "Personal Project to make it easier to find recipes"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
	maven { url = uri("https://repo.spring.io/snapshot") }
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.liquibase:liquibase-core")
	runtimeOnly("org.postgresql:postgresql")
	runtimeOnly("com.h2database:h2") // Add H2 for development
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("org.springframework.security:spring-security-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	// Liquibase runtime dependencies
	liquibaseRuntime("org.liquibase:liquibase-core")
	liquibaseRuntime("org.postgresql:postgresql:42.7.3")
	liquibaseRuntime("info.picocli:picocli:4.7.6")
}

// Local development database configuration
val localDevJdbcUrl = "jdbc:postgresql://localhost:5432"
val localDevJdbcUser = "postgres"
val localDevJdbcPassword = ""
val localDevJdbcBootstrapDb = "postgres"
val localDevJdbcDevDb = "foodblr"
val localDevJdbcTestDb = "foodblr_test"

// Liquibase configuration for development database
liquibase {
    activities.register("dev") {
        val args = mutableMapOf(
            "changelogFile" to "db/changelog/db.changelog-master.xml",
            "url" to "$localDevJdbcUrl/$localDevJdbcDevDb",
            "username" to localDevJdbcUser,
            "logLevel" to "info",
            "searchPath" to "${project.projectDir.absolutePath}/src/main/resources"
        )
        if (localDevJdbcPassword.isNotEmpty()) {
            args["password"] = localDevJdbcPassword
        }
        this.arguments = args
    }
    activities.register("test") {
        val args = mutableMapOf(
            "changelogFile" to "db/changelog/db.changelog-master.xml",
            "url" to "$localDevJdbcUrl/$localDevJdbcTestDb",
            "username" to localDevJdbcUser,
            "logLevel" to "info",
            "searchPath" to "${project.projectDir.absolutePath}/src/main/resources"
        )
        if (localDevJdbcPassword.isNotEmpty()) {
            args["password"] = localDevJdbcPassword
        }
        this.arguments = args
    }
    runList = "dev"
}

// Tasks to create development and test databases using JDBC
val createDatabases by tasks.registering {
    description = "Creates development database using JDBC"
    group = "database"
    doLast {
        createDatabaseIfNotExists(localDevJdbcDevDb)
    }
}

val createTestDatabase by tasks.registering {
    description = "Creates test database using JDBC"
    group = "database"
    doLast {
        createDatabaseIfNotExists(localDevJdbcTestDb)
    }
}

val migrateDevDatabase by tasks.registering {
    description = "Migrates development database"
    group = "database"
    dependsOn(createDatabases)
    dependsOn("update")
}

val migrateTestDatabase by tasks.registering {
    description = "Migrates test database"
    group = "database"
    dependsOn(createTestDatabase)
}

val setupDatabases by tasks.registering {
    description = "Creates and migrates both development and test databases"
    group = "database"
    dependsOn(migrateDevDatabase, migrateTestDatabase)
}

fun createDatabaseIfNotExists(databaseName: String) {
    val jdbcUrl = "$localDevJdbcUrl/$localDevJdbcBootstrapDb"

    try {
        // Load the PostgreSQL driver
        Class.forName("org.postgresql.Driver")

        DriverManager.getConnection(
            jdbcUrl,
            localDevJdbcUser,
            localDevJdbcPassword
        ).use { connection ->
            connection.createStatement().use { statement ->
                // Check if database exists
                val resultSet = statement.executeQuery(
                    "SELECT 1 FROM pg_database WHERE datname = '$databaseName'"
                )

                if (!resultSet.next()) {
                    // Database doesn't exist, create it
                    statement.executeUpdate("CREATE DATABASE \"$databaseName\"")
                    println("Created database: $databaseName")
                } else {
                    println("Database already exists: $databaseName")
                }
            }
        }
    } catch (e: Exception) {
        println("Warning: Could not create database $databaseName: ${e.message}")
        println("Make sure PostgreSQL is running and accessible at $jdbcUrl")
    }
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
	}
}

allOpen {
	annotation("jakarta.persistence.Entity")
	annotation("jakarta.persistence.MappedSuperclass")
	annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
