
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.dokka.gradle.DokkaTaskPartial

apply(from = "../gradle/kotlin.gradle")
apply(from = "../gradle/publish.gradle")
apply(from = "../gradle/dokka.gradle")

plugins {
    java
}

// Overridden because this test bundle requires the templates
tasks.named<Jar>("testJar") {
    archiveClassifier.set("test")
    from(project.sourceSet("test").output){
        exclude("**.yml")
        exclude("**.properties")
        exclude("**.xml")
    }
}

extra["basePackage"] = "com.hexagonkt.http.server"

dependencies {
    "api"(project(":hexagon_http"))
    "testImplementation"(project(":http_client_ahc"))
    "testImplementation"(project(":http_server_jetty"))

    // For the Mock OpenAPI Server
    "testImplementation"("io.swagger.parser.v3:swagger-parser:2.0.20")
    "testImplementation"(project(":hexagon_settings"))
    "testImplementation"(project(":serialization_json"))
}

extensions.configure<PublishingExtension> {
    (publications["mavenJava"] as MavenPublication).artifact(tasks.named("testJar"))
}

setUpDokka(tasks.getByName<DokkaTaskPartial>("dokkaHtmlPartial"))
setUpDokka(tasks.getByName<DokkaTask>("dokkaJavadoc"))
setUpDokka(tasks.getByName<DokkaTask>("dokkaGfm"))

fun setUpDokka(dokkaTask: DokkaTaskPartial) {
    dokkaTask.dokkaSourceSets {
        configureEach {
            sourceRoots.from(file("src/test/kotlin"))
        }
    }
}

fun setUpDokka(dokkaTask: DokkaTask) {
    dokkaTask.dokkaSourceSets {
        configureEach {
            sourceRoots.from(file("src/test/kotlin"))
        }
    }
}
