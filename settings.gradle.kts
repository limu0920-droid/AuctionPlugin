dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
    repositories {
        maven("https://repo.taboolib.com/repository/releases/")
        maven("https://repo.ptms.ink/repository/maven-public/")
        mavenCentral()
    }
}

rootProject.name = "AuctionPlugin"