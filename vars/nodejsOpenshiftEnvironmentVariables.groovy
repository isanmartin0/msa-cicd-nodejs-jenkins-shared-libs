#!/usr/bin/groovy
import com.evobanco.Constants
import com.evobanco.Utils

def call(body) {

    def utils = new com.evobanco.Utils()
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def branchNameHY = config.branchHY
    def branchType = config.branch_type

    def packageJSON = readJSON file: 'package.json'
    def project = "${packageJSON.name}"

    def projectName
    if (branchType == 'master') {
        projectName = "${packageJSON.name}"
    } else {
        projectName = "${packageJSON.name}-${branchNameHY}"
    }

    echo "Creating environment variables"


}
