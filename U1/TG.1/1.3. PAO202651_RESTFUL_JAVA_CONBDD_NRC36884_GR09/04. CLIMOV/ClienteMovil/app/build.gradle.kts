import java.util.concurrent.TimeUnit

plugins { id("com.android.application") }

val apiBase = providers.gradleProperty("EUREKABANK_REST_URL")
    .orElse(providers.environmentVariable("EUREKABANK_REST_URL"))
    .orElse("http://10.0.2.2:8080/WS_EurekaBank_REST_JAVA/api/eureka")
    .get().replace("\\", "\\\\").replace("\"", "\\\"")

android {
    namespace = "ec.edu.monster"
    compileSdk = 35
    defaultConfig {
        applicationId = "ec.edu.monster.eurekabank.rest"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        buildConfigField("String", "API_BASE_URL", "\"$apiBase\"")
    }
    buildFeatures { buildConfig = true }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

val sdk = android.sdkDirectory
val adb = File(sdk, "platform-tools/adb.exe")
val emulator = File(sdk, "emulator/emulator.exe")
val avdHome = File(sdk, "avd")
val emulatorLog = File(sdk, "logs/eurekabank-emulator.log")
val sdkEnvironment = mapOf(
    "ANDROID_HOME" to sdk.absolutePath,
    "ANDROID_SDK_ROOT" to sdk.absolutePath,
    "ANDROID_AVD_HOME" to avdHome.absolutePath
)

fun runAndroidCommand(vararg arguments: String): String {
    val process = ProcessBuilder(*arguments).redirectErrorStream(true).apply {
        environment().putAll(sdkEnvironment)
    }.start()
    if (!process.waitFor(15, TimeUnit.SECONDS)) {
        process.destroyForcibly()
        throw GradleException("Tiempo agotado al ejecutar ${arguments.first()}.")
    }
    val output = process.inputStream.bufferedReader().readText()
    if (process.exitValue() != 0) throw GradleException(output.trim())
    return output
}

tasks.matching { it.name == "installDebug" }.configureEach {
    doFirst {
        val devicePattern = Regex("(?m)^(\\S+)\\s+device\\b")
        var device = devicePattern.find(runAndroidCommand(adb.absolutePath, "devices"))?.groupValues?.get(1)
        if (device == null) {
            if (!emulator.isFile) throw GradleException("Falta el emulador Android en ${emulator.parentFile}.")
            val avd = runAndroidCommand(emulator.absolutePath, "-list-avds").lineSequence()
                .map(String::trim).firstOrNull(String::isNotEmpty)
                ?: throw GradleException("No hay AVD. Cree uno Android 35 Google APIs x86_64 y vuelva a ejecutar Run.")
            emulatorLog.parentFile.mkdirs()
            ProcessBuilder(emulator.absolutePath, "-avd", avd, "-no-snapshot-save")
                .redirectErrorStream(true).redirectOutput(emulatorLog).apply { environment().putAll(sdkEnvironment) }.start()
        }
        val deadline = System.nanoTime() + TimeUnit.MINUTES.toNanos(3)
        var booted = false
        while (!booted && System.nanoTime() < deadline) {
            device = devicePattern.find(runAndroidCommand(adb.absolutePath, "devices"))?.groupValues?.get(1)
            val currentDevice = device
            if (currentDevice != null) booted = runAndroidCommand(adb.absolutePath, "-s", currentDevice, "shell", "getprop", "sys.boot_completed").trim() == "1"
            if (!booted) Thread.sleep(2000)
        }
        if (!booted) throw GradleException("El emulador no inició. Revise $emulatorLog.")
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
