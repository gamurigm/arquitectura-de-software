plugins { id("com.android.application") }

val apiBase = providers.gradleProperty("EUREKABANK_REST_URL")
    .orElse(providers.environmentVariable("EUREKABANK_REST_URL"))
    .orElse("http://10.0.2.2:8080/eurekabank-rest-java-gr09/api/eureka")
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
