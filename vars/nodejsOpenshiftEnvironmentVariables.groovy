#!/usr/bin/groovy
import com.evobanco.NodejsConstants

def call(body) {

    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    Boolean isCreatePortEnvironmentVariable = false
    Boolean isDevMode = false
    Boolean isUseNpmMirrorOpenshift = false
    Boolean isUseAlternateNpmRunOpenshift = false



    def createPortEnvironmentVariable = config.createPortEnvironmentVariableOpenshift
    def portNumber = config.portNumber
    def devMode = config.devMode
    def debugPort = config.debugPort
    def useNpmMirrorOpenshift = config.useNpmMirror
    def npmMirror = config.npmMirror
    def useAlternateNpmRunOpenshift = config.useAlternateNpmRun
    def alternateNpmRunScript = config.alternateNpmRunScript
    def branchNameHY = config.branchHY
    def branchType = config.branch_type

    echo "createPortEnvironmentVariable: ${createPortEnvironmentVariable}"
    echo "portNumber: ${portNumber}"
    echo "devMode: ${devMode}"
    echo "debugPort: ${debugPort}"
    echo "useNpmMirrorOpenshift: ${useNpmMirrorOpenshift}"
    echo "npmMirror: ${npmMirror}"
    echo "useAlternateNpmRunOpenshift: ${useAlternateNpmRunOpenshift}"
    echo "alternateNpmRunScript: ${alternateNpmRunScript}"
    echo "branchNameHY: ${branchNameHY}"
    echo "branchType: ${branchType}"


    def packageJSON = readJSON file: 'package.json'
    def project = "${packageJSON.name}"

    def projectName
    if (branchType == 'master') {
        projectName = "${packageJSON.name}"
    } else {
        projectName = "${packageJSON.name}-${branchNameHY}"
    }

    echo "Creating environment variables"

    if (createPortEnvironmentVariable != null) {
        isCreatePortEnvironmentVariable = createPortEnvironmentVariable.toBoolean()
    }

    if (devMode != null) {
        isDevMode = devMode.toBoolean()
    }

    if (useNpmMirrorOpenshift != null) {
        isUseNpmMirrorOpenshift = useNpmMirrorOpenshift.toBoolean()
    }

    if (useAlternateNpmRunOpenshift != null) {
        isUseAlternateNpmRunOpenshift = useAlternateNpmRunOpenshift.toBoolean()
    }

    if (isCreatePortEnvironmentVariable) {
        try {
            //Remove PORT environment variable created by template
            echo "Removing PORT environment variable"
            sh "oc env dc/${project} ${NodejsConstants.PORT_ENVIRONMENT_VARIABLE}- -n ${projectName}"
        } catch (err) {
            echo "The ${NodejsConstants.PORT_ENVIRONMENT_VARIABLE} environment variable on dc/${project} -n ${projectName} cannot be removed"
        }

        echo "Adding ${NodejsConstants.PORT_ENVIRONMENT_VARIABLE}=${portNumber} environment variable"
        sh "oc env dc/${project} ${NodejsConstants.PORT_ENVIRONMENT_VARIABLE}=\"${portNumber}\" -n ${projectName}"

    }

    if (isDevMode) {
        try {
            echo "Removing DEBUG_PORT environment variable"
            sh "oc env dc/${project} ${NodejsConstants.DEBUG_PORT_ENVIRONMENT_VARIABLE}- -n ${projectName}"
        } catch (err) {
            echo "The ${NodejsConstants.DEBUG_PORT_ENVIRONMENT_VARIABLE} environment variable on dc/${project} -n ${projectName} cannot be removed"
        }

        echo "Adding ${NodejsConstants.DEBUG_PORT_ENVIRONMENT_VARIABLE}=${debugPort} environment variable"
        sh "oc env dc/${project} ${NodejsConstants.DEBUG_PORT_ENVIRONMENT_VARIABLE}=${debugPort} -n ${projectName}"


        try {
            echo "Removing DEV_MODE environment variable"
            sh "oc env dc/${project} ${NodejsConstants.DEV_MODE_ENVIRONMENT_VARIABLE}- -n ${projectName}"
        } catch (err) {
            echo "The ${NodejsConstants.DEV_MODE_ENVIRONMENT_VARIABLE} environment variable on dc/${project} -n ${projectName} cannot be removed"
        }

        echo "Adding ${NodejsConstants.DEV_MODE_ENVIRONMENT_VARIABLE}=${devMode} environment variable"
        sh "oc env dc/${project} ${NodejsConstants.DEV_MODE_ENVIRONMENT_VARIABLE}=${devMode} -n ${projectName}"

    }

    if (isUseNpmMirrorOpenshift) {
        try {
            echo "Removing NPM_MIRROR environment variable"
            sh "oc env dc/${project} ${NodejsConstants.NPM_MIRROR_ENVIRONMENT_VARIABLE}- -n ${projectName}"
        } catch (err) {
            echo "The ${NodejsConstants.NPM_MIRROR_ENVIRONMENT_VARIABLE} environment variable on dc/${project} -n ${projectName} cannot be removed"
        }

        echo "Adding ${NodejsConstants.NPM_MIRROR_ENVIRONMENT_VARIABLE}=${npmMirror} environment variable"
        sh "oc env dc/${project} ${NodejsConstants.NPM_MIRROR_ENVIRONMENT_VARIABLE}=${npmMirror} -n ${projectName}"

    }

    if (isUseAlternateNpmRunOpenshift) {
        try {
            echo "Removing NPM_RUN environment variable"
            sh "oc env dc/${project} ${NodejsConstants.NPM_RUN_ENVIRONMENT_VARIABLE}- -n ${projectName}"
        } catch (err) {
            echo "The ${NodejsConstants.NPM_RUN_ENVIRONMENT_VARIABLE} environment variable on dc/${project} -n ${projectName} cannot be removed"
        }

        echo "Adding ${NodejsConstants.NPM_RUN_ENVIRONMENT_VARIABLE}=${alternateNpmRunScript} environment variable"
        sh "oc env dc/${project} ${NodejsConstants.NPM_RUN_ENVIRONMENT_VARIABLE}=${alternateNpmRunScript} -n ${projectName}"

    }




}
