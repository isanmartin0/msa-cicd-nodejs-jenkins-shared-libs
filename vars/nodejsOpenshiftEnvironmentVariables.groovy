#!/usr/bin/groovy
import com.evobanco.NodejsConstants

def call(body) {

    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    Boolean isCreatePortEnvironmentVariable = false
    Boolean isDevMode = false
    Boolean isUseNpmMirror = false
    Boolean isUseAlternateNpmRunScript = false



    def createPortEnvironmentVariable = config.createPortEnvironmentVariableOpenshift
    def portNumber = config.portNumber
    def devMode = config.devModeOpenshift
    def debugPort = config.debugPortOpenshift
    def useNpmMirror = config.useNpmMirrorOpenshift
    def npmMirror = config.npmMirrorOpenshift
    def useAlternateNpmRun = config.useAlternateNpmRunOpenshift
    def alternateNpmRunScript = config.alternateNpmRunScriptOpenshift
    def branchNameHY = config.branchHY
    def branchType = config.branch_type

    echo "nodejsOpenshiftEnvironmentVariables parameters"
    echo "createPortEnvironmentVariable: ${createPortEnvironmentVariable}"
    echo "portNumber: ${portNumber}"
    echo "devMode: ${devMode}"
    echo "debugPort: ${debugPort}"
    echo "useNpmMirror: ${useNpmMirror}"
    echo "npmMirror: ${npmMirror}"
    echo "useAlternateNpmRun: ${useAlternateNpmRun}"
    echo "alternateNpmRunScript: ${alternateNpmRunScript}"
    echo "branchNameHY: ${branchNameHY}"
    echo "branchType: ${branchType}"



    def mapEnvironmentVariables = ["ENVIR_1" : "XXX", "ENVIR_2" : 2, "ENVIR_3" : true]

    def packageJSON = readJSON file: 'package.json'
    def project = "${packageJSON.name}"

    def projectName
    if (branchType == 'master') {
        projectName = "${packageJSON.name}"
    } else {
        projectName = "${packageJSON.name}-${branchNameHY}"
    }


    if (createPortEnvironmentVariable != null) {
        isCreatePortEnvironmentVariable = createPortEnvironmentVariable.toBoolean()
    }

    if (devMode != null) {
        isDevMode = devMode.toBoolean()
    }

    if (useNpmMirror != null) {
        isUseNpmMirror = useNpmMirror.toBoolean()
    }

    if (useAlternateNpmRun != null) {
        isUseAlternateNpmRunScript = useAlternateNpmRun.toBoolean()
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

    if (isUseNpmMirror) {
        try {
            echo "Removing NPM_MIRROR environment variable"
            sh "oc env dc/${project} ${NodejsConstants.NPM_MIRROR_ENVIRONMENT_VARIABLE}- -n ${projectName}"
        } catch (err) {
            echo "The ${NodejsConstants.NPM_MIRROR_ENVIRONMENT_VARIABLE} environment variable on dc/${project} -n ${projectName} cannot be removed"
        }

        echo "Adding ${NodejsConstants.NPM_MIRROR_ENVIRONMENT_VARIABLE}=${npmMirror} environment variable"
        sh "oc env dc/${project} ${NodejsConstants.NPM_MIRROR_ENVIRONMENT_VARIABLE}=${npmMirror} -n ${projectName}"

    }

    if (isUseAlternateNpmRunScript) {
        try {
            echo "Removing NPM_RUN environment variable"
            sh "oc env dc/${project} ${NodejsConstants.NPM_RUN_ENVIRONMENT_VARIABLE}- -n ${projectName}"
        } catch (err) {
            echo "The ${NodejsConstants.NPM_RUN_ENVIRONMENT_VARIABLE} environment variable on dc/${project} -n ${projectName} cannot be removed"
        }

        echo "Adding ${NodejsConstants.NPM_RUN_ENVIRONMENT_VARIABLE}=${alternateNpmRunScript} environment variable"
        sh "oc env dc/${project} ${NodejsConstants.NPM_RUN_ENVIRONMENT_VARIABLE}=${alternateNpmRunScript} -n ${projectName}"

    }

    mapEnvironmentVariables.each { key, value ->
        println "Name: $key Age: $value"
        try {
            echo "Removing $key environment variable"
            sh "oc env dc/${project} $key- -n ${projectName}"
        } catch (err) {
            echo "The $key environment variable on dc/${project} -n ${projectName} cannot be removed"
        }

        echo "Adding $key=$value environment variable"
        sh "oc env dc/${project} $key=$value -n ${projectName}"

    }


}
