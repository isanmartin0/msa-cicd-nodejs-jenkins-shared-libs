#!/usr/bin/groovy

def call(body) {

    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def packageJSON = readJSON file: 'package.json'
    def project = "${packageJSON.name}"
    def projectName
    if (config.branch_type == 'master') {
    	projectName = "${packageJSON.name}"
    } else {
    	projectName = "${packageJSON.name}-${config.branchHY}"
    }

    openshiftDeploy deploymentConfig: project, namespace: projectName

    def hostname = sh(script: "oc get route ${project} -o jsonpath=\"{.spec.host}\" -n ${projectName}", returnStdout: true).toString().trim()

    echo "Deployed app at hostname: ${hostname}"

    return hostname
}
