plugins {
    alias(libs.plugins.hivemq.extension)
    alias(libs.plugins.defaults)
    alias(libs.plugins.oci)
    alias(libs.plugins.license)
}

group = "com.hivemq.extensions"
description = "HiveMQ Mqtt Message Log Extension"

hivemqExtension {
    name = "HiveMQ Mqtt Message Log Extension"
    author = "HiveMQ"
    priority = 1000
    startPriority = 1000
    sdkVersion = libs.versions.hivemq.extensionSdk

    resources {
        from("LICENSE")
        from("src/main/resources/config.xsd") { into("conf") }
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(11)
    }
}

dependencies {
    compileOnly(libs.jetbrains.annotations)
    implementation(libs.commonsLang)
    implementation(libs.jaxb.api)
    runtimeOnly(libs.jaxb.impl)
}

oci {
    registries {
        dockerHub {
            optionalCredentials()
        }
    }
    imageMapping {
        mapModule("com.hivemq", "hivemq-enterprise") {
            toImage("hivemq/hivemq4")
        }
    }
    imageDefinitions.register("main") {
        allPlatforms {
            dependencies {
                runtime("com.hivemq:hivemq-enterprise:latest") { isChanging = true }
            }
            layer("main") {
                contents {
                    permissions("opt/hivemq/", 0b111_111_101)
                    permissions("opt/hivemq/extensions/", 0b111_111_101)
                    into("opt/hivemq/extensions") {
                        from(zipTree(tasks.hivemqExtensionZip.flatMap { it.archiveFile }))
                    }
                }
            }
        }
    }
}

@Suppress("UnstableApiUsage")
testing {
    suites {
        withType<JvmTestSuite> {
            useJUnitJupiter(libs.versions.junit.jupiter)
        }
        "test"(JvmTestSuite::class) {
            dependencies {
                compileOnly(libs.jetbrains.annotations)
                implementation(libs.mockito)
                implementation(libs.logback.classic)
            }
        }
        "integrationTest"(JvmTestSuite::class) {
            dependencies {
                compileOnly(libs.jetbrains.annotations)
                implementation(libs.awaitility)
                implementation(libs.hivemq.mqttClient)
                implementation(libs.testcontainers.junitJupiter)
                implementation(libs.testcontainers.hivemq)
                implementation(libs.gradleOci.junitJupiter)
                runtimeOnly(libs.logback.classic)
            }
            oci.of(this) {
                imageDependencies {
                    runtime(project).tag("latest")
                }
            }
        }
    }
}

license {
    header = rootDir.resolve("HEADER")
    mapping("java", "SLASHSTAR_STYLE")
    exclude("**/test-conf/mqttMessageLog.properties")
}
