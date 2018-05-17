#!/usr/bin/groovy

def call(body) {

    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()


    echo "config.repoUrl: ${config.repoUrl}"

    def packageJSON = readJSON file: 'package.json'
    def project = "${packageJSON.name}"
    def projectName
    if (config.branch_type == 'master') {
    	projectName = "${packageJSON.name}"
    } else {
    	projectName = "${packageJSON.name}-${config.branchHY}"
    }

    def version = packageJSON.version

    def artifactUrl = "${config.repoUrl}/${packageJSON.name}/${version}/${packageJSON.name}-${version}.tgz"
    def buildEnvVars = [ [ name : 'WAR_FILE_URL', value : artifactUrl ]]

    openshiftBuild buildConfig: project, namespace: projectName, verbose: 'true', showBuildLogs: 'true', env: buildEnvVars

    def destTag = "${packageJSON.name}-${version}"

    openshiftTag(namespace: projectName, sourceStream: project, sourceTag: 'latest', destinationStream: project, destinationTag: destTag)
}
