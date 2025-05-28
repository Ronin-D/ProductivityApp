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

rootProject.name = "ProductivityApp"
include(":app")
include(":feature:auth:sign_in")
include(":feature:auth:sign_up")
include(":core:data:network")
include(":core:data:data_store")
include(":core:ui")
include(":feature:user:user_app_statistics")
include(":feature:user:profile")
include(":core:util")
