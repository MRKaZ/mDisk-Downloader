plugins {
    id(Plugins.APPLICATION)
    id(Plugins.KOTLIN_ANDROID)
    id(Plugins.KOTLIN_KAPT)
}

android {
    compileSdk = AndroidConfig.COMPILE_SDK_VERSION

    defaultConfig {
        applicationId = AndroidConfig.APPLICATION_ID
        minSdk = AndroidConfig.MIN_SDK_VERSION
        targetSdk = AndroidConfig.TARGET_SDK_VERSION
        versionCode = AndroidConfig.VERSION_CODE
        versionName = AndroidConfig.VERSION_NAME

        testInstrumentationRunner = AndroidConfig.TEST_INSTRUMENTATION_RUNNER
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
        }
        release {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = AndroidConfig.JVT_TARGET
    }

    splits {
        abi {
            isEnable = AndroidConfig.IS_ABI_SPLIT_ENABLED
            reset()
            include(*AndroidConfig.ARGS)
            isUniversalApk = AndroidConfig.UNIVERSAL_APK_ENABLED
        }
    }
}

dependencies {
    implementation(fileTree(LibDependencies.LIBS_DIR))
    /** Java components */
    testCompileOnly(LibDependencies.JUNIT)
    androidTestCompileOnly(LibDependencies.JUNIT_EXT_TEST_COMPILE)
    androidTestCompileOnly(LibDependencies.ESPRESSO_TEST_COMPILE)

    /** Layouts components */
    implementation(LibDependencies.CONSTRANIT_LAYOUT)
    implementation(LibDependencies.SWIPE_REFRESH_LAYOUT)

    /** Designing components */
    implementation(LibDependencies.MATERIAL)
    implementation(LibDependencies.APP_COMPACT)

    /** Kotlin components */
    implementation(LibDependencies.KTX_CORE)
    implementation(LibDependencies.KTX_ACTIVITY)

    // Lifecycle TODO : UI-Related changed
    implementation(LibDependencies.LIFECYCLE_VIEW_MODEL)
    implementation(LibDependencies.LIFECYCLE_LIVE_DATA)

    // Coroutine TODO : Threads and background threads
    implementation(LibDependencies.COROUTINES_CORE)
    implementation(LibDependencies.COROUTINES_ANDROID)

    /** Networking components */
    // Retrofit
    implementation(LibDependencies.RETROFIT)
    implementation(LibDependencies.RETROFIT_GSON)
    implementation(LibDependencies.RETROFIT_SCALARS)

    /** Utils */
    implementation(LibDependencies.GSON)
    implementation(LibDependencies.PERMISSION_X)

    implementation(LibDependencies.COMMONS_TEXT)
    implementation(LibDependencies.ANIMATED_BOTTOM_BAR)
    
    /** Image loaders */
    // Glide
    implementation(LibDependencies.GLIDE)
    kapt(LibDependencies.GLIDE_COMPILER)

    implementation(LibDependencies.YT_DL)
    implementation(LibDependencies.FFMPEG)
    implementation(LibDependencies.ARIA2C)

    implementation(LibDependencies.PREFERENCE)

}