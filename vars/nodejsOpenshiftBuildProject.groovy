#!/usr/bin/groovy

def call(body) {

    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    echo "nodejsOpenshiftBuildProject parameters"

    echo "config.devModeOpenshift:  ${config.devModeOpenshift}"
    echo "config.debugPortOpenshift:  ${config.debugPortOpenshift}"
    echo "config.useNpmMirrorOpenshift:  ${config.useNpmMirrorOpenshift}"
    echo "config.npmMirrorOpenshift:  ${config.npmMirrorOpenshift}"
    echo "config.useAlternateNpmRunOpenshift:  ${config.useAlternateNpmRunOpenshift}"
    echo "config.alternateNpmRunScriptOpenshift:  ${config.alternateNpmRunScriptOpenshift}"


    Boolean isDevMode = false
    Boolean isUseNpmMirror = false
    Boolean isUseAlternateNpmRunScript = false

    def devMode = config.devModeOpenshift
    def debugPort = config.debugPortOpenshift
    def useNpmMirror = config.useNpmMirrorOpenshift
    def npmMirror = config.npmMirrorOpenshift
    def useAlternateNpmRun = config.useAlternateNpmRunOpenshift
    def alternateNpmRunScript = config.alternateNpmRunScriptOpenshift


    def packageJSON = readJSON file: 'package.json'
    def project = "${packageJSON.name}"
    def projectName
    if (config.branch_type == 'master') {
    	projectName = "${packageJSON.name}"
    } else {
    	projectName = "${packageJSON.name}-${config.branchHY}"
    }

    def version = packageJSON.version


    if (devMode != null) {
        isDevMode = devMode.toBoolean()
    }

    if (useNpmMirror != null) {
        isUseNpmMirror = useNpmMirror.toBoolean()
    }

    if (useAlternateNpmRun != null) {
        isUseAlternateNpmRunScript = useAlternateNpmRun.toBoolean()
    }




    def artifactUrl = "${config.repoUrl}/${packageJSON.name}/${version}/${packageJSON.name}-${version}.tgz"
    def buildEnvVars = [ [ name : 'WAR_FILE_URL', value : artifactUrl ]]


    echo "buildEnvVars.size: ${buildEnvVars.size()}"

    if (isDevMode) {
        buildEnvVars << [ [ name : 'DEV_MODE', value : isDevMode ]]
    }

    echo "buildEnvVars.size: ${buildEnvVars.size()}"

    openshiftBuild buildConfig: project, namespace: projectName, verbose: 'true', showBuildLogs: 'true', env: buildEnvVars

    def destTag = "${packageJSON.name}-${version}"

    openshiftTag(namespace: projectName, sourceStream: project, sourceTag: 'latest', destinationStream: project, destinationTag: destTag)
}
