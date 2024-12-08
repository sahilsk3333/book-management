plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.4.0"
	id("io.spring.dependency-management") version "1.1.6"
}

group = "me.sahil"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

dependencies {

	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")

	//test deps
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testImplementation("org.springframework.security:spring-security-test")


	// validation
	implementation("org.springframework.boot:spring-boot-starter-validation")

	// Spring Boot Starter for Security
	implementation("org.springframework.boot:spring-boot-starter-security")

	// Spring Boot Starter for JPA
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")

	implementation("io.jsonwebtoken:jjwt-api:0.11.5") // JWT API
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5") // JWT Implementation
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5") // JSON Processing for JWT

	// PostgreSQL Driver
	runtimeOnly("org.postgresql:postgresql")

	// BCrypt Password Encoder
	implementation("org.springframework.security:spring-security-crypto")

}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
