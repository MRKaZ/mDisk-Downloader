/**
 * @Project mDisk Downloader
 * @Class LibDependencies
 * @Author MRKaZ
 * @Since 4=30 AM 10/2/2022
 * @Origin Taprobana (LK)
 * @Copyright (c) 2022 MRKaZ. All rights reserved.
 */

object AndroidConfig {
    const val APPLICATION_ID = "com.mrkazofficial.mdiskdownloader"

    const val COMPILE_SDK_VERSION = 30
    const val MIN_SDK_VERSION = 21
    const val TARGET_SDK_VERSION = 29

    const val VERSION_CODE = 1001
    const val VERSION_NAME = "1.1"

    const val TEST_INSTRUMENTATION_RUNNER = "androidx.test.runner.AndroidJUnitRunner"

    const val JVT_TARGET = "1.8"

    const val IS_ABI_SPLIT_ENABLED = true
    val ARGS = arrayOf("x86", "x86_64", "armeabi-v7a", "arm64-v8a")
    const val UNIVERSAL_APK_ENABLED = false
}

object LibDependencies {

    val LIBS_DIR = mapOf("dir" to "libs", "include" to listOf("*.jar"))

    const val CONSTRANIT_LAYOUT = "androidx.constraintlayout:constraintlayout:${LibVersions.CONSTRAINT_LAYOUT_VERSION}"
    const val SWIPE_REFRESH_LAYOUT = "androidx.swiperefreshlayout:swiperefreshlayout:${LibVersions.SWIPE_REFRESH_LAYOUT_VERSION}"

    const val MATERIAL = "com.google.android.material:material:${LibVersions.MATERIAL_VERSION}"
    const val APP_COMPACT = "androidx.appcompat:appcompat:${LibVersions.APP_COMPACT_VERSION}"

    const val JUNIT = "junit:junit:${LibVersions.JUNIT_VERSION}"
    const val JUNIT_EXT_TEST_COMPILE = "androidx.test.ext:junit:${LibVersions.JUNIT_EXT_VERSION}"
    const val ESPRESSO_TEST_COMPILE = "androidx.test.espresso:espresso-core:${LibVersions.ESPRESSO_CORE_VERSION}"

    const val KTX_CORE = "androidx.core:core-ktx:${LibVersions.KTX_CORE_VERSION}"
    const val KTX_ACTIVITY = "androidx.activity:activity-ktx:${LibVersions.KTX_ACTIVITY_VERSION}"

    const val COROUTINES_CORE = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${LibVersions.COROUTINE_VERSION}"
    const val COROUTINES_ANDROID = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${LibVersions.COROUTINE_VERSION}"

    const val LIFECYCLE_VIEW_MODEL = "androidx.lifecycle:lifecycle-viewmodel-ktx:${LibVersions.LIFECYCLE_VERSION}"
    const val LIFECYCLE_LIVE_DATA = "androidx.lifecycle:lifecycle-livedata-ktx:${LibVersions.LIFECYCLE_VERSION}"

    const val RETROFIT = "com.squareup.retrofit2:retrofit:${LibVersions.RETROFIT_VERSION}"
    const val RETROFIT_GSON = "com.squareup.retrofit2:converter-gson:${LibVersions.RETROFIT_VERSION}"
    const val RETROFIT_SCALARS = "com.squareup.retrofit2:converter-scalars:${LibVersions.RETROFIT_VERSION}"

    const val GSON = "com.google.code.gson:gson:${LibVersions.GSON_VERSION}"
    const val PERMISSION_X = "com.guolindev.permissionx:permissionx:${LibVersions.PERMISSION_X_VERSION}"
    const val COMMONS_TEXT = "org.apache.commons:commons-text:${LibVersions.COMMONS_TEXT_VERSION}"
    const val ANIMATED_BOTTOM_BAR = "nl.joery.animatedbottombar:library:${LibVersions.ANIMATED_BOTTOM_BAR_VERSION}"

    const val GLIDE = "com.github.bumptech.glide:glide:${LibVersions.GLIDE_VERSION}"
    const val GLIDE_COMPILER = "com.github.bumptech.glide:compiler:${LibVersions.GLIDE_VERSION}"

    const val YT_DL = "com.github.yausername.youtubedl-android:library:${LibVersions.YT_DL_VERSION}"
    const val FFMPEG = "com.github.yausername.youtubedl-android:ffmpeg:${LibVersions.YT_DL_VERSION}"
    const val ARIA2C = "com.github.yausername.youtubedl-android:aria2c:${LibVersions.YT_DL_VERSION}"

    const val PREFERENCE = "androidx.preference:preference:${LibVersions.PREFERENCE_VERSION}"
}

object LibVersions {
    const val CONSTRAINT_LAYOUT_VERSION = "2.1.4"
    const val SWIPE_REFRESH_LAYOUT_VERSION = "1.1.0"

    const val APP_COMPACT_VERSION = "1.3.0"
    const val MATERIAL_VERSION = "1.4.0"

    const val JUNIT_VERSION = "4.13.2"
    const val JUNIT_EXT_VERSION = "1.1.3"
    const val ESPRESSO_CORE_VERSION = "3.4.0"

    const val KTX_ACTIVITY_VERSION = "1.2.4"
    const val KTX_CORE_VERSION = "1.6.0"
    const val COROUTINE_VERSION = "1.6.1"
    const val LIFECYCLE_VERSION = "2.2.0"

    const val RETROFIT_VERSION = "2.9.0"

    const val GSON_VERSION = "2.9.0"
    const val PERMISSION_X_VERSION = "1.6.4"
    const val COMMONS_TEXT_VERSION = "1.8"
    const val ANIMATED_BOTTOM_BAR_VERSION = "1.1.0"

    const val GLIDE_VERSION = "4.13.0"

    const val YT_DL_VERSION = "0.14.0"
    //const val FFMPEG_VERSION = "0.14.0"
    //const val ARIA2C_VERSION = "0.14.0"

    const val PREFERENCE_VERSION = "1.1.1"
}

object Plugins{
    const val APPLICATION = "com.android.application"
    const val KOTLIN_ANDROID = "org.jetbrains.kotlin.android"
    const val KOTLIN_KAPT = "kotlin-kapt"
}