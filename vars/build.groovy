#!/usr/bin/env groovy

def sonarqubeMaven()
{
    withSonarQubeEnv('sonarqube')
    {
      sh '''mvn clean verify sonar:sonar'''
    }
}

def buildMaven()
{
    sh 'mvn package -DskipTests=true'
}

def buildNode()
{
    sh 'sudo npm install'
    sh 'sudo npm run build'
}

def buildAngular()
{
    sh '''sudo npm install npm@7.24.0 --legacy-peer-deps
    sudo npm run build'''
}

def dockerImage()
{
    withCredentials([[$class: 'AmazonWebServicesCredentialsBinding',
    credentialsId: "ng-aws-cred",
    accessKeyVariable: 'AWS_ACCESS_KEY_ID',
    secretKeyVariable: 'AWS_SECRET_ACCESS_KEY']])
    {
        sh 'docker context use default'
        app = docker.build("${ECR_NAME}")
    }
}

def dockerPush()
{
    docker.withRegistry("https://${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_NAME}", "ecr:${AWS_REGION}:${AWS_ACCOUNT_ID}") {
        app.push("${GIT_COMMIT}")
        app.push("latest")
    }
}