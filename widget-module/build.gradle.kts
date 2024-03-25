plugins {
    id("base")
    id("eclipse")

    // the ignition module plugin: https://github.com/inductiveautomation/ignition-module-tools
    id("io.ia.sdk.modl") version ("0.1.1")
    id("org.barfuin.gradle.taskinfo") version "2.1.0"
}

allprojects {
    group = "com.macautoinc.widget"
    version = "1.0.0-SNAPSHOT"
}

ignitionModule {
    // name of the .modl file to build
    fileName.set("MACAutoWidget")

    // module xml configuration
    name.set("MAC Auto Inc. Widget")
    id.set("com.macautoinc.widget")
    moduleVersion.set("${project.version}")
    moduleDescription.set("This module provides an example on how to create a module with a widget for Perspective")
    requiredIgnitionVersion.set("8.1.38")
    license.set("license.html")

    // If we depend on other module being loaded/available, then we specify IDs of the module we depend on,
    // and specify the Ignition Scope that applies. "G" for gateway, "D" for designer, "C" for VISION client
    // (this module does not run in the scope of a Vision client, so we don't need a "C" entry here)
    moduleDependencies.put("com.inductiveautomation.perspective", "DG")

    // map of 'Gradle Project Path' to Ignition Scope in which the project is relevant.  This is combined with
    // the dependency declarations within the subproject's build.gradle.kts in order to determine which
    // dependencies need to be bundled with the module and added to the module.xml.
    projectScopes.putAll(
            mapOf(
                    ":gateway" to "G",
                    ":web" to "G",
                    ":designer" to "D",
                    ":common" to "GD"
            )
    )

    // 'hook classes' are the things that Ignition loads and runs when your module is installed.  This map tells
    // Ignition which classes should be loaded in a given scope.
    hooks.putAll(
            mapOf(
                    "com.macautoinc.echarts.gateway.GatewayHook" to "G",
                    "com.macautoinc.echarts.designer.DesignerHook" to "D"
            )
    )
    skipModlSigning.set(true)
}

tasks {
    // set the deployModl task to post to the local gateway running in the Docker container
    // see docker-compose.yml for details
    deployModl {
        hostGateway.set("http://localhost:59088")
    }
}

val deepClean by tasks.registering {
    dependsOn(allprojects.map { "${it.path}:clean" })
    description = "Executes clean tasks and remove node plugin caches."
    doLast {
        delete(file(".gradle"))
    }
}
