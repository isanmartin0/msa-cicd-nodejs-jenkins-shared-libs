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


def String getParallelConfigurationProjectURL(String projectURL, String moduleName) {

    def ppc_extension = "-ppc";
    if (projectURL == null || "".equals(projectURL) || moduleName == null || "".equals(moduleName)) {
        return ""
    } else {
        projectURL.substring(0, projectURL.lastIndexOf("/") + 1) + moduleName + ppc_extension
    }
}


