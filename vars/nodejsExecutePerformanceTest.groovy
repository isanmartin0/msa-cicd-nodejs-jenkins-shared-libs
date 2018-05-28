#!/usr/bin/groovy

def call(body) {

    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def taurus_test_base_path = config.pts_taurus_test_base_path
    def acceptance_test_path = config.pts_acceptance_test_path
    def openshift_route_hostname_with_protocol = config.pts_openshift_route_hostname_with_protocol
    def performance_test_type = config.pts_performance_test_type

    echo "executePerformanceTest global variable parameters"
    echo "taurus_test_base_path: ${taurus_test_base_path}"
    echo "acceptance_test_path: ${acceptance_test_path}"
    echo "openshift_route_hostname_with_protocol: ${openshift_route_hostname_with_protocol}"
    echo "performance_test_type: ${performance_test_type}"

    def isErrorTestStage = false
    checkout scm
    echo "Running ${performance_test_type} tests..."

    def test_files_location = taurus_test_base_path + acceptance_test_path + '**/*.yml'
    echo "Searching ${performance_test_type} tests with pattern: ${test_files_location}"

    //TODO: Complete global variable
}
