package org.inagora.tinkerwrapperplugin

import groovy.xml.Namespace
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class TinkerWrapperManifestTask extends DefaultTask {
    private static final REAL_APP_NAME = "org.inagora.tinkerwrapper.implementation.RealApplicationImpl"
    private static final META_DATA_TAG = "meta-data"
    private static final META_DATA_ATTR = "application_name"
    private static final SERVICE_TAG = "service"
    private static final SERVICE_TINKER_RESULT = "org.inagora.tinkerwrapper.implementation.TinkerResultService"

    String manifestPath

    TinkerWrapperManifestTask() {
        group = 'tinkerWrapper'
    }

    @TaskAction
    def replaceApplication() {
        project.logger.error("----------------------TinkerWrapper Build Info --------------------------------")
        project.logger.error("Updating manifest, path is ${manifestPath}")
        project.logger.error("We will: ")
        project.logger.error("1. Replace your application to RealApplicationImpl, your application will be visited by reflection")
        project.logger.error("2. Add TinkerResultService to this manifest")
        def ns = new Namespace("http://schemas.android.com/apk/res/android", "android")
        def isr = null
        def pw = null
        try {
            isr = new InputStreamReader(new FileInputStream(manifestPath), "utf-8")
            def xml = new XmlParser().parse(isr)
            def application = xml.application[0]
            if (application) {
                // Replace application
                def originAppName = application.attributes()[ns.name]
                application.attributes()[ns.name] = REAL_APP_NAME

                def metaDataTags = application[META_DATA_TAG]
                // Remove any old application_name elements
                metaDataTags.findAll {
                    (it.attributes()[ns.name] == META_DATA_ATTR)
                }.each {
                    it.parent().remove(it)
                }
                // Add the new application_name element
                application.appendNode(META_DATA_TAG, [
                        (ns.name) : META_DATA_ATTR,
                        (ns.value): originAppName]
                )

                // Register TinkerResultService to manifest
                application.appendNode(SERVICE_TAG, [
                        (ns.name)    : SERVICE_TINKER_RESULT,
                        (ns.exported): "false"
                ])

                // Write the manifest file
                pw = new PrintWriter(manifestPath, "utf-8")
                def printer = new XmlNodePrinter(pw)
                printer.preserveWhitespace = true
                printer.print(xml)
            }
        } finally {
            IOHelper.closeQuietly(pw)
            IOHelper.closeQuietly(isr)
        }
        project.logger.error("-------------------------------------------------------------------------------")
    }
}