#!/usr/bin/env groovy
package com.evobanco

def getBranchType(String branchName) {
    //Must be specified according to <flowInitContext> configuration of jgitflow-maven-plugin in pom.xml
    def dev_pattern = "develop"
    def release_pattern = "release/.*"
    def feature_pattern = "feature/.*"
    def hotfix_pattern = "hotfix/.*"
    def master_pattern = "master"

    if (branchName ==~ dev_pattern) {
        return NodejsConstants.BRANCH_TYPE_DEVELOP
    } else if (branchName ==~ release_pattern) {
        return NodejsConstants.BRANCH_TYPE_RELEASE
    } else if (branchName ==~ master_pattern) {
        return NodejsConstants.BRANCH_TYPE_MASTER
    } else if (branchName ==~ feature_pattern) {
        return NodejsConstants.BRANCH_TYPE_FEATURE
    } else if (branchName ==~ hotfix_pattern) {
        return NodejsConstants.BRANCH_TYPE_HOTFIX
    } else {
        return null
    }
}

def getBranch(){
    if (env.BRANCH_NAME){
        return env.BRANCH_NAME
    } else {
        return sh(script: 'git symbolic-ref --short HEAD', returnStdout: true).toString().trim()
    }
}


def String getParallelConfigurationProjectURL(String projectURL) {

    def ppc_extension = "-ppc";
    def parallelProject = ""

    if (projectURL != null && !"".equals(projectURL)) {
        parallelProject = projectURL + ppc_extension
    }

    return parallelProject
}

def boolean isScopedPackage(String packageName) {
    if (packageName == null || "".equals(packageName)) {
        return false
    } else if (packageName.trim().startsWith("@") && packageName.trim().contains("/")) {
        return true
    } else {
        return false
    }
}

def getPackageScope(String packageName) {
    def packageScope = ""
    if (isScopedPackage(packageName)) {
        packageScope = packageName.substring(packageName.indexOf('@'), packageName.indexOf('/'))
    } else {
        throw new Exception("is not a scoped package")
    }
    return packageScope
}

def getPackageTag(String packageName, String packageVersion) {

    def packageTag = ""
    if (packageName == null || "".equals(packageName) || packageVersion == null || "".equals(packageVersion)) {
        return ""
    } else {
        packageTag = packageName.trim() + "@" + packageVersion.trim()
    }
    return packageTag
}

def getPackageTarball(String packageName, String packageVersion) {

    def packageTarball = ""
    def tarballSufix = ".tgz"
    if (packageName == null || "".equals(packageName) || packageVersion == null || "".equals(packageVersion)) {
        return ""
    } else {
        packageTarball = packageName.trim() + "-" + packageVersion.trim() + tarballSufix
    }
    return packageTarball
}


def getPackageTarball(String packageTag) {

    def packageTarball = ""
    def tarballSufix = ".tgz"
    if (packageTag == null || "".equals(packageTag)) {
        return ""
    } else {
        packageTarball = packageTag.trim().replace("@","-") + tarballSufix
    }
    return packageTarball
}

def getRouteHostnameWithProtocol(String routeHostname, boolean isSecuredRoute) {

    def routeHostNameWithProtocol = ''
    if (routeHostname != null && !"".equals(routeHostname)) {
        if (!routeHostname.toLowerCase().startsWith(NodejsConstants.HTTP_PROTOCOL) && !routeHostname.toLowerCase().startsWith(NodejsConstants.HTTPS_PROTOCOL)) {
            if (isSecuredRoute) {
                routeHostNameWithProtocol = NodejsConstants.HTTPS_PROTOCOL + routeHostname
            } else {
                routeHostNameWithProtocol = NodejsConstants.HTTP_PROTOCOL + routeHostname
            }
        } else {
            routeHostNameWithProtocol = routeHostname
        }
    }

    return routeHostNameWithProtocol
}