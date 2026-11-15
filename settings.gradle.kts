pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

// JVM toolchain 자동 다운로드(foojay) 플러그인은 의도적으로 넣지 않는다.
// 설치된 JDK(Android Studio JBR 25)로 빌드하고, 바이트코드 타깃만 낮춘다.

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // Pleos Connect SDK (Vehicle 등)
        maven {
            name = "PleosPlayground"
            url = uri("https://nexus-playground.pleos.ai/repository/maven-releases/")
            content { includeGroupByRegex("ai\\.pleos\\.playground(\\..*)?") }
        }
    }
}

rootProject.name = "hmg-vehicle-app"

include(":shared")
include(":backend")
include(":frontend:app")
// 로컬 실험용 샌드박스 앱(제출 대상 아님). 폴더가 있는 저장소에서만 포함한다.
if (file("frontend/sandbox").isDirectory) include(":frontend:sandbox")
