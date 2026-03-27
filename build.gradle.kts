import io.izzel.taboolib.gradle.*

plugins {
    java
    `maven-publish`
    kotlin("jvm") version "2.1.20"
    kotlin("plugin.serialization") version "2.1.20"
    id("io.izzel.taboolib") version "2.0.23"
}

// 【关键修改】在项目顶层定义版本号，TabooLib 会自动读取它作为插件版本
version = "1.0.0"

taboolib {
    env {
        install(Basic)
        install(Bukkit)
        install(BukkitHook)
        install(BukkitUtil)
        install(CommandHelper)
        install(LettuceRedis)

        debug = false
        forceDownloadInDev = true
        repoCentral = "https://maven.aliyun.com/repository/central"
        repoTabooLib = "http://ptms.ink:8081/repository/releases"
        enableIsolatedClassloader = false
    }

    description {
        name = "AuctionPlugin"

        contributors {
        }
        dependencies {
        }
    }

    // 避免和别的插件冲突的重定向包名
    relocate("kotlinx.serialization", "org.fzzfegg.auction.serialization")

    version { taboolib = "6.2.3-d4a5f0ea" }
}

repositories {
    mavenCentral()
    maven("https://www.mcwar.cn/nexus/repository/maven-public/")
}

dependencies {
    taboo("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    taboo("io.lettuce:lettuce-core:6.6.0.RELEASE")
    taboo("org.apache.commons:commons-pool2:2.12.0")
    taboo("com.zaxxer:HikariCP:4.0.3")
    taboo("mysql:mysql-connector-java:8.0.33")
    taboo("com.github.ben-manes.caffeine:caffeine:2.9.3")

    compileOnly("ink.ptms.core:v12004:12004:mapped")
    compileOnly("ink.ptms.core:v12004:12004:universal")
    compileOnly("ink.ptms:nms-all:1.0.0")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core-jvm:1.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.6.0")

    compileOnly(fileTree("libs"))
    compileOnly(kotlin("stdlib"))
    compileOnly(kotlin("reflect"))
}

kotlin {
    jvmToolchain(8)
    target {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
}