plugins {
    val kotlinVersion = "1.6.10"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.14.0"
}

group = "org.mirai"
version = "0.1.0"

repositories {
//    if (System.getenv("CI")?.toBoolean() != true) {
//        maven("https://maven.aliyun.com/repository/public") // 阿里云国内代理仓库
//    }
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("junit:junit:4.13.1")
    implementation("com.bernardomg.tabletop:dice:2.2.3")
}

tasks.test {
    useJUnitPlatform()
}
