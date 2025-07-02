plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.25"
    id("org.jetbrains.intellij.platform") version "2.6.0"
}

group = "io.github.sangcomz"
version = "0.2.0"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin.html
dependencies {
    intellijPlatform {
        create("IC", "2024.2.5")
        testFramework(org.jetbrains.intellij.platform.gradle.TestFrameworkType.Platform)

        bundledPlugin("Git4Idea")
    }
}

intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            sinceBuild = "242"
        }

        changeNotes = """
            Multiple Renderer Support: Text, Compact, and Progress Bar formats
            Custom Display Templates: Fully customizable status bar formatting
            Progress Bar Renderer*: Visual progress indicator with limit tracking
            File Exclusion Patterns: Filter out test files, build files, and more
            Auto-Refresh: Configurable automatic updates (1-60 minutes)
            Enhanced Alert System: Line count limits with "don't show again" options
            Advanced Settings UI: Comprehensive configuration panel
            Real-time Settings: Changes apply immediately without restart
    """.trimIndent()
    }
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "21"
        targetCompatibility = "21"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "21"
    }
}