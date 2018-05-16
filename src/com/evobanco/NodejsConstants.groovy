#!/usr/bin/env groovy
package com.evobanco

class NodejsConstants {

    static final BRANCH_TYPE_DEVELOP = 'develop'
    static final BRANCH_TYPE_FEATURE = 'feature'
    static final BRANCH_TYPE_RELEASE = 'release'
    static final BRANCH_TYPE_HOTFIX = 'hotfix'
    static final BRANCH_TYPE_MASTER = 'master'
    static final SNAPSHOT_SUFIX = '-SNAPSHOT'
    static final RELEASE_SUFIX = '-RELEASE'

    static final HTTP_STATUS_CODE_OK = '200'
    static final HOSTNAME_DOMAIN = '.grupoevo.corp'

    static final int MINIMUM_POD_REPLICAS = 1
    static final int MINIMUM_POD_REPLICAS_PRO_ENVIRONMENT = 2
    static final int MAXIMUM_POD_REPLICAS = 2
    static final int MAXIMUM_POD_REPLICAS_PRO_ENVIRONMENT = 4

    static final HTTP_PROTOCOL = 'http://'
    static final HTTPS_PROTOCOL = 'https://'


    static final SUCCESS_BUILD_RESULT = 'SUCCESS'
    static final FAILURE_BUILD_RESULT = 'FAILURE'
    static final UNSTABLE_BUILD_RESULT = 'UNSTABLE'




}