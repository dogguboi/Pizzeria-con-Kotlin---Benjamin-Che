plugins {
    kotlin("jvm") version "2.0.21"
    application
}

repositories {
    mavenCentral()
}

dependencies {
    // Necesaria para reflection (Paso 2): findAnnotation, declaredMemberProperties, etc.
    implementation(kotlin("reflect"))
}

application {
    mainClass.set("pizzeria.MainKt")
}
