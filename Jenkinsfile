#!/usr/bin/env groovy

node {
    stage('checkout') {
        checkout scm
    }

    docker.image('jhipster/jhipster:v8.7.3').inside('-u jhipster -e MAVEN_OPTS="-Duser.home=./"') {
        stage('check java') {
            sh "java -version"
        }

        stage('clean') {
            sh "chmod +x mvnw"
            sh "./mvnw -ntp clean"
        }
        stage('nohttp') {
            sh "./mvnw -ntp checkstyle:check"
        }

        stage('Install Snyk CLI') {
           sh """
               curl -Lo ./snyk $(curl -s https://api.github.com/repos/snyk/snyk/releases/latest | grep "browser_download_url.*snyk-linux" | cut -d ':' -f 2,3 | tr -d \" | tr -d ' ')
               chmod +x snyk
           """
        }
        stage('Snyk test') {
           sh './snyk test --all-projects'
        }
        stage('Snyk monitor') {
           sh './snyk monitor --all-projects'
        }
        stage('backend tests') {
            try {
                sh "./mvnw -ntp verify"
            } catch(err) {
                throw err
            } finally {
                junit '**/target/surefire-reports/TEST-*.xml,**/target/failsafe-reports/TEST-*.xml'
            }
        }

        stage('packaging') {
            sh "./mvnw -ntp verify deploy -DskipTests"
            archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
        }
        stage('quality analysis') {
            withSonarQubeEnv('') {
                sh "./mvnw -ntp initialize sonar:sonar"
            }
        }
    }

    def dockerImage
    stage('publish docker') {
        // A pre-requisite to this step is to setup authentication to the docker registry
        // https://github.com/GoogleContainerTools/jib/tree/master/jib-maven-plugin#authentication-methods
        sh "./mvnw -ntp verify jib:build"
    }
}
