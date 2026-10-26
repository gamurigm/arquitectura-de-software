plugins {
    id("com.android.application")
}

android {
    namespace = "ec.edu.monster.climov"
    compileSdk = 35

    defaultConfig {
        applicationId = "ec.edu.monster.climov"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

tasks.register<Exec>("installAndLaunchDebug") {
    group = "application"
    description = "Instala y abre la aplicación debug en el dispositivo Android conectado."
    dependsOn("installDebug")
    commandLine(
        file("${android.sdkDirectory}/platform-tools/adb.exe"),
        "shell", "monkey", "-p", android.defaultConfig.applicationId ?: android.namespace, "1"
    )
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
}
