#!/usr/bin/groovy

def call(body) {

    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    Boolean isCreatePortEnvironmentVariable = false
    def createPortEnvironmentVariable = config.createPortEnvironmentVariableOpenshift
    def portNumber = config.portNumber
    def branchNameHY = config.branchHY
    def branchType = config.branch_type

    echo "createPortEnvironmentVariable: ${createPortEnvironmentVariable}"
    echo "portNumber: ${portNumber}"
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

    if (isCreatePortEnvironmentVariable) {
        try {
            //Remove JAVA_OPTS environment variable created by template
            echo "Removing PORT environment variable"
            sh "oc env dc/${project} PORT- -n ${projectName}"
        } catch (err) {
            echo "The PORT environment variable on dc/${project} -n ${projectName} cannot be removed"
        }

        echo "Adding PORT=${portNumber} environment variable"
        sh "oc env dc/${project} PORT=\"${portNumber}\" -n ${projectName}"

    }

}
