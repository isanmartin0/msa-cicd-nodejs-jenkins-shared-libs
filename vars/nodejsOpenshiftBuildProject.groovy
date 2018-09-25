#!/usr/bin/groovy
import com.evobanco.NodejsConstants
import com.evobanco.NodejsUtils

def call(body) {

    def utils = new com.evobanco.NodejsUtils()
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    echo "nodejsOpenshiftBuildProject parameters"

    echo "config.branch_type:  ${config.branch_type}"
    echo "config.branchHY:  ${config.branchHY}"
    echo "config.devModeOpenshift:  ${config.devModeOpenshift}"
    echo "config.debugPortOpenshift:  ${config.debugPortOpenshift}"
    echo "config.useNpmMirrorOpenshift:  ${config.useNpmMirrorOpenshift}"
    echo "config.npmMirrorOpenshift:  ${config.npmMirrorOpenshift}"
    echo "config.useAlternateNpmRunOpenshift:  ${config.useAlternateNpmRunOpenshift}"
    echo "config.alternateNpmRunScriptOpenshift:  ${config.alternateNpmRunScriptOpenshift}"
    echo "config.package_tag:  ${config.package_tag}"
    echo "config.package_tarball:  ${config.package_tarball}"
    echo "config.is_scoped_package:  ${config.is_scoped_package}"



    Boolean isDevMode = false
    Boolean isUseNpmMirror = false
    Boolean isUseAlternateNpmRunScript = false
    Boolean isScopedPackage = false

    def devMode = config.devModeOpenshift
    def debugPort = config.debugPortOpenshift
    def useNpmMirror = config.useNpmMirrorOpenshift
    def npmMirror = config.npmMirrorOpenshift
    def useAlternateNpmRun = config.useAlternateNpmRunOpenshift
    def alternateNpmRunScript = config.alternateNpmRunScriptOpenshift
    def scopedPackage = config.is_scoped_package

    def packageJSON = readJSON file: 'package.json'
    def project = utils.getProject(packageJSON.name)
    def projectName = utils.getProjectName(packageJSON.name, config.branch_type, config.branchHY)

    echo "project: ${project}"
    echo "projectName: ${projectName}"

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

    if (scopedPackage != null) {
        isScopedPackage = scopedPackage.toBoolean()
    }

    //Setting build environment variables
    def buildEnvVars = []


    echo "buildEnvVars.size: ${buildEnvVars.size()}"

    buildEnvVars <<  [ name : NodejsConstants.NODEJS_PACKAGE_TAG_VARIABLE, value : config.package_tag ]
    buildEnvVars <<  [ name : NodejsConstants.NODEJS_PACKAGE_TARBALL_VARIABLE, value : config.package_tarball ]
    buildEnvVars <<  [ name : NodejsConstants.NODEJS_IS_SCOPED_PACKAGE_VARIABLE, value : config.is_scoped_package ]

    if (isDevMode) {
        buildEnvVars <<  [ name : NodejsConstants.DEV_MODE_ENVIRONMENT_VARIABLE, value : isDevMode ]
        buildEnvVars <<  [ name : NodejsConstants.DEBUG_PORT_ENVIRONMENT_VARIABLE, value : debugPort ]
    }

    if (isUseNpmMirror) {
        buildEnvVars <<  [ name : NodejsConstants.NPM_MIRROR_ENVIRONMENT_VARIABLE, value : npmMirror ]
    }

    if (isUseAlternateNpmRunScript) {
        buildEnvVars <<  [ name : NodejsConstants.NPM_RUN_ENVIRONMENT_VARIABLE, value : alternateNpmRunScript ]
    }

    echo "buildEnvVars.size: ${buildEnvVars.size()}"

    openshiftBuild buildConfig: project, namespace: projectName, verbose: 'true', showBuildLogs: 'true', env: buildEnvVars

    def destTag = "${project}-${version}"

    openshiftTag(namespace: projectName, sourceStream: project, sourceTag: 'latest', destinationStream: project, destinationTag: destTag)
}
