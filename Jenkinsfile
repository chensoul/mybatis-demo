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
            sh "./mvnw -ntp clean -P-webapp"
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
            sh "./mvnw -ntp verify -P-webapp deploy -DskipTests"
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
