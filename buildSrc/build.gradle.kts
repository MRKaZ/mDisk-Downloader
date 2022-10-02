import org.gradle.kotlin.dsl.`kotlin-dsl`

plugins {
    `kotlin-dsl`
}

kotlinDslPluginOptions {
    @Suppress("DEPRECATION")
    experimentalWarning.set(false)
}

repositories {
    @Suppress("DEPRECATION")
    mavenCentral()
    google()
}