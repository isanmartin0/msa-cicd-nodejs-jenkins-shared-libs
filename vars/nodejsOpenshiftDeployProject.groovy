#!/usr/bin/groovy
import com.evobanco.NodejsUtils

def call(body) {

    def utils = new com.evobanco.NodejsUtils()
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def packageJSON = readJSON file: 'package.json'
    def project = utils.getProject(packageJSON.name)
    def projectName = utils.getProjectName(packageJSON.name)

    openshiftDeploy deploymentConfig: project, namespace: projectName

    def hostname = sh(script: "oc get route ${project} -o jsonpath=\"{.spec.host}\" -n ${projectName}", returnStdout: true).toString().trim()

    echo "Deployed app at hostname: ${hostname}"

    return hostname
}
