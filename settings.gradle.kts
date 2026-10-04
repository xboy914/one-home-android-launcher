pluginManagement { repositories { mavenCentral(); maven("https://mirrors.cloud.tencent.com/nexus/repository/maven-public"); google(); gradlePluginPortal() } }
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories { mavenCentral(); maven("https://mirrors.cloud.tencent.com/nexus/repository/maven-public"); google() }
}
rootProject.name = "DemoLauncher"
include(":app")
