package org.inagora.tinkerwrapperplugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class TinkerWrapperPlugin implements Plugin<Project> {
    def static android

    @Override
    void apply(Project project) {
        android = project.extensions.android
        project.apply plugin: 'com.tencent.tinker.patch'
        project.afterEvaluate {
            it.extensions.tinkerPatch.dex.loader.add('org.inagora.tinkerwrapper.implementation.RealApplicationImpl')
            updateManifest(it)
        }
    }

    private static String updateManifest(Project project) {
        android.applicationVariants.all { variant ->

            def variantOutput = variant.outputs.first()
            def variantName = variant.name.capitalize()
            def manifestPath = ""

            if (variantOutput.metaClass.hasProperty(variantOutput, 'processResourcesProvider')) {
                manifestPath = variantOutput.processResourcesProvider.get().manifestFile
            } else if (variantOutput.processResources.metaClass.hasProperty(variantOutput.processResources, 'manifestFile')) {
                manifestPath = variantOutput.processResources.manifestFile
            } else if (variantOutput.processManifest.metaClass.hasProperty(variantOutput.processManifest, 'manifestOutputFile')) {
                manifestPath = variantOutput.processManifest.manifestOutputFile
            }

            TinkerWrapperManifestTask manifestTask = project.tasks.create("tinkerWrapperProcess${variantName}Manifest", TinkerWrapperManifestTask)
            manifestTask.manifestPath = manifestPath

            def tinkerAMTask = project.tasks.findByName("tinkerProcess${variantName}Manifest")
            tinkerAMTask.dependsOn manifestTask
        }

    }
}