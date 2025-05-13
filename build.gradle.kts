import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import java.awt.Desktop


plugins {
    id("java")
    id("org.jetbrains.intellij.platform") version "2.5.0"
}

group = "io.github.javafactoryplugindev"
version = "0.0.154"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        intellijIdeaCommunity("2024.1") // ✅ 여기에 타겟 IDEA 명시
        bundledPlugin("com.intellij.java")
        testFramework(TestFrameworkType.Platform)
    }

    testImplementation("io.github.javafactoryplugindev:javafactory-annotation:0.1.1")

    implementation("com.openai:openai-java:1.3.0")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
}

tasks {
    wrapper {
        gradleVersion = "8.13"
    }

    patchPluginXml {
        sinceBuild.set("241")  // 버전도 맞춰주고
        untilBuild.set("251.*")
    }

    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }

    test {
        //useJUnitPlatform()
        useJUnit()

        finalizedBy("openTestReport") // ✅ 테스트 끝나고 openTestReport 실행
    }

    runIde {
        jvmArgs = listOf("-Didea.log.level=INFO")
    }


    register("openTestReport") {
        doLast {
            val reportFile = layout.buildDirectory.file("reports/tests/test/index.html").get().asFile
            if (reportFile.exists()) {
                Desktop.getDesktop().browse(reportFile.toURI())
                println("✅ 테스트 리포트 자동 오픈: ${reportFile.absolutePath}")
            } else {
                println("❗ 테스트 리포트 파일이 없습니다: ${reportFile.absolutePath}")
            }
        }
    }
}

