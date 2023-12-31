pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "FreeOkVideoPlugin"
include(":app")

val pluginApi = ":MediaBoxPluginApi"
include(pluginApi)
project(pluginApi).projectDir = File("./submodules/MediaBoxPlugin/pluginApi")
