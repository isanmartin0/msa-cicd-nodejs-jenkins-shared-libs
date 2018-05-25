#!/usr/bin/groovy
import com.evobanco.NodejsConstants

def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    echo "nodejsOpenshiftCheckAndCreateProject parameters"
    echo "Parameters nodejsOpenshiftCheckAndCreateProject"
    echo "config.template: ${config.template}"
    echo "config.environment: ${config.environment}"
    echo "config.branchHY: ${config.branchHY}"
    echo "config.dockerRegistry: ${config.dockerRegistry}"
    echo "config.sourceRepositoryURL: ${config.sourceRepositoryURL}"
    echo "config.sourceRepositoryBranch: ${config.sourceRepositoryBranch}"
    echo "config.npmMirror: ${config.npmMirror}"
    echo "config.nodejsVersion: ${config.nodejsVersion}"
    echo "config.environment: ${config.environment}"
    echo "config.package_tag: ${config.package_tag}"
    echo "config.is_scoped_package: ${config.is_scoped_package}"

    def packageJSON = readJSON file: 'package.json'
    def project = "${packageJSON.name}"
    def projectName
    def branchNameContainerImage = ""
    int minimumPodReplicas = NodejsConstants.MINIMUM_POD_REPLICAS
    int maximumPodReplicas = NodejsConstants.MAXIMUM_POD_REPLICAS
    def hostname = ""
    if (config.branch_type == 'master') {
    	projectName = "${packageJSON.name}"

        //Host name for the route element
        hostname = ".svcs" + NodejsConstants.HOSTNAME_DOMAIN

        //Set minimum number of replicas of pod
        minimumPodReplicas = NodejsConstants.MINIMUM_POD_REPLICAS_PRO_ENVIRONMENT
        maximumPodReplicas = NodejsConstants.MAXIMUM_POD_REPLICAS_PRO_ENVIRONMENT

    } else {
    	projectName = "${packageJSON.name}-${config.branchHY}"

        //Branch name for image container
        branchNameContainerImage = "-${config.branchHY}"
        //Host name for the route element
        hostname = "-${config.branchHY}.svcs${config.environment}" + NodejsConstants.HOSTNAME_DOMAIN
    }

    withCredentials([usernamePassword(credentialsId: "${config.oseCredential}", passwordVariable: 'PASSWORD', usernameVariable: 'USERNAME')]) {
        sh "oc login ${config.cloudURL} -u ${USERNAME} -p ${PASSWORD} --insecure-skip-tls-verify=true"
    }

    try {
        sh "oc project ${projectName}"
    } catch (err) {

        println err

        echo "As the project does not exist on openshift, we will create it"

        sh "oc new-project ${projectName}"
        sh "oc label namespace ${projectName} \"environment=${config.environment}\""
        sh "oc policy add-role-to-user view system:serviceaccount:${config.jenkinsNS}:jenkins -n ${projectName}"
        sh "oc policy add-role-to-user edit system:serviceaccount:${config.jenkinsNS}:jenkins -n ${projectName}"

        sh "oc project ${projectName}"

        withCredentials([string(credentialsId: "${config.artCredential}", variable: 'ARTIFACTORY_TOKEN')]) {
            sh "oc process -n ${projectName} -f ${config.template} BRANCH_NAME=${env.BRANCH_NAME} BRANCH_NAME_HY=${config.branchHY} BRANCH_NAME_HY_CONTAINER_IMAGE=${branchNameContainerImage} PROJECT=${project} DOCKER_REGISTRY=${config.dockerRegistry} ARTIFACTORY_TOKEN=${ARTIFACTORY_TOKEN} SOURCE_REPOSITORY_URL=${config.sourceRepositoryURL} SOURCE_REPOSITORY_BRANCH=${config.sourceRepositoryBranch} NPM_MIRROR=${config.npmMirror} NODEJS_VERSION=${config.nodejsVersion} envLabel=${config.environment} HOST_NAME=${hostname} MIN_POD_REPLICAS=${minimumPodReplicas} MAX_POD_REPLICAS=${maximumPodReplicas} TARGET_PORT=${config.portNumber} NODEJS_PACKAGE_TAG=${config.package_tag} NODEJS_IS_SCOPED_PACKAGE=${config.is_scoped_package}| oc create -n ${projectName} -f -"
        }

        echo "Resources (is,bc,dc,svc,route) created under OCP namespace ${projectName}"
    }
}
